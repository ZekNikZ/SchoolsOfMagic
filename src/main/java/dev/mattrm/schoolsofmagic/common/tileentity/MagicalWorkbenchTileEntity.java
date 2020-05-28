package dev.mattrm.schoolsofmagic.common.tileentity;

import dev.mattrm.schoolsofmagic.common.block.MagicalWorkbenchBlock;
import dev.mattrm.schoolsofmagic.common.inventory.container.MagicalWorkbenchContainer;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import dev.mattrm.schoolsofmagic.common.util.lang.ContainerTranslation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class MagicalWorkbenchTileEntity extends LockableLootTileEntity {
    private NonNullList<ItemStack> craftingInventory;
    private NonNullList<ItemStack> contents;
    protected int numPlayersUsing;
    private IItemHandlerModifiable items = createHandler();
    private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);

    public MagicalWorkbenchTileEntity() {
        this(ModTileEntityTypes.MAGICAL_WORKBENCH.get());
    }

    public MagicalWorkbenchTileEntity(TileEntityType<?> type) {
        super(type);

        this.craftingInventory = NonNullList.withSize(MagicalWorkbenchCraftingInventory.NUM_SLOTS + 1, ItemStack.EMPTY);

        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return contents;
    }

    public NonNullList<ItemStack> getCraftingInventory() {
        return craftingInventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.contents = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return ContainerTranslation.WORKBENCH_NAME.componentTranslation();
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new MagicalWorkbenchContainer(id, player, this);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);

        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.saveAllItems(nbt, this.contents);
        }
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);

        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndWrite(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.contents);
        }
    }

    private void playSound(SoundEvent sound) {
        double dx = (double) this.pos.getX() + 0.5D;
        double dy = (double) this.pos.getY() + 0.5D;
        double dz = (double) this.pos.getZ() + 0.5D;
        this.world.playSound(null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5f, this.world.rand.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }
    }

    protected void onOpenOrClose() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof MagicalWorkbenchBlock) {
            this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, block);
        }
    }

    public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
        BlockState blockState = reader.getBlockState(pos);
        if (blockState.hasTileEntity()) {
            TileEntity te = reader.getTileEntity(pos);
            if (te instanceof MagicalWorkbenchTileEntity) {
                return ((MagicalWorkbenchTileEntity) te).numPlayersUsing;
            }
        }
        return 0;
    }

    public static void swapContents(MagicalWorkbenchTileEntity te, MagicalWorkbenchTileEntity other) {
        NonNullList<ItemStack> list = te.getItems();
        te.setItems(other.getItems());
        other.setItems(list);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();

        if (this.itemHandler != null) {
            this.itemHandler.invalidate();
            this.itemHandler = null;
        }
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }

    @Override
    public void remove() {
        super.remove();

        if (itemHandler != null) {
            itemHandler.invalidate();
        }
    }
}
