package com.konkitoman.ssanticheat.xray.visibile_check;

import com.konkitoman.ssanticheat.ConfigIN;
import com.konkitoman.ssanticheat.ConfigOUT;
import com.konkitoman.ssanticheat.xray.XRay;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class VisibleCheckModeLIGHT implements XRay.VisibleCheck {
    @Override
    public boolean isVisible(WorldChunk chunk, int x, int y, int z, ChunkSection chunkSection, int sectionIndex) {
        int offset = chunk.sectionIndexToCoord(sectionIndex) * 16;
        ChunkPos pos = chunk.getPos();
        return chunk.getWorld().getLightLevel(pos.getBlockPos(x, offset + y + 1, z)) > 0
                || chunk.getWorld().getLightLevel(pos.getBlockPos(x, offset + y - 1, z)) > 0
                || chunk.getWorld().getLightLevel(pos.getBlockPos(x + 1, offset + y, z)) > 0
                || chunk.getWorld().getLightLevel(pos.getBlockPos(x - 1, offset + y, z)) > 0
                || chunk.getWorld().getLightLevel(pos.getBlockPos(x, offset + y, z + 1)) > 0
                || chunk.getWorld().getLightLevel(pos.getBlockPos(x, offset + y, z - 1)) > 0;
    }

    @Override
    public void load(ConfigIN in) {

    }

    @Override
    public ConfigOUT save() {
        return new ConfigOUT();
    }
}
