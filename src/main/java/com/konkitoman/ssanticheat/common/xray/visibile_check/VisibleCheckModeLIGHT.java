package com.konkitoman.ssanticheat.common.xray.visibile_check;

import com.konkitoman.ssanticheat.common.ConfigIN;
import com.konkitoman.ssanticheat.common.ConfigOUT;
import com.konkitoman.ssanticheat.common.SSAntiCheat;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class VisibleCheckModeLIGHT implements XRay.VisibleCheck {
    @Override
    public boolean isVisible(LevelChunk chunk, Vec3i rpos, int sectionIndex) {
        int section_offset = chunk.getSectionYFromSectionIndex(sectionIndex) * 16;
        ChunkPos pos = chunk.getPos();

        for (Vec3i offset : List.of(
                new Vec3i(0, 1, 0),
                new Vec3i(0, -1, 0),
                new Vec3i(1, 0, 0),
                new Vec3i(-1, 0, 0),
                new Vec3i(0, 0, 1),
                new Vec3i(0, 0, -1)
        )) {
            BlockPos p = pos.getBlockAt(rpos.getX() + offset.getX(), section_offset + rpos.getY() + offset.getY(), rpos.getZ() + offset.getZ());
            if (chunk.getLevel().getRawBrightness(p, 0) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNeighborNotify(ServerLevel level, BlockPos pos) {
        for (ServerPlayer player : level.players()) {
            XRay.addPlayerBlockUpdate(level, player, pos);
        }
    }

    @Override
    public void load(ConfigIN in) {

    }

    @Override
    public ConfigOUT save() {
        return new ConfigOUT();
    }
}
