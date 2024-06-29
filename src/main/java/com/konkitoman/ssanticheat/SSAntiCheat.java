package com.konkitoman.ssanticheat;

import com.konkitoman.ssanticheat.xray.XRay;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
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

public class SSAntiCheat implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ssanticheat");
    public static MinecraftServer SERVER = null;
    public static Config CONFIG;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Konkito Man SSAntiCheat was loaded!");

        XRay.Initialize();

        ServerLifecycleEvents.SERVER_STARTING.register(SSAntiCheat::load);
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, e) -> {
            load(server);
        });

        ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> {
            save(server);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("ssanticheat")
                    .then(XRay.commandRegister(registryAccess).requires(source -> source.hasPermissionLevel(2)))

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("enable").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (!XRay.isEnable()) XRay.setEnable(true);
                                SSAntiCheat.save(SERVER);
                                return 1;
                            }))
                    )

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("disable").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (XRay.isEnable()) XRay.setEnable(false);
                                SSAntiCheat.save(SERVER);
                                return 1;
                            }))
                    )

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("status").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (XRay.isEnable()) {
                                    ctx.getSource().sendMessage(Text.literal("Xray: online"));
                                } else {
                                    ctx.getSource().sendMessage(Text.literal("Xray: offline"));
                                }
                                SSAntiCheat.save(SERVER);
                                return 1;
                            }))
                    )
            );
        });
    }

    public static void save(MinecraftServer server) {
        LOGGER.info("Saving");
        try {

            new Dump(DumpSettings.builder().setMultiLineFlow(true).setDefaultFlowStyle(FlowStyle.BLOCK).setSchema(new CoreSchema()).build()).dump(CONFIG.save().map, new YamlOutputStreamWriter(new FileOutputStream(server.getPath("config/ssanticheat.yaml").toFile()), Charset.defaultCharset()) {
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

    static void load(MinecraftServer server) {
        Path config_path = server.getPath("config");
        try {
            if (!Files.exists(config_path)) {
                Files.createDirectory(config_path);
            }

            Path path = server.getPath("config/ssanticheat.yaml");
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
