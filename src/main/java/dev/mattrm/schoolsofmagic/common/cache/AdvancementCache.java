package dev.mattrm.schoolsofmagic.common.cache;

import dev.mattrm.schoolsofmagic.common.util.DoubleKeyMap;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public abstract class AdvancementCache {
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: replace boolean with enum or custom progress class maybe?
    protected DoubleKeyMap<UUID, ResourceLocation, Boolean> cache;

    private static ClientAdvancementCache clientInstance;
    private static ServerAdvancementCache serverInstance;

    public static ClientAdvancementCache getClientInstance() {
        if (clientInstance == null) {
            throw new RuntimeException("Advancement cache accessed before initialized.");
        }

        return clientInstance;
    }

    public static ServerAdvancementCache getServerInstance() {
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

    public static void initClientCache(@Nonnull ClientAdvancementCache cache) {
        clientInstance = cache;
        clientInstance.setup();
        LOGGER.debug("Initialized client cache.");
    }

    public static void initServerCache(@Nonnull ServerAdvancementCache cache) {
        serverInstance = cache;
        serverInstance.setup();
        LOGGER.debug("Initialized server cache.");
    }

    public static void invalidateClient() {
        if (clientInstance != null) {
            clientInstance.cache.getDelegate().clear();
        }
    }

    public static void invalidateServer() {
        if (serverInstance != null) {
            serverInstance.cache.getDelegate().clear();
        }
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

    public final void put(UUID uuid, ResourceLocation advancement, boolean progress) {
        // TODO: INFO or DEBUG?
        LOGGER.debug("Put advancement progress into cache: " + uuid + " " + advancement + " DONE=" + progress);
        cache.put(uuid, advancement, progress);
    }

    public final boolean get(UUID uuid, ResourceLocation advancement) {
        if (!cache.containsKeyPair(uuid, advancement)) {
            LOGGER.debug("Loading advancement progress into cache: " + uuid + " " + advancement);
            this.load(uuid, advancement);
        }

        boolean result = cache.get(uuid, advancement);
        LOGGER.debug("Requested advancement progress from cache: " + uuid + " " + advancement + " DONE=" + result);

        return result;
    }

    public final boolean getIsDone(UUID uuid, ResourceLocation advancement) {
        return this.get(uuid, advancement);
    }

    public final void invalidate(UUID uuid, ResourceLocation advancement) {
        if (cache.containsKeyPair(uuid, advancement)) {
            boolean progress = cache.get(uuid, advancement);
            LOGGER.debug("Invalidating current advancement progress: " + uuid + " " + advancement + " DONE=" + progress);
        }
        cache.removeIfPresent(uuid, advancement);
    }
}
