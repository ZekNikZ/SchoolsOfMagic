package dev.mattrm.schoolsofmagic.common.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory;
import dev.mattrm.schoolsofmagic.common.item.MagicalJournalItem;
import dev.mattrm.schoolsofmagic.common.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory.CENTER_SLOT;
import static dev.mattrm.schoolsofmagic.common.inventory.MagicalWorkbenchCraftingInventory.NUM_SLOTS;

public class ShapelessWorkbenchRecipe implements IRecipe<MagicalWorkbenchCraftingInventory> {
    private NonNullList<Ingredient> ingredients;
    private final ItemStack recipeOutput;
    private final ResourceLocation id;
    private final String group;
    private final boolean requiresJournal;
    private final String[] requiredAdvancements;
    private final RecipeAdvancementMode advancementMode;
    private final boolean centerMustBeInCenter;

    private ShapelessWorkbenchRecipe(final ResourceLocation id, final String group, final NonNullList<Ingredient> ingredients, final ItemStack recipeOutput, final boolean requiresJournal, final String[] requiredAdvancements, final RecipeAdvancementMode advancementMode, final boolean centerMustBeInCenter) {
        this.id = id;
        this.group = group;
        this.ingredients = ingredients;
        this.recipeOutput = recipeOutput;

        this.requiresJournal = requiresJournal;
        this.requiredAdvancements = requiredAdvancements;
        this.advancementMode = advancementMode;

        this.centerMustBeInCenter = centerMustBeInCenter;
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
        NonNullList<ItemStack> contents = NonNullList.create();

        for (int i = 0; i < NUM_SLOTS; i++) {
            if (i == CENTER_SLOT && this.centerMustBeInCenter) continue;

            ItemStack itemStack = inv.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                contents.add(new ItemStack(itemStack.getItem()));
            }
        }

        int start = 0;
        if (this.centerMustBeInCenter) {
            start = 1;
            if (!ingredients.get(0).test(inv.getStackInSlot(CENTER_SLOT))) {
                return false;
            }
        }

        for (int i = start; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int j;
            for (j = 0; j < contents.size(); j++) {
                if (ingredient.test(contents.get(j))) {
                    break;
                }
            }
            if (j == contents.size()) {
                return false;
            } else {
                contents.remove(j);
            }
        }

        if (contents.size() != 0) {
            return false;
        }

        // Check journal / advancements
        if (this.requiresJournal) {
            ItemStack journalSlotItem = inv.getStackInSlot(MagicalWorkbenchCraftingInventory.JOURNAL_SLOT);

            if (journalSlotItem.getItem() == ModItems.MAGICAL_JOURNAL.get()) {
                if (this.requiredAdvancements.length > 0) {
                    return this.advancementMode.check(MagicalJournalItem.getOwnerUUID(journalSlotItem), this.requiredAdvancements, worldIn.isRemote);
                } else {
                    return true;
                }
            } else {
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
        return ModCrafting.Recipes.WORKBENCH_SHAPELESS.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModCrafting.RecipeTypes.WORKBENCH_SHAPELESS;
    }

    public boolean centerMustBeInCenter() {
        return centerMustBeInCenter;
    }

    public boolean requiresJournal() {
        return requiresJournal;
    }

    public String[] getRequiredAdvancements() {
        return requiredAdvancements;
    }

    public RecipeAdvancementMode getAdvancementMode() {
        return advancementMode;
    }

    private static NonNullList<Ingredient> deserializeIngredients(JsonObject json) {
        JsonObject center = JSONUtils.getJsonObject(json, "center");
        JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
        NonNullList<Ingredient> resultList = NonNullList.create();

        resultList.add(Ingredient.deserialize(center));

        for (JsonElement ingredient : ingredients) {
            resultList.add(Ingredient.deserialize(ingredient));
        }

        return resultList;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShapelessWorkbenchRecipe> {
        @Override
        public ShapelessWorkbenchRecipe read(final ResourceLocation recipeId, final JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            NonNullList<Ingredient> ingredients = ShapelessWorkbenchRecipe.deserializeIngredients(json);
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));

            boolean requiresJournal = JSONUtils.getBoolean(json, "requires_journal");
            JsonArray requiredAdvancementsArr = JSONUtils.getJsonArray(json, "required_advancements");
            String[] requiredAdvancements = new String[requiredAdvancementsArr.size()];
            for (int i = 0; i < requiredAdvancements.length; i++) {
                requiredAdvancements[i] = JSONUtils.getString(requiredAdvancementsArr.get(i), "required_advancements[" + i + "]");
            }
            RecipeAdvancementMode mode = RecipeAdvancementMode.valueOf(JSONUtils.getString(json, "advancement_mode"));

            boolean centerMustBeInCenter = JSONUtils.getBoolean(json, "center_ingredient_must_be_in_center");

            return new ShapelessWorkbenchRecipe(recipeId, group, ingredients, result, requiresJournal, requiredAdvancements, mode, centerMustBeInCenter);
        }

        @Nullable
        @Override
        public ShapelessWorkbenchRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            final String group = buffer.readString(Short.MAX_VALUE);
            final int numIngredients = buffer.readVarInt();
            final NonNullList<Ingredient> ingredients = NonNullList.withSize(numIngredients, Ingredient.EMPTY);
            for (int i = 0; i < ingredients.size(); i++) {
                ingredients.set(i, Ingredient.read(buffer));
            }

            final ItemStack result = buffer.readItemStack();

            final boolean requiresJournal = buffer.readBoolean();

            final RecipeAdvancementMode advancementMode = buffer.readEnumValue(RecipeAdvancementMode.class);
            final int numRequiredAdvancements = buffer.readVarInt();
            final String[] requiredAdvancements = new String[numRequiredAdvancements];
            for (int i = 0; i < numRequiredAdvancements; i++) {
                requiredAdvancements[i] = buffer.readString(Short.MAX_VALUE);
            }

            final boolean centerInCenter = buffer.readBoolean();

            return new ShapelessWorkbenchRecipe(recipeId, group, ingredients, result, requiresJournal, requiredAdvancements, advancementMode, centerInCenter);
        }

        @Override
        public void write(PacketBuffer buffer, ShapelessWorkbenchRecipe recipe) {
            buffer.writeString(recipe.getGroup());
            buffer.writeVarInt(recipe.getIngredients().size());

            for (final Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.getRecipeOutput());

            buffer.writeBoolean(recipe.requiresJournal());

            buffer.writeEnumValue(recipe.getAdvancementMode());
            buffer.writeVarInt(recipe.getRequiredAdvancements().length);
            for (final String advancement : recipe.requiredAdvancements) {
                buffer.writeString(advancement);
            }

            buffer.writeBoolean(recipe.centerMustBeInCenter());
        }
    }
}
