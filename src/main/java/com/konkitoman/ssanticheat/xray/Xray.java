package com.konkitoman.ssanticheat.xray;

import com.konkitoman.ssanticheat.SSAntiCheat;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeLIGHT;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeVISIBLE;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.HashMap;

public class Xray {
    public record PlayerBlockUpdate(ServerPlayerEntity player, BlockPos pos) {
    }

    public static boolean ENABLED = true;
    public static VisibleCheck visibleCheck = new VisibleCheckModeLIGHT();
    public static GetShadowBlock getShadowBlock = new GetShadowBlockRandom();

    static HashMap<RegistryKey<World>, ArrayList<PlayerBlockUpdate>> UPDATES = new HashMap<>();

    public static void Initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            UPDATES.forEach((key, values) -> {
                ServerWorld world = server.getWorld(key);
                SSAntiCheat.LOGGER.info("Update");
                for (PlayerBlockUpdate update : values) {
                    int size = 15;
                    for (int y = -size; y < size; y++) {
                        for (int x = -size; x < size; x++) {
                            for (int z = -size; z <= size; z++) {
                                double distanceFromCenter = Math.sqrt(x * x + y * y + z * z);
                                if (distanceFromCenter <= size) {
                                    world.getChunkManager().sendToNearbyPlayers(update.player, new BlockUpdateS2CPacket(world, update.pos.add(x, y, z)));
                                }
                            }
                        }
                    }
                }
                values.clear();
            });

            UPDATES.clear();
        });
    }


    public static boolean isEnable() {
        return ENABLED;
    }

    public static void setEnable(boolean value) {
        ENABLED = value;
    }

    public static void addPlayerBlockUpdate(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        if (!UPDATES.containsKey(world.getRegistryKey())) {
            UPDATES.put(world.getRegistryKey(), new ArrayList<>());
        }

        UPDATES.get(world.getRegistryKey()).add(new PlayerBlockUpdate(player, pos));
    }

    public static LiteralArgumentBuilder<ServerCommandSource> commandRegister(CommandRegistryAccess registryAccess) {
        return LiteralArgumentBuilder.<ServerCommandSource>literal("xray")
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("set_mode")
                        .then(LiteralArgumentBuilder.<ServerCommandSource>literal("visible").executes(ctx -> {
                                    visibleCheck = new VisibleCheckModeVISIBLE();
                                    ctx.getSource().sendMessage(Text.literal("The Xray mode was set to visible"));
                                    return 1;
                                })
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>literal("light").executes(ctx -> {
                                    visibleCheck = new VisibleCheckModeLIGHT();
                                    ctx.getSource().sendMessage(Text.literal("The Xray mode was set to light"));
                                    return 1;
                                })
                        )
                )

                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("get_mode")
                        .executes(ctx -> {
                            if (visibleCheck instanceof VisibleCheckModeLIGHT) {
                                ctx.getSource().sendMessage(Text.literal("Xray mode: light"));
                            } else if (visibleCheck instanceof VisibleCheckModeVISIBLE) {
                                ctx.getSource().sendMessage(Text.literal("Xray mode: visible"));
                            }
                            return 1;
                        })
                )

                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("set_shadow")
                        .then(LiteralArgumentBuilder.<ServerCommandSource>literal("random")
                                .then(RequiredArgumentBuilder.<ServerCommandSource, String>argument("blocks", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                                    String blocks_text = ctx.getArgument("blocks", String.class);

                                                    try {
                                                        ArrayList<Block> blocks = new ArrayList<>();
                                                        for (String block_text : blocks_text.split(",")) {
                                                            StringReader sr = new StringReader(block_text);
                                                            blocks.add(BlockStateArgumentType.blockState(registryAccess).parse(sr).getBlockState().getBlock());
                                                        }

                                                        ctx.getSource().sendMessage(Text.literal("Setting shadow with blocks:"));
                                                        ctx.getSource().sendMessage(Text.literal(blocks.toString()));

                                                        for (Block block : blocks) {
                                                            if (!block.getDefaultState().isOpaque()) {
                                                                ctx.getSource().sendMessage(Text.literal("Block needs to be opaque Block:"));
                                                                ctx.getSource().sendMessage(Text.literal(block.toString()));
                                                                return 0;
                                                            }
                                                        }
                                                        Xray.getShadowBlock = new GetShadowBlockRandom(blocks);

                                                    } catch (CommandSyntaxException e) {
                                                        ctx.getSource().sendMessage(Text.literal(e.toString()));
                                                    }

                                                    return 1;
                                                }
                                        )

                                ).executes(ctx -> {
                                            GetShadowBlockRandom getShadow = new GetShadowBlockRandom();
                                            ctx.getSource().sendMessage(Text.literal("Setting shadow with blocks:"));
                                            ctx.getSource().sendMessage(Text.literal(getShadow.blocks.toString()));
                                            getShadowBlock = getShadow;
                                            return 1;
                                        }
                                )
                        )
                        .then(LiteralArgumentBuilder.<ServerCommandSource>literal("solid").then(RequiredArgumentBuilder.<ServerCommandSource, BlockStateArgument>argument("block", BlockStateArgumentType.blockState(registryAccess)).executes(ctx -> {
                            BlockStateArgument blockStateA = ctx.getArgument("block", BlockStateArgument.class);
                            Block block = blockStateA.getBlockState().getBlock();
                            ctx.getSource().sendMessage(Text.literal("Setting shadow as block:"));
                            ctx.getSource().sendMessage(Text.literal(block.toString()));
                            if (!block.getDefaultState().isOpaque()) {
                                ctx.getSource().sendMessage(Text.literal("Block needs to be opaque Block:"));
                                ctx.getSource().sendMessage(Text.literal(block.toString()));
                                return 0;
                            }
                            Xray.getShadowBlock = new GetShadowBlockStatic(block);
                            return 1;
                        })))
                );
    }

    public static boolean isVisible(WorldChunk chunk, int x, int y, int z, ChunkSection chunkSection, int sectionIndex) {
        return visibleCheck.isVisible(chunk, x, y, z, chunkSection, sectionIndex);
    }

    public static Block shadowBlock() {
        return getShadowBlock.getBlock();
    }

    public interface VisibleCheck {
        boolean isVisible(WorldChunk chunk, int x, int y, int z, ChunkSection chunkSection, int sectionIndex);
    }

    public interface GetShadowBlock {
        Block getBlock();
    }

    public static class GetShadowBlockRandom implements GetShadowBlock {
        public Random rng = Random.create(2142532);

        public ArrayList<Block> blocks = new ArrayList<>();

        public GetShadowBlockRandom() {
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

        public GetShadowBlockRandom(ArrayList<Block> blocks) {
            this.blocks = blocks;
        }

        @Override
        public Block getBlock() {
            int index = rng.nextBetween(0, blocks.size() - 1);
            return blocks.get(index);
        }
    }

    public static class GetShadowBlockStatic implements GetShadowBlock {
        Block block;

        public GetShadowBlockStatic(Block block) {
            this.block = block;
        }

        @Override
        public Block getBlock() {
            return block;
        }
    }
}
