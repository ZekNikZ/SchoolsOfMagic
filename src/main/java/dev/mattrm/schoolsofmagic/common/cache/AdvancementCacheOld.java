package dev.mattrm.schoolsofmagic.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class AdvancementCacheOld {
    private static LoadingCache<UUID, PlayerAdvancements> cache;
    private static MinecraftServer server;

    private static Advancement fromId(String advancement) {
       return server.getAdvancementManager().getAdvancement(new ResourceLocation(""));
    }

    public static AdvancementProgress getBlocking(UUID uuid, String advancement) throws IOException {
        return getBlocking(uuid, fromId(advancement));
    }

    public static AdvancementProgress getBlocking(UUID uuid, Advancement advancement) throws IOException {
        try {
            return cache.get(uuid).getProgress(advancement);
        } catch (UncheckedExecutionException | ExecutionException e) {
            throw new IOException("Faild contacting Mojang API");
        }
    }

    // TODO: make a client cache too ---- NEVERMIND just make a worldsaveddata instead
    public static CompletableFuture<AdvancementProgress> get(UUID uuid, String advancement) {
        return get(uuid, fromId(advancement));
    }

    public static CompletableFuture<AdvancementProgress> get(UUID uuid, Advancement advancement) {
        PlayerAdvancements advancements = cache.getIfPresent(uuid);
        if (advancements != null) {
            return CompletableFuture.completedFuture(advancements.getProgress(advancement));
        } else {
            CompletableFuture<AdvancementProgress> future = new CompletableFuture<>();
            ForkJoinPool.commonPool().execute(() -> {
                try {
                    future.complete(cache.get(uuid).getProgress(advancement));
                } catch (ExecutionException | UncheckedExecutionException x) {
                    future.completeExceptionally(x.getCause());
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            });
            return future;
        }
    }

    public static void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }

    private AdvancementCacheOld() {
    }

    public static void initCache(int cacheSize, MinecraftServer serverIn) {
        server = serverIn;
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(new AdvancementCacheOld.Loader(serverIn));
    }

    private static final class Loader extends CacheLoader<UUID, PlayerAdvancements> {
        private final MinecraftServer server;

        Loader(MinecraftServer server) {
            this.server = server;
        }

        @Override
        public PlayerAdvancements load(@Nonnull UUID uuid) throws IOException {
            PlayerAdvancements playerAdvancements;

            File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory(), "advancements");
            File file2 = new File(file1, uuid + ".json");
            playerAdvancements = new PlayerAdvancements(this.server, file2, null);

            return playerAdvancements;
        }
    }
}
