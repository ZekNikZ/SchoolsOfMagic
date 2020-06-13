package dev.mattrm.schoolsofmagic.common.data.unlocks;

import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import dev.mattrm.schoolsofmagic.common.util.PacketBufferUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class Unlock extends JsonData<UnlockType> {
    protected final School school;
    protected final ResourceLocation icon;
    protected final List<ResourceLocation> parents;
    protected final ResourceLocation linkedAdvancement;
    protected final int priority;
    protected final int points;

    public Unlock(@Nonnull ResourceLocation id, @Nonnull School school, @Nonnull ResourceLocation icon, @Nonnull UnlockType type, @Nonnull List<ResourceLocation> parents, @Nonnull ResourceLocation linkedAdvancement, @Nonnull int priority, @Nonnull int points) {
        super(id, type);
        this.school = school;
        this.icon = icon;
        this.parents = parents;
        this.linkedAdvancement = linkedAdvancement;
        this.priority = priority;
        this.points = points;
    }

    public School getSchool() {
        return this.school;
    }

    public final ITextComponent getName() {
        return new TranslationTextComponent("unlock." + this.getId().getNamespace() + "." + this.getId().getPath().replace('/', '.') + ".name");
    }

    public final ITextComponent getDescription() {
        return new TranslationTextComponent("unlock." + this.getId().getNamespace() + "." + this.getId().getPath().replace('/', '.') + ".description");
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public List<ResourceLocation> getParents() {
        return this.parents;
    }

    public ResourceLocation getLinkedAdvancement() {
        return this.linkedAdvancement;
    }

    public int getPriority() {
        return priority;
    }

    public int getPoints() {
        return points;
    }

    public abstract ItemStack getIconItemStack();

    @Override
    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.getType().getId());
        buffer.writeResourceLocation(this.getId());

        buffer.writeResourceLocation(this.school.getId());
        buffer.writeResourceLocation(this.icon);
        buffer.writeResourceLocation(this.linkedAdvancement);

        buffer.writeVarInt(this.priority);
        buffer.writeVarInt(this.points);

        PacketBufferUtils.writeList(buffer, this.parents, (el, b) -> b.writeResourceLocation(el));
    }
}
