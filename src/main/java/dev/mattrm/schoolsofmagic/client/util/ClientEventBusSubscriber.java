package dev.mattrm.schoolsofmagic.client.util;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.client.gui.MagicalWorkbenchScreen;
import dev.mattrm.schoolsofmagic.common.inventory.container.ModContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GlobalConstants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainerTypes.MAGICAL_WORKBENCH.get(), MagicalWorkbenchScreen::new);
    }
}
