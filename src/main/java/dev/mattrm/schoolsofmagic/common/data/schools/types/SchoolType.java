package dev.mattrm.schoolsofmagic.common.data.schools.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public abstract class SchoolType extends JsonDataType<School> implements IForgeRegistryEntry<SchoolType> {
    public SchoolType() {
        super(null);
    }

    @Override
    public abstract School deserialize(ResourceLocation id, JsonObject json);

    @Override
    public abstract School readFromBuffer(PacketBuffer buffer);

    @Override
    public SchoolType setRegistryName(ResourceLocation name) {
        this.setId(name);
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.getId();
    }

    @Override
    public Class<SchoolType> getRegistryType() {
        return SchoolType.class;
    }
}
