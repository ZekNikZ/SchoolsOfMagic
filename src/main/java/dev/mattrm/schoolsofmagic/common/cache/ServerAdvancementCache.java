package dev.mattrm.schoolsofmagic.common.cache;

import com.mojang.authlib.GameProfile;
import dev.mattrm.schoolsofmagic.common.network.SchoolsOfMagicPacketHandler;
import dev.mattrm.schoolsofmagic.common.network.packet.AdvancementProgressSyncMessage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class ServerAdvancementCache extends AdvancementCache {
    private static final Logger LOGGER = LogManager.getLogger();

    private MinecraftServer server;

    public ServerAdvancementCache(MinecraftServer server) {
        this.server = server;
    }

    public Map<ResourceLocation, Boolean> getAllForPlayer(UUID uuid) {
        return this.cache.getAllByKey1(uuid);
    }

    public void syncClient(ServerPlayerEntity player) {
        SchoolsOfMagicPacketHandler.getInstance().send(
                PacketDistributor.PLAYER.with(() -> player),
                new AdvancementProgressSyncMessage.SyncAll(this.cache)
        );
    }

    @Override
    public void notifyListeners(UUID uuid, ResourceLocation advancement) {
        super.notifyListeners(uuid, advancement);

        SchoolsOfMagicPacketHandler.getInstance().send(PacketDistributor.ALL.noArg(), new AdvancementProgressSyncMessage.Sync(uuid, advancement, this.get(uuid, advancement)));
        LOGGER.debug("Notified all server clients of update: " + uuid + " " + advancement);
    }

    @Override
    public void load(UUID uuid, ResourceLocation advancement) {
        LOGGER.info("Loading advancement information from world data files: " + uuid + " " + advancement);

        ServerPlayerEntity player = this.server.getPlayerList().getPlayerByUUID(uuid);
        PlayerAdvancements playerAdvancements = null;
        if (player != null) {
            playerAdvancements = this.server.getPlayerList().getPlayerAdvancements(player);
        }

        if (playerAdvancements == null) {
            File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "advancements");
            File file2 = new File(file1, uuid + ".json");
            player = new FakePlayer(this.server.getWorld(DimensionType.OVERWORLD), new GameProfile(uuid, UsernameCache.get(uuid).getNow("__fakePlayer")));
            playerAdvancements = new PlayerAdvancements(this.server, file2, player);
        }

        playerAdvancements.setPlayer(player);

        Advancement adv = this.server.getAdvancementManager().getAdvancement(advancement);
        if (adv == null) {
            throw new NoSuchElementException("Advancement is not registered: " + advancement);
        }
        this.put(uuid, advancement, playerAdvancements.getProgress(adv).isDone());

        this.notifyListeners(uuid, advancement);
    }
}
