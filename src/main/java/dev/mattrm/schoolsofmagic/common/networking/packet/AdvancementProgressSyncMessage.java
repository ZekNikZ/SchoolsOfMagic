package dev.mattrm.schoolsofmagic.common.networking.packet;

import com.google.gson.internal.$Gson$Preconditions;
import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.cache.ClientAdvancementCache;
import dev.mattrm.schoolsofmagic.common.cache.ServerAdvancementCache;
import dev.mattrm.schoolsofmagic.common.networking.SchoolsOfMagicPacketHandler;
import dev.mattrm.schoolsofmagic.common.util.DoubleKeyMap;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

// TODO: make work with multiple advancements at once
public class AdvancementProgressSyncMessage {
    public static class Query {
        private static final Logger LOGGER = LogManager.getLogger();

        private UUID uuid;
        private ResourceLocation advancement;
        private boolean invalidateCache;

        public Query(UUID uuid, Advancement advancement) {
            this(uuid, advancement.getId(), false);
        }

        public Query(UUID uuid, Advancement advancement, boolean invalidateCache) {
            this(uuid, advancement.getId(), invalidateCache);
        }

        public Query(UUID uuid, ResourceLocation advancement) {
            this(uuid, advancement, false);
        }

        public Query(UUID uuid, ResourceLocation advancement, boolean invalidateCache) {
            this.uuid = uuid;
            this.advancement = advancement;
            this.invalidateCache = invalidateCache;
        }

        public UUID getUUID() {
            return uuid;
        }

        public ResourceLocation getAdvancement() {
            return advancement;
        }

        public boolean shouldInvalidateCache() {
            return invalidateCache;
        }

        public static void encode(final Query message, final PacketBuffer buffer) {
            buffer.writeUniqueId(message.getUUID());
            buffer.writeResourceLocation(message.getAdvancement());
            buffer.writeBoolean(message.shouldInvalidateCache());
        }

        public static Query decode(final PacketBuffer buffer) {
            return new Query(buffer.readUniqueId(), buffer.readResourceLocation(), buffer.readBoolean());
        }

        public static void handle(final Query message, final Supplier<NetworkEvent.Context> ctx) {
            LOGGER.info("Handling advancement query from client: " + message.getUUID() + " " + message.getAdvancement());

            ctx.get().enqueueWork(() -> {
                // Update server cache
                if (message.shouldInvalidateCache()) {
                    AdvancementCache.getServerInstance().invalidate(message.getUUID(), message.getAdvancement());
                }
                boolean progress = AdvancementCache.getServerInstance().get(message.getUUID(), message.getAdvancement());
                LOGGER.debug("Found progress is DONE=" + progress);

                // Respond
                SchoolsOfMagicPacketHandler.getInstance().sendTo(
                        new Sync(message.getUUID(), message.getAdvancement(), progress),
                        ctx.get().getNetworkManager(),
                        NetworkDirection.PLAY_TO_CLIENT
                );
            });

            ctx.get().setPacketHandled(true);
        }
    }

    public static class Sync {
        private static final Logger LOGGER = LogManager.getLogger();

        private UUID uuid;
        private ResourceLocation advancement;
        private boolean progress;

        public Sync(UUID uuid, ResourceLocation advancement, boolean progress) {
            this.uuid = uuid;
            this.advancement = advancement;
            this.progress = progress;
        }

        public UUID getUUID() {
            return uuid;
        }

        public ResourceLocation getAdvancement() {
            return advancement;
        }

        public boolean getProgress() {
            return progress;
        }

        public static void encode(final Sync message, final PacketBuffer buffer) {
            buffer.writeUniqueId(message.getUUID());
            buffer.writeResourceLocation(message.getAdvancement());

            buffer.writeBoolean(message.getProgress());
        }

        public static Sync decode(final PacketBuffer buffer) {
            return new Sync(buffer.readUniqueId(), buffer.readResourceLocation(), buffer.readBoolean());
        }

