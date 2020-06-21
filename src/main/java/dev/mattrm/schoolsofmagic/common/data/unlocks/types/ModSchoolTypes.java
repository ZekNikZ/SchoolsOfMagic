package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.api.SchoolsOfMagicAPI;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SimpleSchoolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModSchoolTypes {
    public static final DeferredRegister<SchoolType> SCHOOL_TYPES = new DeferredRegister<>(SchoolsOfMagicAPI.SCHOOL_TYPE_REGISTRY, GlobalConstants.MODID);

    public static final RegistryObject<SchoolType> SIMPLE_SCHOOL_TYPE = SCHOOL_TYPES.register("normal", SimpleSchoolType::new);

}
