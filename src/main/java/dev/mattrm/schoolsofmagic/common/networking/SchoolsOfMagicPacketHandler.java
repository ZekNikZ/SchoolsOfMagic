package dev.mattrm.schoolsofmagic.common.networking;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.common.networking.packet.AdvancementProgressSyncMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class SchoolsOfMagicPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel instance;

    public static void setup() {
        instance = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(GlobalConstants.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        int id = 0;
        instance.registerMessage(++id, AdvancementProgressSyncMessage.Query.class, AdvancementProgressSyncMessage.Query::encode, AdvancementProgressSyncMessage.Query::decode, AdvancementProgressSyncMessage.Query::handle);
        instance.registerMessage(++id, AdvancementProgressSyncMessage.Sync.class, AdvancementProgressSyncMessage.Sync::encode, AdvancementProgressSyncMessage.Sync::decode, AdvancementProgressSyncMessage.Sync::handle);
    }

    public static SimpleChannel getInstance() {
        if (instance == null) {
            setup();
        }

        return instance;
    }
}
