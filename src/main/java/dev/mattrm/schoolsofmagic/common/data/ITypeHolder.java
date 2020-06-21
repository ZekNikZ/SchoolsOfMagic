package dev.mattrm.schoolsofmagic.common.data;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ITypeHolder<T extends JsonDataType<?> & IForgeRegistryEntry<T>> {
    void setTypeClass(Class<T> typeClass);

    T getType(ResourceLocation id);
}
