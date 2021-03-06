package dev.mattrm.schoolsofmagic.common.data.schools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.mattrm.schoolsofmagic.common.data.ModDataJsonReloadListener;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import dev.mattrm.schoolsofmagic.common.network.SchoolsOfMagicPacketHandler;
import dev.mattrm.schoolsofmagic.common.network.packet.DataSyncMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class SchoolManager extends ModDataJsonReloadListener<School, SchoolType> {
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<ResourceLocation, School> schools = ImmutableMap.of();

    public SchoolManager() {
        super("som_schools");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        Map<ResourceLocation, School> map = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();

            // TODO: remove, debug
            LOGGER.info("Loading school {}:", resourceLocation);
            LOGGER.info(entry.getValue().toString());

            try {
                School school = deserializeData(resourceLocation, entry.getValue());
                if (school == null) {
                    LOGGER.info("Skipping loading school {} as it's serializer returned null", resourceLocation);
                }

                map.put(resourceLocation, school);
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading school {}", resourceLocation, jsonparseexception);
            }
        }

        this.schools = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
        LOGGER.info("Loaded {} schools", map.size());

        SchoolsOfMagicPacketHandler.getInstance().send(
                PacketDistributor.ALL.noArg(),
                new DataSyncMessage.SchoolSync(this.schools)
        );
    }

    public School getSchool(ResourceLocation id) {
        return this.schools.get(id);
    }


    public void syncClient(ServerPlayerEntity player) {
        SchoolsOfMagicPacketHandler.getInstance().send(
                PacketDistributor.PLAYER.with(() -> player),
                new DataSyncMessage.SchoolSync(this.schools)
        );
    }
}
