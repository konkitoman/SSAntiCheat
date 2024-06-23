package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.xray.ChunkDataState;
import com.konkitoman.ssanticheat.xray.Xray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(net.minecraft.network.packet.s2c.play.ChunkData.class)
public class ChunkDataSender {

    @Shadow
    @Final
    private byte[] sectionsData;
    @Unique
    ArrayList<ChunkDataState> toRestore = null;

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getHeightmaps()Ljava/util/Collection;"))
    private void ChunkDataBegin(WorldChunk chunk, CallbackInfo ci) {
        if (!Xray.ENABLED) return;
        toRestore = new ArrayList<>();
        int i = 0;
        for (ChunkSection section : chunk.getSectionArray()) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (state.isOpaque()) {
                            if (Xray.isVisible(chunk, x, y, z, section, i)) continue;

                            toRestore.add(new ChunkDataState(state, i, x, y, z));
                            section.setBlockState(x, y, z, Xray.shadowBlock().getDefaultState());
                        }
                    }
                }
            }
            i += 1;
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;)V", at = @At(value = "TAIL"))
    private void ChunkDataEnd(WorldChunk chunk, CallbackInfo ci) {
        if (toRestore == null) return;
        toRestore.forEach(state -> {
            chunk.getSection(state.section()).setBlockState(state.x(), state.y(), state.z(), state.state());
        });
    }
}
