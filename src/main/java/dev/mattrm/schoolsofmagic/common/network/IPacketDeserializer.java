package dev.mattrm.schoolsofmagic.common.network;

import net.minecraft.network.PacketBuffer;

public interface IPacketDeserializer<T extends IPacketSerializer> {
    T readFromBuffer(PacketBuffer buffer);
}
