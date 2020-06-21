package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public abstract class UnlockType extends JsonDataType<Unlock> implements IForgeRegistryEntry<UnlockType> {
    private final ResourceLocation clientTexture;
    private final int clientTextureX;
    private final int clientTextureY;

    public UnlockType(ResourceLocation id, ResourceLocation clientTexture, int clientTextureX, int clientTextureY) {
        super(id);
        this.clientTexture = clientTexture;
        this.clientTextureX = clientTextureX;
        this.clientTextureY = clientTextureY;
    }

    public ResourceLocation getClientTexture() {
        return clientTexture;
    }

    public int getClientTextureX() {
        return clientTextureX;
    }

    public int getClientTextureY() {
        return clientTextureY;
    }

    @Override
    public abstract Unlock deserialize(ResourceLocation id, JsonObject json);

    @Override
    public abstract Unlock readFromBuffer(PacketBuffer buffer);

    @Override
    public UnlockType setRegistryName(ResourceLocation name) {
        this.setId(name);
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return this.getId();
    }

    @Override
    public Class<UnlockType> getRegistryType() {
        return UnlockType.class;
    }
}
