package com.konkitoman.ssanticheat.mixin.xray;

import com.konkitoman.ssanticheat.common.SSAntiCheat;
import com.konkitoman.ssanticheat.common.xray.XRay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.lang.reflect.Field;

@Mixin(net.minecraft.server.network.ServerGamePacketListenerImpl.class)
public abstract class ServerCommonNetworkHandler {
    @ModifyArg(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"))
    public Packet<?> send(Packet<?> packet) {
        if (!XRay.isEnable()) return packet;

        if (!(((Object) this) instanceof ServerGamePacketListenerImpl)) return packet;

        ServerGamePacketListenerImpl pl = (ServerGamePacketListenerImpl) ((Object) this);

        if (packet instanceof ClientboundBlockUpdatePacket) {
            BlockPos pos = ((ClientboundBlockUpdatePacket) packet).getPos();

            if (pos == null) {
                return packet;
            }

            ServerLevel world = pl.player.serverLevel();
            LevelChunk chunk = world.getChunkAt(pos);
            int section_index = chunk.getSectionIndex(pos.getY());

            BlockPos p = pos.subtract(chunk.getPos().getBlockAt(0, 0, 0));
            p = p.subtract(new Vec3i(0, chunk.getSectionYFromSectionIndex(section_index) * 16, 0));

            if (XRay.isVisible(chunk, p, section_index)) {
                return packet;
            }

            packet = new ClientboundBlockUpdatePacket(((ClientboundBlockUpdatePacket) packet).getPos(), XRay.shadowBlock().defaultBlockState());
            return packet;
        }

        if (packet instanceof ClientboundSectionBlocksUpdatePacket) {
            ClientboundSectionBlocksUpdatePacket p = (ClientboundSectionBlocksUpdatePacket) packet;
            try {
                Field sectionPos_field = ClientboundSectionBlocksUpdatePacket.class.getDeclaredField("sectionPos");
                Field positions_field = ClientboundSectionBlocksUpdatePacket.class.getDeclaredField("positions");
                Field states_field = ClientboundSectionBlocksUpdatePacket.class.getDeclaredField("states");
                sectionPos_field.setAccessible(true);
                positions_field.setAccessible(true);
                states_field.setAccessible(true);
                SectionPos sectionPos = (SectionPos) sectionPos_field.get(p);
                short[] positions = (short[]) positions_field.get(p);
                BlockState[] states = (BlockState[]) states_field.get(p);

                ServerLevel world = pl.getPlayer().serverLevel();

                int i = 0;
                for (short pos_i : positions) {
                    BlockPos b_pos = sectionPos.relativeToBlockPos(pos_i);
                    LevelChunk chunk = world.getChunkAt(b_pos);

                    int section_index = chunk.getSectionIndex(b_pos.getY());

                    BlockPos sp = b_pos.subtract(chunk.getPos().getBlockAt(0, 0, 0));
                    sp = sp.subtract(new Vec3i(0, chunk.getSectionYFromSectionIndex(section_index) * 16, 0));

                    if (!XRay.isVisible(chunk, sp, section_index)) {
                        states[i] = XRay.shadowBlock().defaultBlockState();
                    }

                    i += 1;
                }

                states_field.set(p, states);

            } catch (Exception e) {
                SSAntiCheat.LOGGER.error("Cannot modify the ClientboundSectionBlocksUpdatePacket");
            }
        }


        return packet;
    }
}
