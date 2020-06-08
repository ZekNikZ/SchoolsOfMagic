package dev.mattrm.schoolsofmagic.common.data.unlocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import dev.mattrm.schoolsofmagic.common.data.ModDataJsonReloadListener;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class UnlockManager extends ModDataJsonReloadListener<Unlock, UnlockType> {
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<School, Map<ResourceLocation, Unlock>> unlocks = ImmutableMap.of();

    public UnlockManager() {
        super("som_unlocks");
    }

    protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        Map<School, ImmutableMap.Builder<ResourceLocation, Unlock>> map = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();

            // TODO: remove, debug
            LOGGER.info("Loading unlock {}:", resourceLocation);
            LOGGER.info(entry.getValue().toString());

            try {
                Unlock unlock = deserializeData(resourceLocation, entry.getValue());
                if (unlock == null) {
                    LOGGER.info("Skipping loading unlock {} as it's serializer returned null", resourceLocation);
                }

                map.computeIfAbsent(unlock.getSchool(), (p_223391_0_) -> {
                    return ImmutableMap.builder();
                }).put(resourceLocation, unlock);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading unlock {}", resourceLocation, jsonparseexception);
            }
        }

        this.unlocks = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> entry.getValue().build()));
        LOGGER.info("Loaded {} unlocks", map.size());
    }
}