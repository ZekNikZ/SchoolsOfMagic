package dev.mattrm.schoolsofmagic.common.data.schools.types;

import com.google.gson.JsonObject;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.schools.SimpleSchool;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SimpleSchoolType extends SchoolType {
    public SimpleSchoolType(ResourceLocation id) {
        super(id);
    }

    @Override
    public School deserialize(ResourceLocation id, JsonObject json) {
        // Textures
        ResourceLocation iconTexture = new ResourceLocation(JSONUtils.getString(json, "icon"));
        ResourceLocation backgroundTexture = new ResourceLocation(JSONUtils.getString(json, "journal_background"));

        return new SimpleSchool(id, iconTexture, backgroundTexture, this);
    }
}
