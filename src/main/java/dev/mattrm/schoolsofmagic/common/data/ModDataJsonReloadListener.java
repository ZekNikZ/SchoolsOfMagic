package dev.mattrm.schoolsofmagic.common.data;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class ModDataJsonReloadListener<D extends JsonData<T>, T extends JsonDataType<D> & IForgeRegistryEntry<T>> extends JsonReloadListener implements ITypeHolder<T> {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();

    private Class<T> typeClass;
    private final String folder;

    public ModDataJsonReloadListener(String folder) {
        super(GSON, folder);
        this.folder = folder;
    }

    public void setTypeClass(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    protected final D deserializeData(ResourceLocation id, JsonObject json) {
        ResourceLocation typeRaw = ResourceLocation.tryCreate(JSONUtils.getString(json, "type"));
        if (typeRaw == null) {
            throw new JsonSyntaxException("Invalid data type '" + typeRaw + "'");
        }

        T type = this.getType(typeRaw);
        if (type == null) {
            throw new JsonSyntaxException("Unsupported " + this.folder + " type '" + typeRaw + "'");
        }

        return type.deserialize(id, json);
    }

    @Override
    public T getType(ResourceLocation id) {
        return GameRegistry.findRegistry(typeClass).getValue(id);
    }
}