        public static void handle(final Sync message, final Supplier<NetworkEvent.Context> ctx) {
            LOGGER.info("Handling advancement response from server: " + message.getUUID() + " " + message.getAdvancement());

            ctx.get().enqueueWork(() -> {
                AdvancementCache.getClientInstance().put(message.getUUID(), message.getAdvancement(), message.getProgress());
            });

            ctx.get().setPacketHandled(true);
        }
    }

    public static class QueryAll {
        private static final Logger LOGGER = LogManager.getLogger();

        private UUID uuid;
        private boolean invalidateCache;

        public QueryAll(UUID uuid) {
            this(uuid, false);
        }

        public QueryAll(UUID uuid, boolean invalidateCache) {
            this.uuid = uuid;
            this.invalidateCache = invalidateCache;
        }

        public UUID getUUID() {
            return uuid;
        }

        public boolean shouldInvalidateCache() {
            return invalidateCache;
        }

        public static void encode(final QueryAll message, final PacketBuffer buffer) {
            buffer.writeUniqueId(message.getUUID());
            buffer.writeBoolean(message.shouldInvalidateCache());
        }

        public static QueryAll decode(final PacketBuffer buffer) {
            return new QueryAll(buffer.readUniqueId(), buffer.readBoolean());
        }

        public static void handle(final QueryAll message, final Supplier<NetworkEvent.Context> ctx) {
            LOGGER.info("Handling advancement query-all from client: " + message.getUUID());


            ctx.get().enqueueWork(() -> {
                Map<ResourceLocation, Boolean> playerProgress = ((ServerAdvancementCache) AdvancementCache.getServerInstance()).getAllForPlayer(message.getUUID());

                for (ResourceLocation advancement : playerProgress.keySet()) {
                    // Update server cache
                    if (message.shouldInvalidateCache()) {
                        AdvancementCache.getServerInstance().invalidate(message.getUUID(), advancement);
                    }
                    boolean progress = AdvancementCache.getServerInstance().get(message.getUUID(), advancement);
                    LOGGER.debug("Found progress is DONE=" + progress);

                    // TODO: is this even needed anymore? See ServAdvCach#load
                    // Respond
                    SchoolsOfMagicPacketHandler.getInstance().sendTo(
                            new Sync(message.getUUID(), advancement, progress),
                            ctx.get().getNetworkManager(),
                            NetworkDirection.PLAY_TO_CLIENT
                    );
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }

    public static class SyncAll {
        private static final Logger LOGGER = LogManager.getLogger();

        private DoubleKeyMap<UUID, ResourceLocation, Boolean> progress;

        public SyncAll(DoubleKeyMap<UUID, ResourceLocation, Boolean> progress) {
            this.progress = progress;
        }


        public DoubleKeyMap<UUID, ResourceLocation, Boolean> getProgress() {
            return progress;
        }

        public static void encode(final SyncAll message, final PacketBuffer buffer) {
            buffer.writeVarInt(message.getProgress().size());
            message.getProgress().getDelegate().forEach((key, value) -> {
                buffer.writeUniqueId(key.key1());
                buffer.writeResourceLocation(key.key2());
                buffer.writeBoolean(value);
            });
        }

        public static SyncAll decode(final PacketBuffer buffer) {
            DoubleKeyMap<UUID, ResourceLocation, Boolean> progress = new DoubleKeyMap<>();

            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                progress.put(buffer.readUniqueId(), buffer.readResourceLocation(), buffer.readBoolean());
            }

            return new SyncAll(progress);
        }

        public static void handle(final SyncAll message, final Supplier<NetworkEvent.Context> ctx) {
            LOGGER.info("Handling advancement sync-all from server.");

            ctx.get().enqueueWork(() -> {
                AdvancementCache.invalidateClient();
                message.getProgress().getDelegate().forEach((key, value) -> {
                    AdvancementCache.getClientInstance().put(key.key1(), key.key2(), value);
                });
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
