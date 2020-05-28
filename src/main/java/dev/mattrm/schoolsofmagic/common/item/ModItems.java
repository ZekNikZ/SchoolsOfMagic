package dev.mattrm.schoolsofmagic.common.item;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.client.misc.ModItemGroups;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =  new DeferredRegister<Item>(ForgeRegistries.ITEMS, GlobalConstants.MODID);

    public static final RegistryObject<Item> WAND_BASE = ITEMS.register("wand_base", () -> new WandItemBase(new Item.Properties().group(ModItemGroups.SCHOOLS_OF_MAGIC_ITEM_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> MAGICAL_JOURNAL = ITEMS.register("journal", () -> new MagicalJournalItem(new Item.Properties().maxStackSize(1).group(ModItemGroups.SCHOOLS_OF_MAGIC_ITEM_GROUP)));

    public static Map<String, Item> BLOCK_ITEMS = new HashMap<>();
}
