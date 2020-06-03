package dev.mattrm.schoolsofmagic.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;

public final class ItemStackHelperExtensions {
    public static CompoundNBT saveAllItems(String tagName, CompoundNBT tag, NonNullList<ItemStack> list) {
        return saveAllItems(tagName, tag, list, true);
    }
    
    public static CompoundNBT saveAllItems(String tagName, CompoundNBT tag, NonNullList<ItemStack> list, boolean saveEmpty) {
        ListNBT storedList = new ListNBT();

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = (ItemStack)list.get(i);
            if (!itemStack.isEmpty()) {
                CompoundNBT itemStackNBT = new CompoundNBT();
                itemStackNBT.putByte("Slot", (byte)i);
                itemStack.write(itemStackNBT);
                storedList.add(itemStackNBT);
            }
        }

        if (!storedList.isEmpty() || saveEmpty) {
            tag.put(tagName, storedList);
        }

        return tag;
    }

    public static void loadAllItems(String tagName, CompoundNBT tag, NonNullList<ItemStack> list) {
        ListNBT listNBT = tag.getList(tagName, 10);

        for(int i = 0; i < listNBT.size(); ++i) {
            CompoundNBT itemNBT = listNBT.getCompound(i);
            int slot = itemNBT.getByte("Slot") & 255;
            if (slot >= 0 && slot < list.size()) {
                list.set(slot, ItemStack.read(itemNBT));
            }
        }
    }
}
