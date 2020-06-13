package dev.mattrm.schoolsofmagic.common.data;

import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class JsonDataProxy<T> {
    private T data;

    public JsonDataProxy(T data) {
        this.data = data;
    }

    public final T getData() {
        return this.data;
    }

    protected final void setData(T data) {
        this.data = data;
    }

    protected abstract void loadData(ResourceLocation id);
}
