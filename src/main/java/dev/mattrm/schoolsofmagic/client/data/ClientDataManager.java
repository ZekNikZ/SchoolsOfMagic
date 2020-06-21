package dev.mattrm.schoolsofmagic.client.data;

import com.google.common.collect.Maps;
import dev.mattrm.schoolsofmagic.common.data.ITypeHolder;
import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Map;

public abstract class ClientDataManager<D extends JsonData<T>, T extends JsonDataType<D> & IForgeRegistryEntry<T>, O> implements ITypeHolder<T> {
    private Map<ResourceLocation, T> types = Maps.newHashMap();
    private Class<T> typeClass;

    protected O data;

    public void loadData(O data) {
        this.data = data;
    }

    public void setTypeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public T getType(ResourceLocation id) {
        return GameRegistry.findRegistry(typeClass).getValue(id);
    }
}
