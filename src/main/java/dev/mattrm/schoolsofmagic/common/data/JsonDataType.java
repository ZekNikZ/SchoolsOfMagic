package dev.mattrm.schoolsofmagic.common.data;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.network.IPacketDeserializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class JsonDataType<T extends JsonData<?>> implements IPacketDeserializer<T> {
    private ResourceLocation id;

    public JsonDataType(ResourceLocation id) {
        this.id = id;
    }

    protected void setId(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return id;
    }

    public abstract T deserialize(ResourceLocation id, JsonObject json);
}
