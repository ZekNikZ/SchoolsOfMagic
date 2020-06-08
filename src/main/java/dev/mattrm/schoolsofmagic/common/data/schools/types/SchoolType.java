package dev.mattrm.schoolsofmagic.common.data.schools.types;

import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.util.ResourceLocation;

public abstract class SchoolType extends JsonDataType<School> {
    public SchoolType(ResourceLocation id) {
        super(id);
    }
}
