package com.konkitoman.ssanticheat;

import com.konkitoman.ssanticheat.common.SSAntiCheat;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ForgeSSAntiCheat.MODID)
@Mod.EventBusSubscriber(modid = ForgeSSAntiCheat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ForgeSSAntiCheat {
    public static final String MODID = "ssanticheat";

    public ForgeSSAntiCheat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SSAntiCheat.onInitializeServer();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        SSAntiCheat.load(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        SSAntiCheat.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        SSAntiCheat.onServerTick();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SSAntiCheat.onRegisterCommands(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public void onNeighborNotifyEvent(BlockEvent.NeighborNotifyEvent event) {
        SSAntiCheat.onNeighborNotify((ServerLevel) event.getLevel(), event.getPos());
    }
}
