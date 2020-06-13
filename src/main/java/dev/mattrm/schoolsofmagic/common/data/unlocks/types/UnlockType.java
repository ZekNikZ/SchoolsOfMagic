package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public abstract class UnlockType extends JsonDataType<Unlock> {
    private ResourceLocation clientTexture;
    private int clientTextureX;
    private int clientTextureY;

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
}
