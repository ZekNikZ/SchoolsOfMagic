package dev.mattrm.schoolsofmagic.common.util.lang;

import dev.mattrm.schoolsofmagic.GlobalConstants;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public enum TooltipTranslation implements ITranslationProvider {
    JOURNAL_OWNER("journal.owner", 1),
    JOURNAL_NO_OWNER("journal.no_owner", 0),
    JOURNAL_LOADING_OWNER("journal.loading_owner", 0),
    ;

    private static final String PREFIX = "tooltip.";
    private final String key;
    private final int argCount;

    TooltipTranslation(@Nonnull String key, @Nonnegative int argCount) {
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