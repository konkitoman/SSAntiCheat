package com.konkitoman.ssanticheat.common.xray;

import com.konkitoman.ssanticheat.common.ConfigIN;
import com.konkitoman.ssanticheat.common.ConfigOUT;
import com.konkitoman.ssanticheat.common.IConfig;
import com.konkitoman.ssanticheat.common.SSAntiCheat;
import com.konkitoman.ssanticheat.common.xray.visibile_check.VisibleCheckModeLIGHT;
import com.konkitoman.ssanticheat.common.xray.visibile_check.VisibleCheckModeVISIBLE;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Random;

import java.util.ArrayList;
import java.util.HashMap;

import static com.konkitoman.ssanticheat.common.SSAntiCheat.CONFIG;
import static com.konkitoman.ssanticheat.common.SSAntiCheat.SERVER;

public class XRay {
    public record PlayerBlockUpdate(ServerPlayer player, BlockPos pos) {
    }

    static HashMap<ResourceKey<Level>, ArrayList<PlayerBlockUpdate>> UPDATES = new HashMap<>();
    static CommandBuildContext REGISTRY_ACCESS = null;

    public static void Initialize() {

    }

    public static void onServerTick() {
        UPDATES.forEach((key, values) -> {
            ServerLevel world = SERVER.getLevel(key);
            if (world == null) {
                return;
            }

            for (PlayerBlockUpdate update : values) {
                int size = 15;
                for (int y = -size; y < size; y++) {
                    for (int x = -size; x < size; x++) {
                        for (int z = -size; z <= size; z++) {
                            double distanceFromCenter = Math.sqrt(x * x + y * y + z * z);
                            if (distanceFromCenter <= size) {
                                try {
                                    world.getChunkSource().blockChanged(update.pos.offset(x, y, z));
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            }
            values.clear();
        });

        UPDATES.clear();
    }

    public static void onNeighborNotify(ServerLevel level, BlockPos pos) {
        CONFIG.xray.mode.onNeighborNotify(level, pos);
    }

    public static boolean isEnable() {
        return SSAntiCheat.CONFIG.xray.enable;
    }

    public static void setEnable(boolean value) {
        SSAntiCheat.CONFIG.xray.enable = value;
    }

    public static void addPlayerBlockUpdate(ServerLevel world, ServerPlayer player, BlockPos pos) {
        if (!UPDATES.containsKey(world.dimension())) {
            UPDATES.put(world.dimension(), new ArrayList<>());
        }
        UPDATES.get(world.dimension()).add(new PlayerBlockUpdate(player, pos));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> commandRegister(CommandBuildContext registryAccess) {
        REGISTRY_ACCESS = registryAccess;
        return LiteralArgumentBuilder.<CommandSourceStack>literal("xray").then(LiteralArgumentBuilder.<CommandSourceStack>literal("set_mode").then(LiteralArgumentBuilder.<CommandSourceStack>literal("visible").executes(ctx -> {
                    SSAntiCheat.CONFIG.xray.mode = new VisibleCheckModeVISIBLE();
                    ctx.getSource().sendSystemMessage(Component.literal("The Xray mode was set to visible"));
                    SSAntiCheat.save(SERVER);
                    return 1;
                })).then(LiteralArgumentBuilder.<CommandSourceStack>literal("light").executes(ctx -> {
                    SSAntiCheat.CONFIG.xray.mode = new VisibleCheckModeLIGHT();
                    ctx.getSource().sendSystemMessage(Component.literal("The Xray mode was set to light"));
                    SSAntiCheat.save(SERVER);
                    return 1;
                })))

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("get_mode").executes(ctx -> {
                    if (SSAntiCheat.CONFIG.xray.mode instanceof VisibleCheckModeLIGHT) {
                        ctx.getSource().sendSystemMessage(Component.literal("Xray mode: light"));
                    } else if (SSAntiCheat.CONFIG.xray.mode instanceof VisibleCheckModeVISIBLE) {
                        ctx.getSource().sendSystemMessage(Component.literal("Xray mode: visible"));
                    }
                    SSAntiCheat.save(SERVER);
                    return 1;
                }))

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set_shadow").then(LiteralArgumentBuilder.<CommandSourceStack>literal("random").then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("blocks", StringArgumentType.greedyString()).executes(ctx -> {
                            String blocks_text = ctx.getArgument("blocks", String.class);

                            try {
                                ArrayList<Block> blocks = new ArrayList<>();
                                for (String block_text : blocks_text.split(",")) {
                                    StringReader sr = new StringReader(block_text);

                                    blocks.add(new BlockStateArgument(registryAccess).parse(sr).getState().getBlock());
                                }

                                ctx.getSource().sendSystemMessage(Component.literal("Setting shadow with blocks:"));
                                ctx.getSource().sendSystemMessage(Component.literal(blocks.toString()));

                                for (Block block : blocks) {
                                    if (!block.defaultBlockState().canOcclude()) {
                                        ctx.getSource().sendSystemMessage(Component.literal("Block needs to be opaque Block:"));
                                        ctx.getSource().sendSystemMessage(Component.literal(block.toString()));
                                        SSAntiCheat.save(SERVER);
                                        return 0;
                                    }
                                }
                                SSAntiCheat.CONFIG.xray.shadow = new ShadowBlockRandom(blocks);

                            } catch (CommandSyntaxException e) {
                                ctx.getSource().sendSystemMessage(Component.literal(e.toString()));
                            }

                            SSAntiCheat.save(SERVER);
                            return 1;
                        })

                ).executes(ctx -> {
                    ShadowBlockRandom getShadow = new ShadowBlockRandom();
                    ctx.getSource().sendSystemMessage(Component.literal("Setting shadow with blocks:"));
                    ctx.getSource().sendSystemMessage(Component.literal(getShadow.blocks.toString()));
                    SSAntiCheat.CONFIG.xray.shadow = getShadow;
                    SSAntiCheat.save(SERVER);
                    return 1;
                })).then(LiteralArgumentBuilder.<CommandSourceStack>literal("solid").then(RequiredArgumentBuilder.<CommandSourceStack, BlockInput>argument("block", new BlockStateArgument(registryAccess)).executes(ctx -> {
                    BlockInput blockStateA = ctx.getArgument("block", BlockInput.class);
                    Block block = blockStateA.getState().getBlock();
                    ctx.getSource().sendSystemMessage(Component.literal("Setting shadow as block:"));
                    ctx.getSource().sendSystemMessage(Component.literal(block.toString()));
                    if (!block.defaultBlockState().canOcclude()) {
                        ctx.getSource().sendSystemMessage(Component.literal("Block needs to be opaque Block:"));
                        ctx.getSource().sendSystemMessage(Component.literal(block.toString()));
                        SSAntiCheat.save(SERVER);
                        return 0;
                    }
                    SSAntiCheat.CONFIG.xray.shadow = new ShadowBlockSolid(block);
                    SSAntiCheat.save(SERVER);
                    return 1;
                }))));
    }

