package dev.mattrm.schoolsofmagic.common.inventory.container;

import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import dev.mattrm.schoolsofmagic.common.block.StudyTableBlockBase;
import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftResultInventory;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import dev.mattrm.schoolsofmagic.common.item.MagicalJournalItem;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.recipe.ModCrafting;
import dev.mattrm.schoolsofmagic.common.recipe.ShapedWorkbenchRecipe;
import dev.mattrm.schoolsofmagic.common.recipe.ShapelessWorkbenchRecipe;
import dev.mattrm.schoolsofmagic.common.tileentity.MagicalWorkbenchTileEntity;
import dev.mattrm.schoolsofmagic.common.tileentity.StudyTableTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

public class StudyTableContainer extends Container {
    private static final int LEXICON_SLOT = 0;
    private static final int JOURNAL_SLOT = 1;
    private static final int ITEM_SLOT = 2;
    private static final int PLAYER_INV_SLOTS_MIN = 3;
    private static final int PLAYER_INV_SLOTS_MAX = 29;
    private static final int PLAYER_HOTBAR_SLOTS_MIN = 30;
    private static final int PLAYER_HOTBAR_SLOTS_MAX = 38;

    public final StudyTableTileEntity tileEntity;
    private final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;

    public StudyTableContainer(final int id, final PlayerInventory playerInventoryIn, final StudyTableTileEntity teStudyTable) {
        super(ModContainerTypes.STUDY_TABLE.get(), id);
        this.tileEntity = teStudyTable;
        this.worldPosCallable = IWorldPosCallable.of(teStudyTable.getWorld(), this.tileEntity.getPos());
        this.player = playerInventoryIn.player;

        // Lexicon Slot
        int currSlot = 0;
        int lexiconX = 23;
        int lexiconY = 185;
        this.addSlot(new Slot(this.tileEntity, currSlot++, lexiconX, lexiconY) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.MAGICAL_JOURNAL.get() && MagicalJournalItem.getOwnerUUID(stack) != null;
            }
        });

        // Journal Slot
        int journalX = 49;
        int journalY = 185;
        this.addSlot(new Slot(this.tileEntity, currSlot++, journalX, journalY) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.MAGICAL_JOURNAL.get() && MagicalJournalItem.getOwnerUUID(stack) != null;
            }
        });

        // Item Slot
        int itemX = 36;
        int itemY = 152;
        this.addSlot(new Slot(this.tileEntity, currSlot++, itemX, itemY) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == ModItems.MAGICAL_JOURNAL.get() && MagicalJournalItem.getOwnerUUID(stack) != null;
            }
        });

        // Player Inventory
        int playerInvStartX = 88;
        int playerInvStartY = 138;
        int slotSize = 16;
        int margin = 2;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventoryIn, row * 9 + col + 9, playerInvStartX + col * (slotSize + margin), playerInvStartY + row * (slotSize + margin)));
            }
        }

        // Player Hotbar
        int playerHotbarStartX = 88;
        int playerHotbarStartY = 196;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventoryIn, col, playerHotbarStartX + col * (slotSize + margin), playerHotbarStartY));
        }

        // Ask server for current advancements
        if (this.tileEntity.getWorld().isRemote) {
            ItemStack journalSlot = this.tileEntity.getStackInSlot(StudyTableContainer.JOURNAL_SLOT);
            if (journalSlot.getItem() == ModItems.MAGICAL_JOURNAL.get()) {
                AdvancementCache.getClientInstance().loadAllForPlayer(MagicalJournalItem.getOwnerUUID(journalSlot));
            }
        }
    }

    public StudyTableContainer(final int id, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(id, playerInventory, getTileEntity(playerInventory, data));
    }

    private static StudyTableTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "player inventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof StudyTableTileEntity) {
            return ((StudyTableTileEntity) tileAtPos);
        }
        throw new IllegalStateException("Tile entity is not correct: " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(worldPosCallable, player, ModBlocks.STUDY_TABLE.get());
    }

    // TODO: fix
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
//        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//
//        if (slot != null && slot.getHasStack()) {
//            ItemStack itemStackInSlot = slot.getStack();
//            itemStack = itemStackInSlot.copy();
//
//            if (index == RESULT_SLOT) {
//                this.worldPosCallable.consume((world, blockPos) -> {
//                    itemStackInSlot.getItem().onCreated(itemStackInSlot, world, playerIn);
//                });
//
//                if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, true)) {
//                    return ItemStack.EMPTY;
//                }
//
//                slot.onSlotChange(itemStackInSlot, itemStack);
//            } else if (index >= PLAYER_INV_SLOTS_MIN && index <= PLAYER_HOTBAR_SLOTS_MAX) {
//                if (!this.mergeItemStack(itemStackInSlot, JOURNAL_SLOT, TABLE_INV_SLOTS_MAX + 1, false)) {
//                    if (index < PLAYER_HOTBAR_SLOTS_MIN) {
//                        if (!this.mergeItemStack(itemStackInSlot, PLAYER_HOTBAR_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, false)) {
//                            return ItemStack.EMPTY;
//                        }
//                    } else if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_INV_SLOTS_MAX + 1, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//            } else if (!this.mergeItemStack(itemStackInSlot, PLAYER_INV_SLOTS_MIN, PLAYER_HOTBAR_SLOTS_MAX + 1, false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemStackInSlot.isEmpty()) {
//                slot.putStack(ItemStack.EMPTY);
//            } else {
//                slot.onSlotChanged();
//            }
//
//            if (itemStackInSlot.getCount() == itemStack.getCount()) {
//                return ItemStack.EMPTY;
//            }
//
//            ItemStack itemStack2 = slot.onTake(playerIn, itemStackInSlot);
//            if (index == RESULT_SLOT) {
//                playerIn.dropItem(itemStack2, false);
//            }
//        }
//
//        return itemStack;
        return ItemStack.EMPTY;
    }
}
