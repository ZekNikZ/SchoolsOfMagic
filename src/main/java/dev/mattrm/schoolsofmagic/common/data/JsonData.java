package dev.mattrm.schoolsofmagic.common.data;

import dev.mattrm.schoolsofmagic.common.network.IPacketSerializer;
import net.minecraft.util.ResourceLocation;

public abstract class JsonData<T extends JsonDataType<?>> implements IPacketSerializer {
    private ResourceLocation id;
    private T type;

    public JsonData(ResourceLocation id, T type) {
        this.id = id;
        this.type = type;
    }

    public final ResourceLocation getId() {
        return this.id;
    }

    public final T getType() {
        return this.type;
    }
}
