package com.konkitoman.ssanticheat.common;

import com.konkitoman.ssanticheat.common.xray.XRay;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.schema.CoreSchema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SSAntiCheat {
    public static final Logger LOGGER = LoggerFactory.getLogger("ssanticheat");
    public static MinecraftServer SERVER = null;
    public static Config CONFIG;

    public static void onInitializeServer() {
        LOGGER.info("Konkito Man SSAntiCheat was loaded!");
        XRay.Initialize();
    }

    public static void onServerStarted(MinecraftServer server) {
        SERVER = server;
    }

    public static void onServerTick() {
        if (XRay.isEnable()) XRay.onServerTick();
    }

    public static void onNeighborNotify(ServerLevel level, BlockPos pos) {
        if (XRay.isEnable()) XRay.onNeighborNotify(level, pos);
    }

    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("ssanticheat")
                .then(XRay.commandRegister(registryAccess).requires(source -> source.hasPermission(2)))

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("enable").requires(source -> source.hasPermission(2))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("xray").executes(ctx -> {
                            if (!XRay.isEnable()) XRay.setEnable(true);
                            SSAntiCheat.save(SERVER);
                            return 1;
                        }))
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("disable").requires(source -> source.hasPermission(2))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("xray").executes(ctx -> {
                            if (XRay.isEnable()) XRay.setEnable(false);
                            SSAntiCheat.save(SERVER);
                            return 1;
                        }))
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("status").requires(source -> source.hasPermission(2))
                        .then(LiteralArgumentBuilder.<CommandSourceStack>literal("xray").executes(ctx -> {
                            if (XRay.isEnable()) {
                                ctx.getSource().sendSystemMessage(Component.literal("Xray: online"));
                            } else {
                                ctx.getSource().sendSystemMessage(Component.literal("Xray: offline"));
                            }
                            SSAntiCheat.save(SERVER);
                            return 1;
                        }))
                )
        );
    }

    public static void save(MinecraftServer server) {
        LOGGER.info("Saving");
        try {
            new Dump(DumpSettings.builder().setMultiLineFlow(true).setDefaultFlowStyle(FlowStyle.BLOCK).setSchema(new CoreSchema()).build()).dump(CONFIG.save().map, new YamlOutputStreamWriter(new FileOutputStream(server.getServerDirectory().toPath().resolve("config/ssanticheat.yaml").toFile()), Charset.defaultCharset()) {
                        @Override
                        public void processIOException(IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(MinecraftServer server) {
        Path config_path = server.getServerDirectory().toPath().resolve("config");
        try {
            if (!Files.exists(config_path)) {
                Files.createDirectory(config_path);
            }

            Path path = config_path.resolve("ssanticheat.yaml");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            LoadSettings configuration = LoadSettings.builder().setLabel("SSAntiCheat config").setUseMarks(true)
                    .build();
            File config_file = path.toFile();

            Object e = new Load(configuration).loadFromReader(new FileReader(config_file));

            LOGGER.info("loading");
            CONFIG = new Config();
            if (e instanceof Map<?, ?>) {
                CONFIG.load(new ConfigIN((Map<String, Object>) e));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
