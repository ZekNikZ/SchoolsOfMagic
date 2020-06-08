package dev.mattrm.schoolsofmagic.common.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public abstract class JsonDataType<T extends JsonData<?>> {
    private final ResourceLocation id;

    public JsonDataType(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract T deserialize(ResourceLocation id, JsonObject json);
}
