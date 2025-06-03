package com.konkitoman.ssanticheat.common.xray.visibile_check;

import com.konkitoman.ssanticheat.common.ConfigIN;
import com.konkitoman.ssanticheat.common.ConfigOUT;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class VisibleCheckModeVISIBLE implements XRay.VisibleCheck {
    @Override
    public boolean isVisible(LevelChunk chunk, Vec3i rpos, int sectionIndex) {
        int x = rpos.getX();
        int y = rpos.getY();
        int z = rpos.getZ();

        int offset = chunk.getSectionYFromSectionIndex(sectionIndex) * 16;
        ChunkPos pos = chunk.getPos();
        return !chunk.getLevel().getBlockState(pos.getBlockAt(x - 1, y + offset, z)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x - 1, y + offset, z))
                || !chunk.getLevel().getBlockState(pos.getBlockAt(x + 1, y + offset, z)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x + 1, y + offset, z))
                || !chunk.getLevel().getBlockState(pos.getBlockAt(x, (y - 1) + offset, z)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x, (y - 1) + offset, z))
                || !chunk.getLevel().getBlockState(pos.getBlockAt(x, (y + 1) + offset, z)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x, (y + 1) + offset, z))
                || !chunk.getLevel().getBlockState(pos.getBlockAt(x, y + offset, z - 1)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x, y + offset, z - 1))
                || !chunk.getLevel().getBlockState(pos.getBlockAt(x, y + offset, z + 1)).isSolidRender(chunk.getLevel(), pos.getBlockAt(x, y + offset, z + 1));
    }

    @Override
    public void onNeighborNotify(ServerLevel level, BlockPos pos) {
    }

    @Override
    public void load(ConfigIN in) {
    }

    @Override
    public ConfigOUT save() {
        return new ConfigOUT();
    }
}
