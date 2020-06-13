package dev.mattrm.schoolsofmagic.client.data;

import com.google.common.collect.Maps;
import dev.mattrm.schoolsofmagic.common.data.ITypeHolder;
import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public abstract class ClientDataManager<D extends JsonData<T>, T extends JsonDataType<D>, O> implements ITypeHolder<T> {
    private Map<ResourceLocation, T> types = Maps.newHashMap();
    protected O data;

    public void loadData(O data) {
        this.data = data;
    }

    @Override
    public void registerType(ResourceLocation name, T type) {
        this.types.put(name, type);
    }

    @Override
    public T getType(ResourceLocation id) {
        return this.types.get(id);
    }
}
