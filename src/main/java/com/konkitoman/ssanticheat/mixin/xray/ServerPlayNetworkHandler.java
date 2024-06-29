package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.SSAntiCheat;
import com.konkitoman.ssanticheat.xray.XRay;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeLIGHT;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;


    @Inject(method = "onPlayerInteractBlock", at = @At("TAIL"))
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci, @Local ServerWorld serverWorld, @Local BlockPos blockPos) {
        if (!XRay.isEnable()) return;

        int size = 15;
        if (SSAntiCheat.CONFIG.xray.mode instanceof VisibleCheckModeLIGHT) {
            XRay.addPlayerBlockUpdate(serverWorld, player, blockPos);
        } else {
            for (BlockPos pos : List.of(blockPos.up(), blockPos.down(), blockPos.north(), blockPos.east(), blockPos.south(), blockPos.west())) {
                serverWorld.getChunkManager().sendToNearbyPlayers(player, new BlockUpdateS2CPacket(serverWorld, pos));
            }
        }
    }
}

