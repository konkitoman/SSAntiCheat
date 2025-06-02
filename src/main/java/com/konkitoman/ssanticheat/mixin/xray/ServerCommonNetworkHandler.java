package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.common.SSAntiCheat;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.server.network.ServerGamePacketListenerImpl.class)
public abstract class ServerCommonNetworkHandler {
    @ModifyArg(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"))
    public Packet<?> send(Packet<?> packet) {
        if (!XRay.isEnable()) return packet;

        if ((Object) this instanceof ServerGamePacketListenerImpl) {
            ServerGamePacketListenerImpl pl = (ServerGamePacketListenerImpl) ((Object) this);

            BlockPos pos = null;

            if (packet instanceof ClientboundBlockUpdatePacket) {
                if (!((ClientboundBlockUpdatePacket) packet).getBlockState().canOcclude()) {
                    return packet;
                }
                pos = ((ClientboundBlockUpdatePacket) packet).getPos();
            }

            if (pos == null) {
                return packet;
            }
            ServerLevel world = pl.player.serverLevel();
            LevelChunk chunk = world.getChunkAt(pos);
            int section_index = chunk.getSectionIndex(pos.getY());

            BlockPos p = pos.subtract(chunk.getPos().getBlockAt(0, 0, 0));
            p = p.subtract(new Vec3i(0, chunk.getSectionYFromSectionIndex(section_index) * 16, 0));


            if (XRay.isVisible(chunk, p.getX(), p.getY(), p.getZ(), chunk.getSection(section_index), section_index)) {
                return packet;
            }

            if (packet instanceof ClientboundBlockUpdatePacket) {
                packet = new ClientboundBlockUpdatePacket(((ClientboundBlockUpdatePacket) packet).getPos(), XRay.shadowBlock().defaultBlockState());
                return packet;
            }
        }

        return packet;
    }
}
