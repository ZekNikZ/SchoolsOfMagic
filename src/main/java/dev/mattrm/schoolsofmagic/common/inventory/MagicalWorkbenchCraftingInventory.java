package dev.mattrm.schoolsofmagic.common.inventory;

import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.tileentity.MagicalWorkbenchTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Iterator;

public class MagicalWorkbenchCraftingInventory implements IInventory {
    public static final int NUM_SLOTS = 7;
    public static final int JOURNAL_SLOT = NUM_SLOTS;

    private final NonNullList<ItemStack> stackList;
    private final Container eventHandler;
    private final MagicalWorkbenchTileEntity tileEntity;

    public MagicalWorkbenchCraftingInventory(Container container, MagicalWorkbenchTileEntity tileEntity) {
        this.stackList = tileEntity.getCraftingInventory();
        this.eventHandler = container;
        this.tileEntity = tileEntity;
    }

    @Override
    public int getSizeInventory() {
        return NUM_SLOTS + 1;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stackList) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.stackList.get(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.stackList, index);
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {
        ItemStack itemStack = ItemStackHelper.getAndSplit(this.stackList, index, amount);
        if (!itemStack.isEmpty()) {
            this.eventHandler.onCraftMatrixChanged(this);
        }

        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack itemStack) {
        this.stackList.set(index, itemStack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public void markDirty() {
        this.tileEntity.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerUsing) {
        return true;
    }

    @Override
    public void clear() {
        this.stackList.clear();
    }

    @Override
    public void openInventory(PlayerEntity player) {
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == JOURNAL_SLOT) {
            return stack.getItem() == ModItems.MAGICAL_JOURNAL.get();
        }

        return true;
    }
}
