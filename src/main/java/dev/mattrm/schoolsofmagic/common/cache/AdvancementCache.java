package dev.mattrm.schoolsofmagic.common.cache;

import dev.mattrm.schoolsofmagic.common.util.DoubleKeyMap;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class AdvancementCache {
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: replace boolean with enum or custom progress class maybe?
    protected DoubleKeyMap<UUID, ResourceLocation, AdvancementProgress> cache;

    private static AdvancementCache clientInstance;
    private static AdvancementCache serverInstance;

    public static AdvancementCache getClientInstance() {
        if (clientInstance == null) {
            throw new RuntimeException("Advancement cache accessed before initialized.");
        }

        return clientInstance;
    }

    public static AdvancementCache getServerInstance() {
        if (serverInstance == null) {
            throw new RuntimeException("Advancement cache accessed before initialized.");
        }

        return serverInstance;
    }

    public static AdvancementCache getInstance(boolean client) {
        return client ? getClientInstance() : getServerInstance();
    }

    public void setup() {
        // stub
        this.cache = new DoubleKeyMap<>();
    }

    public void notifyListeners(UUID uuid, ResourceLocation advancement) {
        // stub
    }

    public abstract void load(UUID uuid, ResourceLocation advancement);

    public static void initClientCache(@Nonnull AdvancementCache cache) {
        clientInstance = cache;
        clientInstance.setup();
        LOGGER.debug("Initialized client cache.");
    }

    public static void initServerCache(@Nonnull AdvancementCache cache) {
        serverInstance = cache;
        serverInstance.setup();
        LOGGER.debug("Initialized server cache.");
    }

    public static void initCache(boolean client, @Nonnull AdvancementCache cache) {
        if (client) {
            initClientCache(cache);
        } else {
            initServerCache(cache);
        }
    }

    public static void invalidateClient() {
        clientInstance = null;
    }

    public static void invalidateServer() {
        serverInstance = null;
    }

    public static void invalidate(boolean client) {
        if (client) {
            invalidateClient();
        } else {
            invalidateServer();
        }
    }

    public static boolean isClientSetup() {
        return clientInstance != null;
    }

    public static boolean isServerSetup() {
        return clientInstance != null;
    }

    public static boolean isSetup(boolean client) {
        return client ? isClientSetup() : isServerSetup();
    }

    public final void put(UUID uuid, ResourceLocation advancement, AdvancementProgress progress) {
        // TODO: INFO or DEBUG?
        if (progress != null) {
            LOGGER.debug("Put advancement progress into cache: " + uuid + " " + advancement + " DONE=" + progress.isDone());
        } else {
            LOGGER.debug("Put advancement progress into cache: " + uuid + " " + advancement + " NULL");
        }
        cache.put(uuid, advancement, progress);
    }

    public final AdvancementProgress get(UUID uuid, ResourceLocation advancement) {
        if (!cache.containsKeyPair(uuid, advancement)) {
            LOGGER.debug("Loading advancement progress into cache: " + uuid + " " + advancement);
            this.load(uuid, advancement);
        }

        AdvancementProgress result = cache.get(uuid, advancement);

        if (result == null) {
            LOGGER.warn("Requested advancement progress from cache is null, invalidating: " + uuid + " " + advancement);
            this.invalidate(uuid, advancement);
        } else {
            LOGGER.debug("Requested advancement progress from cache: " + uuid + " " + advancement + " DONE=" + result.isDone());
        }

        return result;
    }

    public final boolean getIsDone(UUID uuid, ResourceLocation advancement) {
        AdvancementProgress progress = this.get(uuid, advancement);

        return progress != null && progress.isDone();
    }

    public final void invalidate(UUID uuid, ResourceLocation advancement) {
        if (cache.containsKeyPair(uuid, advancement)) {
            AdvancementProgress progress = cache.get(uuid, advancement);
            if (progress != null) {
                LOGGER.debug("Invalidating current advancement progress: " + uuid + " " + advancement + " DONE=" + progress.isDone());
            } else {
                LOGGER.debug("Invalidating current advancement progress: " + uuid + " " + advancement + " NULL");
            }
        }
        cache.removeIfPresent(uuid, advancement);
    }
}
