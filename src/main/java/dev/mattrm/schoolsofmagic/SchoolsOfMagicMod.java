package dev.mattrm.schoolsofmagic;

import dev.mattrm.schoolsofmagic.client.misc.ModItemGroups;
import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import dev.mattrm.schoolsofmagic.common.cache.UsernameCache;
import dev.mattrm.schoolsofmagic.common.inventory.container.ModContainerTypes;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.recipe.ModCrafting;
import dev.mattrm.schoolsofmagic.common.tileentity.ModTileEntityTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GlobalConstants.MODID)
public class SchoolsOfMagicMod {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SchoolsOfMagicMod() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Forge mod hooks
        // TODO: refractor these elsewhere
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);

        // Register all mod objects
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModCrafting.Recipes.RECIPE_SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // PreInit code
        LOGGER.info("Enabling username cache...");
        UsernameCache.initCache(100);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Client setup
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Inter-mod communication dispatch
    }

    private void processIMC(final InterModProcessEvent event) {
        // Inter-mod communication receiving and processing
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Server starting with Schools of Magic Mod enabled");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new item here
            LOGGER.info("HELLO from Register Item");

            ModBlocks.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .forEach(block -> {
                                final Item.Properties properties = new Item.Properties().group(ModItemGroups.SCHOOLS_OF_MAGIC_ITEM_GROUP);
                                final BlockItem blockItem = new BlockItem(block, properties);
                                blockItem.setRegistryName(block.getRegistryName());
                                ModItems.BLOCK_ITEMS.put(block.getRegistryName().getPath(), blockItem);
                                itemRegistryEvent.getRegistry().register(blockItem);
                            }
                    );
        }
    }
}
