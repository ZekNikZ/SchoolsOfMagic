package dev.mattrm.schoolsofmagic.common.item;

import dev.mattrm.schoolsofmagic.client.gui.JournalScreen;
import dev.mattrm.schoolsofmagic.common.cache.UsernameCache;
import dev.mattrm.schoolsofmagic.common.util.lang.GuiTranslation;
import dev.mattrm.schoolsofmagic.common.util.lang.Styles;
import dev.mattrm.schoolsofmagic.common.util.lang.TooltipTranslation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MagicalJournalItem extends Item {
    public MagicalJournalItem(Properties props) {
        super(props);
    }

    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

    public boolean isEnchantable(ItemStack itemStack) {
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (player != null) {
            ItemStack itemStack = player.getHeldItem(hand);
            CompoundNBT nbt = itemStack.getOrCreateTag();

            String owner = MagicalJournalItem.getOwner(itemStack);
            if (owner == null) {
                nbt.putString("Owner", player.getGameProfile().getId().toString());
            } else {
                if (world.isRemote) {
                    Minecraft.getInstance().displayGuiScreen(new JournalScreen(MagicalJournalItem.getOwnerUUID(itemStack), GuiTranslation.JOURNAL_GUI.componentTranslation(owner)));
                }
            }
        }

        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        MagicalJournalItem.addOwnerTooltip(tooltip, MagicalJournalItem.getOwner(itemStack));
    }

    public static UUID getOwnerUUID(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();

        // TODO: check if this is the proper way to use UUID
        String uuidRaw = nbt.getString("Owner");
        if (uuidRaw.equals("")) {
            return null;
        } else {
            try {
                return UUID.fromString(uuidRaw);
            } catch (IllegalArgumentException e) {
                nbt.putString("Owner", "");
                return null;
            }
        }
    }

    public static String getOwner(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();

        // TODO: check if this is the proper way to use UUID
        String uuidRaw = nbt.getString("Owner");
        if (uuidRaw.equals("")) {
            return null;
        } else {
            try {
                UUID uuid = UUID.fromString(uuidRaw);
                return UsernameCache.get(uuid).get();
            } catch (IllegalArgumentException e) {
                nbt.putString("Owner", "");
                return null;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
//                return "__loading";
                return uuidRaw;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addOwnerTooltip(List<ITextComponent> tooltip, String owner) {
        if (owner == null) {
            tooltip.add(TooltipTranslation.JOURNAL_NO_OWNER.componentTranslation().setStyle(Styles.RED));
        } else if (owner.equals("__loading")) {
            tooltip.add(TooltipTranslation.JOURNAL_LOADING_OWNER.componentTranslation().setStyle(Styles.YELLOW.setItalic(true)));
        } else {
            tooltip.add(TooltipTranslation.JOURNAL_OWNER.componentTranslation(owner).setStyle(Styles.YELLOW));
        }
    }
}
