package dev.mattrm.schoolsofmagic.common.data.schools.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.schools.SimpleSchool;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SimpleSchoolType extends SchoolType {
    public SimpleSchoolType() {
        super();
    }

    @Override
    public School deserialize(ResourceLocation id, JsonObject json) {
        // Textures
        ResourceLocation iconTexture = new ResourceLocation(JSONUtils.getString(json, "icon"));
        ResourceLocation backgroundTexture = new ResourceLocation(JSONUtils.getString(json, "journal_background"));

        int priority = JSONUtils.getInt(json, "priority", 9999);

        return new SimpleSchool(id, iconTexture, backgroundTexture, this, priority);
    }

    @Override
    public School readFromBuffer(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();

        // Textures
        ResourceLocation iconTexture = buffer.readResourceLocation();
        ResourceLocation backgroundTexture = buffer.readResourceLocation();

        int priority = buffer.readVarInt();

        return new SimpleSchool(id, iconTexture, backgroundTexture, this, priority);
    }
}
