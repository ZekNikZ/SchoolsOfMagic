package dev.mattrm.schoolsofmagic.common.network.packet;

import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import dev.mattrm.schoolsofmagic.common.util.PacketBufferUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class DataSyncMessage<T> {
    protected T data;

    public DataSyncMessage(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public static class UnlockSync extends DataSyncMessage<Map<School, Map<ResourceLocation, Unlock>>> {
        public UnlockSync(Map<School, Map<ResourceLocation, Unlock>> data) {
            super(data);
        }

        public static void encode(final UnlockSync message, final PacketBuffer bufferIn) {
            PacketBufferUtils.writeMap(bufferIn, message.getData(),
                    School::writeToBuffer,
                    (unlocks, buffer) -> PacketBufferUtils.writeMap(buffer, unlocks,
                            (loc, b) -> buffer.writeResourceLocation(loc),
                            (unlock, b) -> unlock.writeToBuffer(buffer)
                    )
            );
        }

        public static UnlockSync decode(final PacketBuffer bufferIn) {
            Map<School, Map<ResourceLocation, Unlock>> map = PacketBufferUtils.readMap(bufferIn,
                    (buffer) -> ((School) GameRegistry.findRegistry(SchoolType.class).getValue(buffer.readResourceLocation()).readFromBuffer(buffer)),
                    (buffer) -> PacketBufferUtils.readMap(buffer,
                            PacketBuffer::readResourceLocation,
                            (b) -> ((Unlock) GameRegistry.findRegistry(UnlockType.class).getValue(b.readResourceLocation()).readFromBuffer(b))
                    )
            );

            return new UnlockSync(map);
        }

        public static void handle(final UnlockSync message, final Supplier<NetworkEvent.Context> ctx) {
            SchoolsOfMagicMod.getInstance().getClientUnlockManager().loadData(message.getData());
        }
    }

    public static class SchoolSync extends DataSyncMessage<Map<ResourceLocation, School>> {
        public SchoolSync(Map<ResourceLocation, School> data) {
            super(data);
        }

        public static void encode(final SchoolSync message, final PacketBuffer bufferIn) {
            PacketBufferUtils.writeMap(bufferIn, message.getData(),
                    (r, b) -> b.writeResourceLocation(r),
                    School::writeToBuffer
            );
        }

        public static SchoolSync decode(final PacketBuffer bufferIn) {
            Map<ResourceLocation, School> map = PacketBufferUtils.readMap(bufferIn,
                    PacketBuffer::readResourceLocation,
                    b -> ((School) GameRegistry.findRegistry(SchoolType.class).getValue(b.readResourceLocation()).readFromBuffer(b))
            );

            return new SchoolSync(map);
        }

        public static void handle(final SchoolSync message, final Supplier<NetworkEvent.Context> ctx) {
            SchoolsOfMagicMod.getInstance().getClientSchoolManager().loadData(message.getData());
        }
    }
}

