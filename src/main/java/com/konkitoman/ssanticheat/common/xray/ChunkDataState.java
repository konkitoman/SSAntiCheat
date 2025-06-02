package com.konkitoman.ssanticheat.common.xray;

import net.minecraft.world.level.block.state.BlockState;

public record ChunkDataState(BlockState state, int section, int x, int y, int z) {
}