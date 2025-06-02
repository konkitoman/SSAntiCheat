package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.common.SSAntiCheat;
import com.konkitoman.ssanticheat.common.xray.XRay;
import com.konkitoman.ssanticheat.common.xray.visibile_check.VisibleCheckModeLIGHT;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(net.minecraft.world.level.Level.class)
public abstract class ServerPlayNetworkHandler {
    @Shadow
    public abstract ResourceKey<Level> dimension();

    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    public void onPlayerInteractBlock(BlockPos p_46605_, BlockState p_46606_, int p_46607_, int p_46608_, CallbackInfoReturnable<Boolean> cir) {
        if (!XRay.isEnable()) return;

        for (BlockPos pos : List.of(p_46605_.above(), p_46605_.below(), p_46605_.north(), p_46605_.east(), p_46605_.south(), p_46605_.west())) {
            ServerLevel level = SSAntiCheat.SERVER.getLevel(dimension());
            LevelChunk chunk = level.getChunkAt(pos);
            for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false)) {
                level.getChunkSource().broadcastAndSend(player, new ClientboundBlockUpdatePacket(level, pos));
            }
        }

    }
}

