package dev.mattrm.schoolsofmagic.client.misc;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class ModItemGroups {
    public static final ModItemGroup SCHOOLS_OF_MAGIC_ITEM_GROUP = new ModItemGroup(GlobalConstants.MODID, () -> new ItemStack(ModItems.BLOCK_ITEMS.get("study_table")));

    public static class ModItemGroup extends ItemGroup {
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);

            this.iconSupplier = iconSupplier;
        }

        @Override
        public ItemStack createIcon() {
            return iconSupplier.get();
        }
    }
}
