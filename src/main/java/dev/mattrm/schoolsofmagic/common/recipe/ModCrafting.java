package dev.mattrm.schoolsofmagic.common.recipe;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModCrafting {
    @SuppressWarnings("unused")
    public static class Ingredients {
        // Ingredients go here

        public static void register() {
            // No-op method to ensure that this class is loaded and its static initializers are run
        }
    }

    public static class Recipes {
        public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, GlobalConstants.MODID);

        public static final RegistryObject<IRecipeSerializer<?>> WORKBENCH_SHAPED = RECIPE_SERIALIZERS.register("workbench_shaped", ShapedWorkbenchRecipe.Serializer::new);
    }

    public static class RecipeTypes {
        public static final ModRecipeType<ShapedWorkbenchRecipe> WORKBENCH_SHAPED = new ModRecipeType<ShapedWorkbenchRecipe>("workbench_shaped");

        private static class ModRecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
            private final String id;

            public ModRecipeType(String id) {
                this.id = GlobalConstants.MODID + ":" + id;
            }

            @Override
            public String toString() {
                return id;
            }
        }
    }
}
