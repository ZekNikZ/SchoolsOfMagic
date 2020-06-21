package dev.mattrm.schoolsofmagic.common.data.schools.types;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.api.SchoolsOfMagicAPI;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.SimpleUnlockType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModUnlockTypes {
    public static final DeferredRegister<UnlockType> UNLOCK_TYPES = new DeferredRegister<>(SchoolsOfMagicAPI.UNLOCK_TYPE_REGISTRY, GlobalConstants.MODID);

    public static final RegistryObject<UnlockType> RECIPE_TYPE = UNLOCK_TYPES.register("recipe", () -> new SimpleUnlockType("gui/journal/widgets.png", 0, 0));
    public static final RegistryObject<UnlockType> SPELL_TYPE = UNLOCK_TYPES.register("spell", () -> new SimpleUnlockType("gui/journal/widgets.png", 26, 0));
    public static final RegistryObject<UnlockType> ABILITY_TYPE = UNLOCK_TYPES.register("ability", () -> new SimpleUnlockType("gui/journal/widgets.png", 52, 0));
    public static final RegistryObject<UnlockType> RITUAL_TYPE = UNLOCK_TYPES.register("ritual", () -> new SimpleUnlockType("gui/journal/widgets.png", 78, 0));
}
