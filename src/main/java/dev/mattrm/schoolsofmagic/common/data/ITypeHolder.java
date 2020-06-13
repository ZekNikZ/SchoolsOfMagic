package dev.mattrm.schoolsofmagic.common.data;

import net.minecraft.util.ResourceLocation;

public interface ITypeHolder<T extends JsonDataType<?>> {
    void registerType(ResourceLocation name, T type);

    T getType(ResourceLocation id);
}
