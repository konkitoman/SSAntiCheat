package com.konkitoman.ssanticheat;

import com.konkitoman.ssanticheat.xray.Xray;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSAntiCheat implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ssanticheat");
    public static MinecraftServer SERVER = null;

    @Override
    public void onInitializeServer() {
        LOGGER.info("Konkito Man SSAntiCheat was loaded!");


        Xray.Initialize();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("ssanticheat")
                    .then(Xray.commandRegister(registryAccess).requires(source -> source.hasPermissionLevel(2)))

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("enable").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (!Xray.isEnable()) Xray.setEnable(true);
                                return 1;
                            }))
                    )

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("disable").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (Xray.isEnable()) Xray.setEnable(false);
                                return 1;
                            }))
                    )

                    .then(LiteralArgumentBuilder.<ServerCommandSource>literal("status").requires(source -> source.hasPermissionLevel(2))
                            .then(LiteralArgumentBuilder.<ServerCommandSource>literal("xray").executes(ctx -> {
                                if (Xray.isEnable()) {
                                    ctx.getSource().sendMessage(Text.literal("Xray: online"));
                                } else {
                                    ctx.getSource().sendMessage(Text.literal("Xray: offline"));
                                }
                                return 1;
                            }))
                    )
            );
        });
    }
}
