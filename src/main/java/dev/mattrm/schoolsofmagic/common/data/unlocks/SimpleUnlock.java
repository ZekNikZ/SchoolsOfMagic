package dev.mattrm.schoolsofmagic.common.data.unlocks;

import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class SimpleUnlock extends Unlock {
    protected ItemStack iconItemStack;

    public SimpleUnlock(@Nonnull ResourceLocation id, @Nonnull School school, @Nonnull ResourceLocation icon, @Nonnull UnlockType type, @Nonnull List<ResourceLocation> parents, @Nonnull ResourceLocation linkedAdvancement, @Nonnull int priority, @Nonnull int points) {
        super(id, school, icon, type, parents, linkedAdvancement, priority, points);

        this.iconItemStack = new ItemStack(GameRegistry.findRegistry(Item.class).getValue(icon));
    }

    @Override
    public ItemStack getIconItemStack() {
        return iconItemStack;
    }
}
