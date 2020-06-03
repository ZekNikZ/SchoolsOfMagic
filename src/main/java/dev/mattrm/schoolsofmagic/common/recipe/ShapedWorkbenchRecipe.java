package dev.mattrm.schoolsofmagic.common.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class ShapedWorkbenchRecipe implements IRecipe<MagicalWorkbenchCraftingInventory> {
    private NonNullList<Ingredient> ingredients;
    private final ItemStack recipeOutput;
    private final ResourceLocation id;
    private final String group;

    private ShapedWorkbenchRecipe(final ResourceLocation id, final String group, final NonNullList<Ingredient> ingredients, final ItemStack recipeOutput) {
        this.id = id;
        this.group = group;
        this.ingredients = ingredients;
        this.recipeOutput = recipeOutput;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     *
     * @param inv     The inventory to check.
     * @param worldIn The world.
     */
    @Override
    public boolean matches(MagicalWorkbenchCraftingInventory inv, World worldIn) {
        for (int i = 0; i < MagicalWorkbenchCraftingInventory.NUM_SLOTS; i++) {
            if (!ingredients.get(i).test(inv.getStackInSlot(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     *
     * @param inv
     */
    @Override
    public ItemStack getCraftingResult(MagicalWorkbenchCraftingInventory inv) {
        return this.getRecipeOutput().copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    @Override
    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModCrafting.Recipes.WORKBENCH_SHAPED.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModCrafting.RecipeTypes.WORKBENCH_SHAPED;
    }

    private static String[] patternFromJson(JsonArray jsonArr) {
        String[] resultArr = new String[jsonArr.size()];
        if (resultArr.length != 3) {
            throw new JsonSyntaxException("Invalid pattern: exactly 3 rows are required");
        } else {
            for (int i = 0; i < 3; ++i) {
                String s = JSONUtils.getString(jsonArr.get(i), "pattern[" + i + "]");

                if (i == 1 && s.length() != 3) {
                    throw new JsonSyntaxException("Invalid pattern: row 2 must contain exactly 3 columns");
                } else if (i != 1 && s.length() != 2) {
                    throw new JsonSyntaxException("Invalid pattern: row " + (i + 1) + " must contain exactly 2 columns");
                }

                resultArr[i] = s;
            }

            return resultArr;
        }
    }

    private static Map<String, Ingredient> deserializeKey(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.deserialize(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys) {
        NonNullList<Ingredient> resultList = NonNullList.withSize(7, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keys.keySet());
        set.remove(" ");

        for(int row = 0; row < pattern.length; ++row) {
            for(int col = 0; col < pattern[row].length(); ++col) {
                String s = pattern[row].substring(col, col + 1);
                Ingredient ingredient = keys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                resultList.set(col + row * (row + 3) / 2, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return resultList;
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapedWorkbenchRecipe> {
        @Override
        public ShapedWorkbenchRecipe read(final ResourceLocation recipeId, final JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> map = ShapedWorkbenchRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] astring = ShapedWorkbenchRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern"));
            NonNullList<Ingredient> ingredients = ShapedWorkbenchRecipe.deserializeIngredients(astring, map);
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new ShapedWorkbenchRecipe(recipeId, group, ingredients, result);
        }

        @Nullable
        @Override
        public ShapedWorkbenchRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            final String group = buffer.readString(Short.MAX_VALUE);
            final int numIngredients = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(numIngredients, Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.read(buffer));
            }

            final ItemStack result = buffer.readItemStack();

            return new ShapedWorkbenchRecipe(recipeId, group, ingredients, result);
        }

        @Override
        public void write(PacketBuffer buffer, ShapedWorkbenchRecipe recipe) {
            buffer.writeString(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());
        }
    }
}
