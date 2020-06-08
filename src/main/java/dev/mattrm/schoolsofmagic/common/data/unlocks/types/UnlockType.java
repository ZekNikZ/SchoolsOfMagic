package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import net.minecraft.util.ResourceLocation;

public abstract class UnlockType extends JsonDataType<Unlock> {
    public UnlockType(ResourceLocation id) {
        super(id);
    }
}
