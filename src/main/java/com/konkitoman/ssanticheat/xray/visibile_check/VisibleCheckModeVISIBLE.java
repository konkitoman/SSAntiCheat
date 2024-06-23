package com.konkitoman.ssanticheat.xray.visibile_check;

import com.konkitoman.ssanticheat.xray.Xray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class VisibleCheckModeVISIBLE implements Xray.VisibleCheck {
    @Override
    public boolean isVisible(WorldChunk chunk, int x, int y, int z, ChunkSection chunkSection, int sectionIndex) {
        int offset = chunk.sectionIndexToCoord(sectionIndex) * 16;
        ChunkPos pos = chunk.getPos();
        return !chunk.getWorld().getBlockState(pos.getBlockPos(x - 1, y + offset, z)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x - 1, y + offset, z))
                || !chunk.getWorld().getBlockState(pos.getBlockPos(x + 1, y + offset, z)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x + 1, y + offset, z))
                || !chunk.getWorld().getBlockState(pos.getBlockPos(x, (y - 1) + offset, z)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x, (y - 1) + offset, z))
                || !chunk.getWorld().getBlockState(pos.getBlockPos(x, (y + 1) + offset, z)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x, (y + 1) + offset, z))
                || !chunk.getWorld().getBlockState(pos.getBlockPos(x, y + offset, z - 1)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x, y + offset, z - 1))
                || !chunk.getWorld().getBlockState(pos.getBlockPos(x, y + offset, z + 1)).isOpaqueFullCube(chunk.getWorld(), pos.getBlockPos(x, y + offset, z + 1));
    }
}