    public static boolean isVisible(LevelChunk chunk, Vec3i pos, int sectionIndex) {
        return SSAntiCheat.CONFIG.xray.mode.isVisible(chunk, pos, sectionIndex);
    }

    public static Block shadowBlock() {
        return SSAntiCheat.CONFIG.xray.shadow.getBlock();
    }

    public interface VisibleCheck extends IConfig {
        boolean isVisible(LevelChunk chunk, Vec3i pos, int sectionIndex);

        void onNeighborNotify(ServerLevel level, BlockPos pos);
    }

    public interface ShadowBlock extends IConfig {
        Block getBlock();
    }

    public static class ShadowBlockRandom implements ShadowBlock {
        public Random rng = new Random(2142532);

        public ArrayList<Block> blocks = new ArrayList<>();

        public ShadowBlockRandom() {
            blocks.add(Blocks.DIAMOND_ORE);
            blocks.add(Blocks.COAL_ORE);
            blocks.add(Blocks.COPPER_ORE);
            blocks.add(Blocks.GOLD_ORE);
            blocks.add(Blocks.IRON_ORE);
            blocks.add(Blocks.REDSTONE_ORE);
            blocks.add(Blocks.EMERALD_ORE);
            blocks.add(Blocks.LAPIS_ORE);
            blocks.add(Blocks.DEEPSLATE_COAL_ORE);
            blocks.add(Blocks.DEEPSLATE_DIAMOND_ORE);
            blocks.add(Blocks.DEEPSLATE_COPPER_ORE);
            blocks.add(Blocks.DEEPSLATE_EMERALD_ORE);
            blocks.add(Blocks.DEEPSLATE_GOLD_ORE);
            blocks.add(Blocks.DEEPSLATE_IRON_ORE);
            blocks.add(Blocks.DEEPSLATE_LAPIS_ORE);
            blocks.add(Blocks.DEEPSLATE_REDSTONE_ORE);
            blocks.add(Blocks.NETHER_QUARTZ_ORE);
            blocks.add(Blocks.ANCIENT_DEBRIS);
        }

        public ShadowBlockRandom(ArrayList<Block> blocks) {
            this.blocks = blocks;
        }

        @Override
        public Block getBlock() {
            int index = rng.nextInt(blocks.size() - 1);
            return blocks.get(index);
        }

        @Override
        public void load(ConfigIN in) {
            in.readStringList("blocks").ifPresent(blocks_strings -> {
                ArrayList<Block> blocks = new ArrayList<>();
                for (String block_string : blocks_strings) {
                    StringReader sr = new StringReader(block_string);
                    try {
                        blocks.add(new BlockStateArgument(REGISTRY_ACCESS).parse(sr).getState().getBlock());
                    } catch (CommandSyntaxException e) {
                        SSAntiCheat.LOGGER.info("Cannot parse block: {}", block_string);
                    }
                }

                this.blocks = blocks;
            });
        }

        @Override
        public ConfigOUT save() {
            ConfigOUT out = new ConfigOUT();

            ArrayList<String> blocks = new ArrayList<>();
            for (Block block : this.blocks) {
                blocks.add(ForgeRegistries.BLOCKS.getKey(block).toString());
            }

            out.writeStringList("blocks", blocks);

            return out;
        }
    }

    public static class ShadowBlockSolid implements ShadowBlock {
        Block block;

        public ShadowBlockSolid(Block block) {
            this.block = block;
        }

        @Override
        public Block getBlock() {
            return block;
        }

        @Override
        public void load(ConfigIN in) {
            in.readString("block").ifPresent(block_string -> {
                StringReader sr = new StringReader(block_string);
                try {
                    block = new BlockStateArgument(REGISTRY_ACCESS).parse(sr).getState().getBlock();
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public ConfigOUT save() {
            ConfigOUT out = new ConfigOUT();

            out.writeString("block", ForgeRegistries.BLOCKS.getKey(block).toString());

            return out;
        }
    }
}
