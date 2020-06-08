package dev.mattrm.schoolsofmagic.common.recipe;

import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.UUID;

public enum RecipeAdvancementMode {
    ALL {
        @Override
        public boolean check(UUID uuid, String[] advancements, boolean clientSide) {
            return Arrays.stream(advancements).map(a -> AdvancementCache.getInstance(clientSide).getIsDone(uuid, new ResourceLocation(a))).allMatch(a -> a);
        }
    },
    ANY {
        @Override
        public boolean check(UUID uuid, String[] advancements, boolean clientSide) {
            return Arrays.stream(advancements).map(a -> AdvancementCache.getInstance(clientSide).getIsDone(uuid, new ResourceLocation(a))).anyMatch(a -> a);
        }
    };

    public abstract boolean check(UUID uuid, String[] advancements, boolean clientSide);
}
