package dev.mattrm.schoolsofmagic.common.data.schools;

import dev.mattrm.schoolsofmagic.common.data.JsonData;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class School extends JsonData<SchoolType> {
    protected ResourceLocation icon;
    protected ResourceLocation journalBackground;

    public School(ResourceLocation id, ResourceLocation icon, ResourceLocation journalBackground, SchoolType type) {
        super(id, type);
        this.icon = icon;
        this.journalBackground = journalBackground;
    }

    public ITextComponent getName() {
        return new TranslationTextComponent("school." + this.getId().getNamespace()  + "." + this.getId().getPath().replace('/','.') + ".name");
    }
}
