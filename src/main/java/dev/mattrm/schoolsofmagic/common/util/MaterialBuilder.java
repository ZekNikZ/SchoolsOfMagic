package dev.mattrm.schoolsofmagic.common.util;


import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public class MaterialBuilder extends Material.Builder {
    private PushReaction pushReaction;
    private boolean blocksMovement;
    private boolean canBurn;
    private boolean requiresNoTool;
    private boolean isLiquid;
    private boolean isReplaceable;
    private boolean isSolid;
    private final MaterialColor color;
    private boolean isOpaque;

    public MaterialBuilder(MaterialColor matColor) {
        super(matColor);
        this.pushReaction = PushReaction.NORMAL;
        this.blocksMovement = true;
        this.requiresNoTool = true;
        this.isSolid = true;
        this.isOpaque = true;
        this.color = matColor;
    }

    @Override
    public MaterialBuilder liquid() {
        this.isLiquid = true;
        return this;
    }

    @Override
    public MaterialBuilder notSolid() {
        this.isSolid = false;
        return this;
    }

    @Override
    public MaterialBuilder doesNotBlockMovement() {
        this.blocksMovement = false;
        return this;
    }

    public MaterialBuilder notOpaque() {
        this.isOpaque = false;
        return this;
    }

    @Override
    public MaterialBuilder requiresTool() {
        this.requiresNoTool = false;
        return this;
    }

    @Override
    public MaterialBuilder flammable() {
        this.canBurn = true;
        return this;
    }

    @Override
    public MaterialBuilder replaceable() {
        this.isReplaceable = true;
        return this;
    }

    @Override
    public MaterialBuilder pushDestroys() {
        this.pushReaction = PushReaction.DESTROY;
        return this;
    }

    @Override
    public MaterialBuilder pushBlocks() {
        this.pushReaction = PushReaction.BLOCK;
        return this;
    }

    @Override
    public Material build() {
        return new Material(this.color, this.isLiquid, this.isSolid, this.blocksMovement, this.isOpaque, this.requiresNoTool, this.canBurn, this.isReplaceable, this.pushReaction);
    }
}
