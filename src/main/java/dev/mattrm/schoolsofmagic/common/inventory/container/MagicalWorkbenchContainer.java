package dev.mattrm.schoolsofmagic.common.inventory.container;

import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.cache.ClientAdvancementCache;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftResultInventory;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import dev.mattrm.schoolsofmagic.common.item.MagicalJournalItem;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.recipe.ModCrafting;
import dev.mattrm.schoolsofmagic.common.recipe.ShapedWorkbenchRecipe;
import dev.mattrm.schoolsofmagic.common.tileentity.MagicalWorkbenchTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MagicalWorkbenchContainer extends Container {
    private static final int RESULT_SLOT = 0;
    private static final int JOURNAL_SLOT = 1;
    private static final int CRAFT_MATRIX_SLOTS_MIN = 2;
    private static final int CRAFT_MATRIX_SLOTS_MAX = 8;
    private static final int TABLE_INV_SLOTS_MIN = 9;
    private static final int TABLE_INV_SLOTS_MAX = 17;
    private static final int PLAYER_INV_SLOTS_MIN = 18;
    private static final int PLAYER_INV_SLOTS_MAX = 44;
    private static final int PLAYER_HOTBAR_SLOTS_MIN = 45;
    private static final int PLAYER_HOTBAR_SLOTS_MAX = 53;

    public final MagicalWorkbenchTileEntity tileEntity;
    private final IWorldPosCallable worldPosCallable;
    private final MagicalWorkbenchCraftingInventory craftMatrix;
    private final MagicalWorkbenchCraftResultInventory craftResult;
    private final PlayerEntity player;

    public MagicalWorkbenchContainer(final int id, final PlayerInventory playerInventoryIn, final MagicalWorkbenchTileEntity teMagicalWorkbench) {
        super(ModContainerTypes.MAGICAL_WORKBENCH.get(), id);
        this.tileEntity = teMagicalWorkbench;
        this.worldPosCallable = IWorldPosCallable.of(teMagicalWorkbench.getWorld(), this.tileEntity.getPos());
        this.craftMatrix = new MagicalWorkbenchCraftingInventory(this, this.tileEntity);
        this.craftResult = new MagicalWorkbenchCraftResultInventory();
        this.player = playerInventoryIn.player;

        // Result Slot
        this.addSlot(new MagicalWorkbenchCraftingResultSlot(playerInventoryIn.player, this.craftMatrix, this.craftResult, 0, 124, 38));

        // Journal Slot
        int curr_index = 0;
        int journalX = 93;
        int journalY = 18;
        this.addSlot(new Slot(this.craftMatrix, MagicalWorkbenchCraftingInventory.JOURNAL_SLOT, journalX, journalY) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.MAGICAL_JOURNAL.get() && MagicalJournalItem.getOwnerUUID(stack) != null;
            }
        });

        // Crafting Grid
        int row1X = 39;
        int row1Y = 20;
        int slotSize = 16;
        int margin = 2;
        for (int i = 0; i < 2; i++) {  // first row
            this.addSlot(new Slot(this.craftMatrix, curr_index++, row1X + i * (slotSize + margin), row1Y));
        }
        int row2X = 30;
        int row2Y = 38;
        for (int i = 0; i < 3; i++) {  // second row
            this.addSlot(new Slot(this.craftMatrix, curr_index++, row2X + i * (slotSize + margin), row2Y));
        }
        int row3X = 39;
        int row3Y = 56;
        for (int i = 0; i < 2; i++) {  // third row
            this.addSlot(new Slot(this.craftMatrix, curr_index++, row3X + i * (slotSize + margin), row3Y));
        }

        // Extra Table Inventory
        int extraInvStartX = 8;
        int extraInvStartY = 84;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(this.tileEntity, col, extraInvStartX + col * (slotSize + margin), extraInvStartY));
        }

        // Player Inventory
        int playerInvStartX = 8;
        int playerInvStartY = 111;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventoryIn, row * 9 + col + 9, playerInvStartX + col * (slotSize + margin), playerInvStartY + row * (slotSize + margin)));
            }
        }

        // Player Hotbar
        int playerHotbarStartX = 8;
        int playerHotbarStartY = 169;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventoryIn, col, playerHotbarStartX + col * (slotSize + margin), playerHotbarStartY));
        }

        // Update result slot
        this.onCraftMatrixChanged(this.craftMatrix);

        // Ask server for current advancements
        if (this.tileEntity.getWorld().isRemote) {
            ItemStack journalSlot = this.craftMatrix.getStackInSlot(MagicalWorkbenchCraftingInventory.JOURNAL_SLOT);
            if (journalSlot.getItem() == ModItems.MAGICAL_JOURNAL.get()) {
                ((ClientAdvancementCache) AdvancementCache.getClientInstance()).loadAllForPlayer(MagicalJournalItem.getOwnerUUID(journalSlot));
            }
        }
    }

    public MagicalWorkbenchContainer(final int id, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(id, playerInventory, getTileEntity(playerInventory, data));
    }

    private static MagicalWorkbenchTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "player inventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof MagicalWorkbenchTileEntity) {
            return ((MagicalWorkbenchTileEntity) tileAtPos);
        }
        throw new IllegalStateException("Tile entity is not correct: " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPosCallable, player, ModBlocks.MAGICAL_WORKBENCH.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStackInSlot = slot.getStack();
            itemStack = itemStackInSlot.copy();

            if (index == RESULT_SLOT) {
                this.worldPosCallable.consume((world, blockPos) -> {
                    itemStackInSlot.getItem().onCreated(itemStackInSlot, world, playerIn);
                });

                if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStackInSlot, itemStack);
            } else if (index >= PLAYER_INV_SLOTS_MIN && index <= PLAYER_HOTBAR_SLOTS_MAX) {
                if (!this.mergeItemStack(itemStackInSlot, JOURNAL_SLOT, TABLE_INV_SLOTS_MAX + 1, false)) {
                    if (index < PLAYER_HOTBAR_SLOTS_MIN) {
                        if (!this.mergeItemStack(itemStackInSlot, PLAYER_HOTBAR_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_INV_SLOTS_MAX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemStackInSlot.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack2 = slot.onTake(playerIn, itemStackInSlot);
            if (index == RESULT_SLOT) {
                playerIn.dropItem(itemStack2, false);
            }
        }

        return itemStack;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.worldPosCallable.consume((p_217069_1_, p_217069_2_) -> {
            updateCraftingResult(this.windowId, p_217069_1_, this.player, this.craftMatrix, this.craftResult);
        });
    }

    protected static void updateCraftingResult(int id, World worldIn, PlayerEntity playerIn, MagicalWorkbenchCraftingInventory inventoryIn, MagicalWorkbenchCraftResultInventory inventoryResult) {
        if (!worldIn.isRemote) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) playerIn;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ShapedWorkbenchRecipe> optional = worldIn.getServer().getRecipeManager().getRecipe(ModCrafting.RecipeTypes.WORKBENCH_SHAPED, inventoryIn, worldIn);
            if (optional.isPresent()) {
                ShapedWorkbenchRecipe recipe = optional.get();
                if (inventoryResult.canUseRecipe(worldIn, serverplayerentity, recipe)) {
                    itemstack = recipe.getCraftingResult(inventoryIn);
                }
            }
//            if (ItemStack.areItemsEqual(inventoryIn.getStackInSlot(3), new ItemStack(Items.DIAMOND, 2))
//                    && inventoryIn.getStackInSlot(3).getCount() >= 2
//                    && IntStream.of(0,1,2,4,5,6).allMatch(i -> inventoryIn.getStackInSlot(i).isEmpty())) {
//                itemstack = new ItemStack(Items.DIAMOND_SWORD);
//            }

            inventoryResult.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 0, itemstack));
        }
    }

    // auto update crafting result
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
        if (slotId >= CRAFT_MATRIX_SLOTS_MIN && slotId <= CRAFT_MATRIX_SLOTS_MAX) {
            this.onCraftMatrixChanged(this.craftMatrix);
        }
        return result;
    }
}
