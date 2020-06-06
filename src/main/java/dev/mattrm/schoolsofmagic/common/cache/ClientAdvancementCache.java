package dev.mattrm.schoolsofmagic.common.cache;

import dev.mattrm.schoolsofmagic.common.networking.SchoolsOfMagicPacketHandler;
import dev.mattrm.schoolsofmagic.common.networking.packet.AdvancementProgressSyncMessage;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class ClientAdvancementCache extends AdvancementCache {
    private static final Logger LOGGER = LogManager.getLogger();

    public void loadAllForPlayer(UUID uuid) {
        SchoolsOfMagicPacketHandler.getInstance().sendToServer(new AdvancementProgressSyncMessage.QueryAll(uuid));
    }

    @Override
    public void load(UUID uuid, ResourceLocation advancement) {
        LOGGER.info("Loading advancement information from server: " + uuid + " " + advancement);
        SchoolsOfMagicPacketHandler.getInstance().sendToServer(new AdvancementProgressSyncMessage.Query(uuid, advancement));

        this.put(uuid, advancement, false);
    }
}
