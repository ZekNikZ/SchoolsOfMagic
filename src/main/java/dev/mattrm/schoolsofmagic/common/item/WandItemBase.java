package dev.mattrm.schoolsofmagic.common.item;

import dev.mattrm.schoolsofmagic.MainEventHandler;
import dev.mattrm.schoolsofmagic.client.misc.ModItemGroups;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.atomic.AtomicInteger;

public class WandItemBase extends Item {
    private static boolean usingWand = false;
    private static double sensitivity;
    public static double xOrigin;
    public static double yOrigin;

    public WandItemBase(Item.Properties props) {
        super(props);
    }

    /**
     * How long it takes to use or consume an item
     *
     * @param p_77626_1_
     */
    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!usingWand) {
            startUsingWand();
            playerIn.setActiveHand(handIn);
            playerIn.sendMessage(new StringTextComponent("STARTED TO USE"));
        }
        return ActionResult.resultConsume(playerIn.getHeldItem(handIn));
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     *
     * @param p_77615_1_
     * @param p_77615_2_
     * @param player
     * @param p_77615_4_
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity player, int p_77615_4_) {
        if (player instanceof PlayerEntity && usingWand) {
            stopUsingWand();
            player.sendMessage(new StringTextComponent("STOPPED USING"));
        }
        super.onPlayerStoppedUsing(p_77615_1_, p_77615_2_, player, p_77615_4_);
    }

    public static boolean isUsingWand () {
        return usingWand;
    }

    public static void startUsingWand() {
        xOrigin = Minecraft.getInstance().mouseHelper.getMouseX();
        yOrigin = Minecraft.getInstance().mouseHelper.getMouseY();
        usingWand = true;
        sensitivity = Minecraft.getInstance().gameSettings.mouseSensitivity;
        Minecraft.getInstance().gameSettings.mouseSensitivity = -1d / 3d;
        MainEventHandler._wandPoints.clear();
        MainEventHandler._wandPoints.add(new Pair(xOrigin, yOrigin));
        MainEventHandler.currentNumPoints = new AtomicInteger(1);
    }

    public static void stopUsingWand() {
        usingWand = false;
        Minecraft.getInstance().gameSettings.mouseSensitivity = sensitivity;
        MainEventHandler._wandPoints.clear();
        MainEventHandler.currentNumPoints.set(0);
    }
}
