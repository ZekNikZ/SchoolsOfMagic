package dev.mattrm.schoolsofmagic.common.data.unlocks;

import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.item.ItemStack;
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

    public Unlock(@Nonnull ResourceLocation id, @Nonnull School school, @Nonnull ResourceLocation icon, @Nonnull UnlockType type, @Nonnull List<ResourceLocation> parents, @Nonnull ResourceLocation linkedAdvancement) {
        super(id, type);
        this.school = school;
        this.icon = icon;
        this.parents = parents;
        this.linkedAdvancement = linkedAdvancement;
    }

    public School getSchool() {
        return this.school;
    }

    public final ITextComponent getName() {
        return new TranslationTextComponent("unlock." + this.getId().getNamespace()  + "." + this.getId().getPath().replace('/','.') + ".name");
    }

    public final ITextComponent getDescription() {
        return new TranslationTextComponent("unlock." + this.getId().getNamespace()  + "." + this.getId().getPath().replace('/','.') + ".description");
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

    public abstract ItemStack getIconItemStack();
}
