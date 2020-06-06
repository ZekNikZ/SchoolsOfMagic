package dev.mattrm.schoolsofmagic.common.networking.packet;

import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.networking.SchoolsOfMagicPacketHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                AdvancementProgress progress = AdvancementCache.getServerInstance().get(message.getUUID(), message.getAdvancement());
                LOGGER.debug("Found progress is " + (progress == null ? "NULL" : "DONE=" + progress.isDone()));

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
        private AdvancementProgress progress;

        public Sync(UUID uuid, ResourceLocation advancement, AdvancementProgress progress) {
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

        public AdvancementProgress getProgress() {
            return progress;
        }

        public static void encode(final Sync message, final PacketBuffer buffer) {
            buffer.writeUniqueId(message.getUUID());
            buffer.writeResourceLocation(message.getAdvancement());

            buffer.writeBoolean(message.getProgress() != null);
            if (message.getProgress() != null) {
                message.getProgress().serializeToNetwork(buffer); // TODO: I don't think this works
            }
        }

        public static Sync decode(final PacketBuffer buffer) {
            return new Sync(buffer.readUniqueId(), buffer.readResourceLocation(), buffer.readBoolean() ? AdvancementProgress.fromNetwork(buffer) : null);
        }

        public static void handle(final Sync message, final Supplier<NetworkEvent.Context> ctx) {
            LOGGER.info("Handling advancement response from server: " + message.getUUID() + " " + message.getAdvancement());

            ctx.get().enqueueWork(() -> {
                AdvancementCache.getClientInstance().put(message.getUUID(), message.getAdvancement(), message.getProgress());
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
