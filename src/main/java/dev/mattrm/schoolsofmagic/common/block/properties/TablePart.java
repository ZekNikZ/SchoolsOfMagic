package dev.mattrm.schoolsofmagic.common.block.properties;

import net.minecraft.util.IStringSerializable;

public enum TablePart implements IStringSerializable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    private TablePart(String p_i49342_3_) {
        this.name = p_i49342_3_;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}