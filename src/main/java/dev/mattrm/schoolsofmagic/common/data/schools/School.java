package dev.mattrm.schoolsofmagic.common.data.schools;

import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Objects;

public abstract class School extends JsonData<SchoolType> {
    protected ResourceLocation icon;
    protected ResourceLocation journalBackground;
    protected int priority;

    public School(ResourceLocation id, ResourceLocation icon, ResourceLocation journalBackground, SchoolType type, int priority) {
        super(id, type);
        this.icon = icon;
        this.journalBackground = journalBackground;
        this.priority = priority;
    }

    public ITextComponent getName() {
        return new TranslationTextComponent("school." + this.getId().getNamespace() + "." + this.getId().getPath().replace('/', '.') + ".name");
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public ResourceLocation getJournalBackground() {
        return journalBackground;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.getType().getId());
        buffer.writeResourceLocation(this.getId());

        buffer.writeResourceLocation(this.icon);
        buffer.writeResourceLocation(this.journalBackground);

        buffer.writeVarInt(this.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof School)) return false;
        School school = (School) o;
        return priority == school.priority &&
                Objects.equals(icon, school.icon) &&
                Objects.equals(journalBackground, school.journalBackground) &&
                Objects.equals(this.getId(), school.getId()) &&
                Objects.equals(this.getType(), school.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, journalBackground, priority, this.getId(), this.getType());
    }

    @Override
    public String toString() {
        return "School{" +
                "id=" + this.getId() +
                ", type=" + this.getType() +
                ", icon=" + icon +
                ", journalBackground=" + journalBackground +
                ", priority=" + priority +
                '}';
    }
}
