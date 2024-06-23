package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.xray.Xray;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(net.minecraft.server.network.ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandler {
    @ModifyArg(method = "send", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V"))
    public Packet<?> send(Packet<?> packet) {
        if (!Xray.ENABLED) return packet;

        if ((Object) this instanceof ServerPlayNetworkHandler) {
            ServerPlayNetworkHandler pl = (ServerPlayNetworkHandler) ((Object) this);

            BlockPos pos = null;

            if (packet instanceof BlockUpdateS2CPacket) {
                if (!((BlockUpdateS2CPacket) packet).getState().isOpaque()) {
                    return packet;
                }
                pos = ((BlockUpdateS2CPacket) packet).getPos();
            }

            if (pos == null) {
                return packet;
            }
            ServerWorld world = pl.player.getServerWorld();
            WorldChunk chunk = world.getWorldChunk(pos);

            BlockPos p = pos.subtract(chunk.getPos().getStartPos());

            int section_index = chunk.getSectionIndex(p.getY());
            int x = p.getX();
            int z = p.getZ();
            p = p.add(-p.getX(), -p.getY(), -p.getZ());
            p = p.add(x, (pos.getY() - (16 * chunk.sectionIndexToCoord(section_index))), z);

            if (Xray.isVisible(chunk, p.getX(), p.getY(), p.getZ(), chunk.getSection(section_index), section_index)) {
                return packet;
            }

            if (packet instanceof BlockUpdateS2CPacket) {
                packet = new BlockUpdateS2CPacket(((BlockUpdateS2CPacket) packet).getPos(), Xray.shadowBlock().getDefaultState());
                return packet;
            }
        }

        return packet;
    }
}
