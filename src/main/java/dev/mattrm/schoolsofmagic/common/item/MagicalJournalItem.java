package dev.mattrm.schoolsofmagic.common.item;

import com.mojang.util.UUIDTypeAdapter;
import dev.mattrm.schoolsofmagic.common.util.lang.Styles;
import dev.mattrm.schoolsofmagic.common.util.lang.TooltipTranslation;
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

            if (MagicalJournalItem.getOwner(itemStack, world) == null) {
                // TODO: check if this is the proper way to use UUID
                nbt.putString("Owner", UUIDTypeAdapter.fromUUID(player.getGameProfile().getId()));
            } else {
                // TODO: Open GUI
            }
        }

        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag tooltipFlag) {
        super.addInformation(itemStack, world, tooltip, tooltipFlag);

        MagicalJournalItem.addOwnerTooltip(tooltip, MagicalJournalItem.getOwner(itemStack, world));
    }

    public static String getOwner(ItemStack itemStack, World worldIn) {
        CompoundNBT nbt = itemStack.getOrCreateTag();

        // TODO: check if this is the proper way to use UUID
        String uuidRaw = nbt.getString("Owner");
        if (uuidRaw.equals("")) {
            return null;
        } else {
            try {
                if (worldIn.getPlayerByUuid(UUIDTypeAdapter.fromString(uuidRaw)) != null) {
                    return worldIn.getPlayerByUuid(UUIDTypeAdapter.fromString(uuidRaw)).getName().getString();
                } else {
                    return null;
                }
            } catch (IllegalArgumentException e) {
                nbt.putString("Owner", "");
                return null;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addOwnerTooltip(List<ITextComponent> tooltip, String owner) {
        if (owner == null) {
            tooltip.add(TooltipTranslation.JOURNAL_NO_OWNER.componentTranslation().setStyle(Styles.RED));
        } else {
            tooltip.add(TooltipTranslation.JOURNAL_OWNER.componentTranslation(owner).setStyle(Styles.YELLOW));
        }
    }
}
