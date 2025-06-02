package com.konkitoman.ssanticheat.common.xray.visibile_check;

import com.konkitoman.ssanticheat.common.ConfigIN;
import com.konkitoman.ssanticheat.common.ConfigOUT;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class VisibleCheckModeLIGHT implements XRay.VisibleCheck {
    @Override
    public boolean isVisible(LevelChunk chunk, int x, int y, int z, LevelChunkSection chunkSection, int sectionIndex) {
        int offset = chunk.getSectionYFromSectionIndex(sectionIndex) * 16;
        ChunkPos pos = chunk.getPos();
        return chunk.getLevel().getRawBrightness(pos.getBlockAt(x, offset + y + 1, z), 0) > 0
                || chunk.getLevel().getRawBrightness(pos.getBlockAt(x, offset + y - 1, z), 0) > 0
                || chunk.getLevel().getRawBrightness(pos.getBlockAt(x + 1, offset + y, z), 0) > 0
                || chunk.getLevel().getRawBrightness(pos.getBlockAt(x - 1, offset + y, z), 0) > 0
                || chunk.getLevel().getRawBrightness(pos.getBlockAt(x, offset + y, z + 1), 0) > 0
                || chunk.getLevel().getRawBrightness(pos.getBlockAt(x, offset + y, z - 1), 0) > 0;
    }

    @Override
    public void load(ConfigIN in) {

    }

    @Override
    public ConfigOUT save() {
        return new ConfigOUT();
    }
}
