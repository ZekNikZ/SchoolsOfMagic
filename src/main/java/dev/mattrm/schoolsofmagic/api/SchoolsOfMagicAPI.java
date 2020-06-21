package dev.mattrm.schoolsofmagic.api;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SchoolsOfMagicAPI {
    private static final Logger LOGGER = LogManager.getLogger();

    // Init these here to prevent race conditions
    public static IForgeRegistry<SchoolType> SCHOOL_TYPE_REGISTRY = createRegistry("school_type", SchoolType.class);
    public static IForgeRegistry<UnlockType> UNLOCK_TYPE_REGISTRY = createRegistry("unlock_type", UnlockType.class);

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistry(String id, Class<T> clazz) {
        LOGGER.info("Registering '" + id + "' registry.");
        RegistryBuilder<T> builder = new RegistryBuilder<>();
        builder.setType(clazz);
        ResourceLocation key = new ResourceLocation(GlobalConstants.MODID, id);
        builder.setName(key);
        builder.setDefaultKey(key);
        return builder.create();
    }
}
