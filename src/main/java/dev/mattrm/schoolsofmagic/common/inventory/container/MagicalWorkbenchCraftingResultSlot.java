package dev.mattrm.schoolsofmagic.common.inventory.container;

import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import dev.mattrm.schoolsofmagic.common.recipe.ModCrafting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;

public class MagicalWorkbenchCraftingResultSlot extends Slot {
    private final MagicalWorkbenchCraftingInventory craftMatrix;
    private final PlayerEntity player;
    private int amountCrafted;

    public MagicalWorkbenchCraftingResultSlot(PlayerEntity player, MagicalWorkbenchCraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
        super(inventoryIn, slotIndex, xPosition, yPosition);
        this.player = player;
        this.craftMatrix = craftingInventory;
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new stack.
     */
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    protected void onSwapCraft(int numItemsCrafted) {
        this.amountCrafted += numItemsCrafted;
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCrafting(this.player.world, this.player, this.amountCrafted);
//            net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerCraftingEvent(this.player, stack, this.craftMatrix);
        }

        if (this.inventory instanceof IRecipeHolder) {
            ((IRecipeHolder)this.inventory).onCrafting(this.player);
        }

        this.amountCrafted = 0;
    }

    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        this.onCrafting(stack);

        // Find the crafting recipe
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
//        NonNullList<ItemStack> recipeRemainingItems = NonNullList.withSize(this.craftMatrix.getSizeInventory(), ItemStack.EMPTY);
//        for (int i = 0; i < 7; i++) {
//            recipeRemainingItems.set(i, this.craftMatrix.getStackInSlot(i));
//        }
        NonNullList<ItemStack> recipeRemainingItems = thePlayer.world.getRecipeManager().getRecipeNonNull(ModCrafting.RecipeTypes.WORKBENCH_SHAPED, this.craftMatrix, thePlayer.world);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        // TODO: temporary fix until I can get it to actually work
//        recipeRemainingItems = NonNullList.withSize(8, ItemStack.EMPTY);

        // Apply the crafting recipe
        for(int i = 0; i < recipeRemainingItems.size() - 1; ++i) {
            ItemStack itemStackProvided = this.craftMatrix.getStackInSlot(i);
            ItemStack remainingItem = recipeRemainingItems.get(i);
            if (!itemStackProvided.isEmpty()) {
                this.craftMatrix.decrStackSize(i, 1);
                itemStackProvided = this.craftMatrix.getStackInSlot(i);
            }

            if (!remainingItem.isEmpty()) {
                if (itemStackProvided.isEmpty()) {
                    this.craftMatrix.setInventorySlotContents(i, remainingItem);
                } else if (ItemStack.areItemsEqual(itemStackProvided, remainingItem) && ItemStack.areItemStackTagsEqual(itemStackProvided, remainingItem)) {
                    remainingItem.grow(itemStackProvided.getCount());
                    this.craftMatrix.setInventorySlotContents(i, remainingItem);
                } else if (!this.player.inventory.addItemStackToInventory(remainingItem)) {
                    this.player.dropItem(remainingItem, false);
                }
            }
        }

        return stack;
    }
}
