package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.common.xray.ChunkDataState;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ClientboundLevelChunkPacketData.class)
public abstract class ChunkDataSender {
    @Shadow
    private static int calculateChunkSize(LevelChunk p_195665_) {
        throw new AssertionError();
    }

    @Unique
    ArrayList<ChunkDataState> sSAntiCheat$toRestore = null;

    @Redirect(method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData;calculateChunkSize(Lnet/minecraft/world/level/chunk/LevelChunk;)I"))
    private int ChunkDataBegin(LevelChunk levelchunksection) {
        if (!XRay.isEnable()) return calculateChunkSize(levelchunksection);
        sSAntiCheat$toRestore = new ArrayList<>();
        int i = 0;
        for (LevelChunkSection section : levelchunksection.getSections()) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (XRay.isVisible(levelchunksection, new Vec3i(x, y, z), i)) continue;

                        sSAntiCheat$toRestore.add(new ChunkDataState(state, i, x, y, z));
                        section.setBlockState(x, y, z, XRay.shadowBlock().defaultBlockState());

                    }
                }
            }
            i += 1;
        }
        return calculateChunkSize(levelchunksection);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At(value = "TAIL"))
    private void ChunkDataEnd(LevelChunk chunk, CallbackInfo ci) {
        if (sSAntiCheat$toRestore == null) return;
        sSAntiCheat$toRestore.forEach(state -> {
            chunk.getSection(state.section()).setBlockState(state.x(), state.y(), state.z(), state.state());
        });
    }
}
