package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.SSAntiCheat;
import com.konkitoman.ssanticheat.xray.XRay;
import com.konkitoman.ssanticheat.xray.visibile_check.VisibleCheckModeLIGHT;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(net.minecraft.server.network.ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManager {
    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Shadow
    protected ServerWorld world;

    @Inject(method = "onBlockBreakingAction", at = @At("TAIL"))
    private void onBlockBreakingAction(BlockPos pos, boolean success, int sequence, String reason, CallbackInfo ci) {
        if (!XRay.isEnable()) return;

        if (SSAntiCheat.CONFIG.xray.mode instanceof VisibleCheckModeLIGHT) {
            XRay.addPlayerBlockUpdate(world, player, pos);
        } else {
            for (BlockPos poss : List.of(pos.up(), pos.down(), pos.north(), pos.east(), pos.south(), pos.west())) {
                world.getChunkManager().sendToNearbyPlayers(player, new BlockUpdateS2CPacket(world, poss));
            }
        }
    }
}
