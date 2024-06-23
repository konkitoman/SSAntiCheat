package com.konkitoman.ssanticheat.xray;

import net.minecraft.block.BlockState;

public record ChunkDataState(BlockState state, int section, int x, int y, int z) {
}