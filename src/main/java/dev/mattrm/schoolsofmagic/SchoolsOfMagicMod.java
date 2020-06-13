package dev.mattrm.schoolsofmagic;

import dev.mattrm.schoolsofmagic.client.data.ClientDataManager;
import dev.mattrm.schoolsofmagic.client.data.ClientSchoolManager;
import dev.mattrm.schoolsofmagic.client.data.unlocks.ClientUnlockNodesManager;
import dev.mattrm.schoolsofmagic.client.misc.ModItemGroups;
import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import dev.mattrm.schoolsofmagic.common.cache.*;
import dev.mattrm.schoolsofmagic.common.data.JsonDataType;
import dev.mattrm.schoolsofmagic.common.data.ModDataJsonReloadListener;
import dev.mattrm.schoolsofmagic.common.data.schools.SchoolManager;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SimpleSchoolType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.SimpleUnlockType;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import dev.mattrm.schoolsofmagic.common.inventory.container.ModContainerTypes;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import dev.mattrm.schoolsofmagic.common.network.SchoolsOfMagicPacketHandler;
import dev.mattrm.schoolsofmagic.common.recipe.ModCrafting;
import dev.mattrm.schoolsofmagic.common.tileentity.ModTileEntityTypes;
import dev.mattrm.schoolsofmagic.common.data.unlocks.UnlockManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(GlobalConstants.MODID)
public class SchoolsOfMagicMod {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: find a better way to do this
    private static SchoolsOfMagicMod instance;


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

        instance = this;
    }

    private void setup(final FMLCommonSetupEvent event) {
        // PreInit code
        LOGGER.info("Initializing username cache...");
        UsernameCache.initCache(100);

        // Force Java to load packet system
        SchoolsOfMagicPacketHandler.setup();

        this.registerType(SchoolType.class, new SimpleSchoolType(new ResourceLocation(GlobalConstants.MODID, "school/normal")));
        this.registerType(UnlockType.class, new SimpleUnlockType(new ResourceLocation(GlobalConstants.MODID, "unlock/recipe"), "gui/journal/widgets.png", 0, 0));
        this.registerType(UnlockType.class, new SimpleUnlockType(new ResourceLocation(GlobalConstants.MODID, "unlock/spell"), "gui/journal/widgets.png", 26, 0));
        this.registerType(UnlockType.class, new SimpleUnlockType(new ResourceLocation(GlobalConstants.MODID, "unlock/ability"), "gui/journal/widgets.png", 52, 0));
        this.registerType(UnlockType.class, new SimpleUnlockType(new ResourceLocation(GlobalConstants.MODID, "unlock/ritual"), "gui/journal/widgets.png", 78, 0));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Client setup
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        LOGGER.info("Initializing client advancement cache...");
        AdvancementCache.invalidateClient();
        AdvancementCache.initClientCache(new ClientAdvancementCache());

        // Setup managers
        this.registerClientJSONDataManager("schools", this.clientSchoolManager = new ClientSchoolManager(), SchoolType.class);
        this.registerClientJSONDataManager("unlocks", this.clientUnlockNodesManager = new ClientUnlockNodesManager(), UnlockType.class);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Inter-mod communication dispatch
    }

    private void processIMC(final InterModProcessEvent event) {
        // Inter-mod communication receiving and processing
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        LOGGER.info("Registering JSON reload listeners...");
        this.registerModJSONReloadListener("schools", event.getServer(), this.schoolManager = new SchoolManager(), SchoolType.class);
        this.registerModJSONReloadListener("unlocks", event.getServer(), this.unlockManager = new UnlockManager(), UnlockType.class);
    }

    private <T extends JsonDataType<?>> void registerModJSONReloadListener(String type, MinecraftServer server, ModDataJsonReloadListener<?, T> listener, Class<T> clazz) {
        LOGGER.info("Registering '" + type + "' reload listener...");
        server.getResourceManager().addReloadListener(listener);
        for (JsonDataType<?> typeToRegister : typesToRegister.get(clazz)) {
            listener.registerType(typeToRegister.getId(), (T) typeToRegister);
        }
    }

    private <T extends JsonDataType<?>> void registerClientJSONDataManager(String type, ClientDataManager<?, T, ?> manager, Class<T> clazz) {
        LOGGER.info("Initializing types of client manager '" + type + "'...");
        for (JsonDataType<?> typeToRegister : typesToRegister.get(clazz)) {
            manager.registerType(typeToRegister.getId(), (T) typeToRegister);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Server starting with Schools of Magic Mod enabled");
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        LOGGER.info("Sending new client current cache.");
        AdvancementCache.getServerInstance().syncClient((ServerPlayerEntity) event.getEntity());
        this.getSchoolManager().syncClient((ServerPlayerEntity) event.getEntity());
        this.getUnlockManager().syncClient((ServerPlayerEntity) event.getEntity());
    }

    // TODO: this works for singleplayer not multiplayer
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        LOGGER.info("Initializing server advancement cache...");
        if (!event.getServer().getWorld(DimensionType.OVERWORLD).isRemote) {
            AdvancementCache.initServerCache(new ServerAdvancementCache(event.getServer()));
        }
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

    private SchoolManager schoolManager;
    private UnlockManager unlockManager;
    private ClientSchoolManager clientSchoolManager;
    private ClientUnlockNodesManager clientUnlockNodesManager;

    public SchoolManager getSchoolManager() {
        return schoolManager;
    }

    public UnlockManager getUnlockManager() {
        return unlockManager;
    }

    public ClientSchoolManager getClientSchoolManager() {
        return clientSchoolManager;
    }

    public ClientUnlockNodesManager getClientUnlockManager() {
        return clientUnlockNodesManager;
    }

    private Map<Class<? extends JsonDataType<?>>, List<JsonDataType<?>>> typesToRegister = new HashMap<>();

    public <T extends JsonDataType<?>> void registerType(Class<T> clazz, T type) {
        LOGGER.info("Registering JSON data type for type group '" + clazz.getName() + "' with id " + type.getId());
        List<JsonDataType<?>> l = typesToRegister.getOrDefault(clazz, new ArrayList<>());
        l.add(type);
        typesToRegister.put(clazz, l);
    }

    public JsonDataType<?> getRegisteredType(Class<? extends JsonDataType<?>> clazz, ResourceLocation id) {
        return typesToRegister.get(clazz).stream().filter(type -> type.getId().equals(id)).findFirst().get();
    }


    public static SchoolsOfMagicMod getInstance() {
        if (instance == null) {
            throw new NullPointerException("Schools of Magic mod instance has not been initialized. If you are trying to register something, do so later.");
        }

        return instance;
    }
}
