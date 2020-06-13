package dev.mattrm.schoolsofmagic.common.data.schools;

import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class SimpleSchool extends School {
    public SimpleSchool(ResourceLocation id, ResourceLocation icon, ResourceLocation journalBackground, SchoolType type, int priority) {
        super(id, icon, journalBackground, type, priority);
    }
}
