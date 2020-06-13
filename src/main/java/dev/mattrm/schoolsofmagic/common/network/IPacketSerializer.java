package dev.mattrm.schoolsofmagic.common.network;

import net.minecraft.network.PacketBuffer;

public interface IPacketSerializer {
    void writeToBuffer(PacketBuffer buffer);
}
