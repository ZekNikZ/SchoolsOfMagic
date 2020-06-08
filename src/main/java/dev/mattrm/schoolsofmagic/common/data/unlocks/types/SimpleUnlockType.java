package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.SimpleUnlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SimpleUnlockType extends UnlockType {
    public SimpleUnlockType(ResourceLocation id) {
        super(id);
    }

    @Override
    public Unlock deserialize(ResourceLocation id, JsonObject json) {
        // School
        ResourceLocation schoolRaw = new ResourceLocation(JSONUtils.getString(json, "school"));
        School school = SchoolsOfMagicMod.getInstance().getSchoolManager().getSchool(schoolRaw);
        if (school == null) {
            throw new JsonSyntaxException("Unsupported school '" + schoolRaw + "'");
        }

        // Icon
        ResourceLocation iconItem = new ResourceLocation(JSONUtils.getString(json, "icon_item"));

        // Parents
        JsonArray parentsRaw = JSONUtils.getJsonArray(json, "parents");
        List<ResourceLocation> parents = new ArrayList<>();
        for (JsonElement parent : parentsRaw) {
            parents.add(new ResourceLocation(parent.getAsString()));
        }

        // Linked Advancements
        ResourceLocation advancement = new ResourceLocation(JSONUtils.getString(json, "linked_advancement"));

        return new SimpleUnlock(id, school, iconItem, this, parents, advancement);
    }
}
