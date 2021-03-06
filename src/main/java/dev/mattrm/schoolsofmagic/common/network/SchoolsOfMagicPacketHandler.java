package dev.mattrm.schoolsofmagic.common.network;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.common.network.packet.AdvancementProgressSyncMessage;
import dev.mattrm.schoolsofmagic.common.network.packet.DataSyncMessage;
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
        instance.registerMessage(++id, AdvancementProgressSyncMessage.QueryAll.class, AdvancementProgressSyncMessage.QueryAll::encode, AdvancementProgressSyncMessage.QueryAll::decode, AdvancementProgressSyncMessage.QueryAll::handle);
        instance.registerMessage(++id, AdvancementProgressSyncMessage.Sync.class, AdvancementProgressSyncMessage.Sync::encode, AdvancementProgressSyncMessage.Sync::decode, AdvancementProgressSyncMessage.Sync::handle);
        instance.registerMessage(++id, AdvancementProgressSyncMessage.SyncAll.class, AdvancementProgressSyncMessage.SyncAll::encode, AdvancementProgressSyncMessage.SyncAll::decode, AdvancementProgressSyncMessage.SyncAll::handle);
        instance.registerMessage(++id, DataSyncMessage.UnlockSync.class, DataSyncMessage.UnlockSync::encode, DataSyncMessage.UnlockSync::decode, DataSyncMessage.UnlockSync::handle);
        instance.registerMessage(++id, DataSyncMessage.SchoolSync.class, DataSyncMessage.SchoolSync::encode, DataSyncMessage.SchoolSync::decode, DataSyncMessage.SchoolSync::handle);
    }

    public static SimpleChannel getInstance() {
        if (instance == null) {
            setup();
        }

        return instance;
    }
}
