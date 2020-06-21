package dev.mattrm.schoolsofmagic.common.data.unlocks.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.SimpleUnlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import dev.mattrm.schoolsofmagic.common.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SimpleUnlockType extends UnlockType {
    public SimpleUnlockType(ResourceLocation clientTexture, int clientTextureX, int clientTextureY) {
        super(null, clientTexture, clientTextureX, clientTextureY);
    }

    public SimpleUnlockType(String clientTexture, int clientTextureX, int clientTextureY) {
        this(new ResourceLocation(GlobalConstants.MODID, "textures/" + clientTexture), clientTextureX, clientTextureY);
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

        // Journal Stuff
        int priority = JSONUtils.getInt(json, "priority", 0);
        int points = JSONUtils.getInt(json, "points");

        return new SimpleUnlock(id, school, iconItem, this, parents, advancement, priority, points);
    }

    @Override
    public Unlock readFromBuffer(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();

        ResourceLocation schoolRaw = buffer.readResourceLocation();
        School school = SchoolsOfMagicMod.getInstance().getClientSchoolManager().getSchool(schoolRaw);
        ResourceLocation icon = buffer.readResourceLocation();
        ResourceLocation linkedAdvancement = buffer.readResourceLocation();

        int priority = buffer.readVarInt();
        int points = buffer.readVarInt();

        List<ResourceLocation> parents = PacketBufferUtils.readList(buffer, PacketBuffer::readResourceLocation);

        return new SimpleUnlock(id, school, icon, this, parents, linkedAdvancement, priority, points);
    }
}
