package dev.mattrm.schoolsofmagic.common.util.lang;

import dev.mattrm.schoolsofmagic.GlobalConstants;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public enum GuiTranslation implements ITranslationProvider {
    JOURNAL_GUI("journal", 1),
    ;

    private static final String PREFIX = "gui.";
    private final String key;
    private final int argCount;

    GuiTranslation(@Nonnull String key, @Nonnegative int argCount) {
        this.key = PREFIX + GlobalConstants.MODID + "." + key;
        this.argCount = argCount;
    }

    @Override
    public boolean areValidArguments(Object... args) {
        return args.length == argCount;
    }

    @Override
    public String getTranslationKey() {
        return key;
    }
}