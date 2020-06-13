package dev.mattrm.schoolsofmagic.common.data.schools.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public abstract class SchoolType extends JsonDataType<School> {
    public SchoolType(ResourceLocation id) {
        super(id);
    }

    @Override
    public abstract School deserialize(ResourceLocation id, JsonObject json);

    @Override
    public abstract School readFromBuffer(PacketBuffer buffer);
}
