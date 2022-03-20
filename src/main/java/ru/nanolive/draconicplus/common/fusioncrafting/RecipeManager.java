package ru.nanolive.draconicplus.common.fusioncrafting;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 20/07/2016.
 */
public class RecipeManager {

    public static final FusionRecipeRegistry FUSION_REGISTRY = new FusionRecipeRegistry();

    private static List<IRecipe> activeCrafting = new ArrayList<IRecipe>();
    private static List<IFusionRecipe> activeFusion = new ArrayList<IFusionRecipe>();

    //region Initialization

    /**
     * Creates the FusionRecipeRegistry and Initializes the FusionRecipeAPI
     */
    public static void initialize() {
        FusionRecipeAPI.registry = FUSION_REGISTRY;
        loadRecipes();
    }

    /**
     * Loads all recipes from {@link DERecipes} If recipes have already been loaded
     * it will first remove all currently loaded recipes before reloading.
     */
    public static void loadRecipes() {
        if (!activeCrafting.isEmpty()){
            for (IRecipe recipe : activeCrafting) {
                CraftingManager.getInstance().getRecipeList().remove(recipe);
            }
        }

        activeCrafting.clear();

        if (!activeFusion.isEmpty()){
            for (IFusionRecipe recipe : activeFusion) {
                FUSION_REGISTRY.remove(recipe);
            }
        }

        activeFusion.clear();

    }

    //endregion

    //region Registration

    public static void addShaped(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShaped(difficulty, new ItemStack(result), recipe);
    }

    public static void addShaped(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != RecipeDifficulty.ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        for (int i = 3; i < recipe.length; i++){
            if (recipe[i] instanceof String) {
                isOre = true;
            }

        }

        if (isOre) {
            IRecipe iRecipe = new ShapedOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            activeCrafting.add(GameRegistry.addShapedRecipe(result, recipe));
        }
    }

    public static void addShapeless(RecipeDifficulty difficulty, Block result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, Item result, Object... recipe) {
        addShapeless(difficulty, new ItemStack(result), recipe);
    }

    public static void addShapeless(RecipeDifficulty difficulty, ItemStack result, Object... recipe) {
        if (difficulty != RecipeDifficulty.ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        boolean isOre = false;

        for (int i = 0; i < recipe.length; i++){
            if (recipe[i] instanceof String) {
                isOre = true;
            }

        }

        if (isOre) {
            IRecipe iRecipe = new ShapelessOreRecipe(result, recipe);
            activeCrafting.add(iRecipe);
            GameRegistry.addRecipe(iRecipe);
        }
        else {
            List<ItemStack> list = new ArrayList<ItemStack>();

            for (Object object : recipe) {
                if (object instanceof ItemStack) {
                    list.add(((ItemStack)object).copy());
                }
                else if (object instanceof Item) {
                    list.add(new ItemStack((Item)object));
                }
                else {
                    if (!(object instanceof Block)) {
                        throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                    }

                    list.add(new ItemStack((Block)object));
                }
            }

            IRecipe iRecipe = new ShapelessRecipes(result, list);
            activeCrafting.add(iRecipe);

            GameRegistry.addRecipe(iRecipe);
        }
    }

    public static void addFusion(RecipeDifficulty difficulty, ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        if (difficulty != RecipeDifficulty.ALL && RecipeDifficulty.getDifficulty() != difficulty) {
            return;
        }

        IFusionRecipe recipe = new SimpleFusionRecipe(result, catalyst, energyCost, craftingTier, ingredients);
        activeFusion.add(recipe);
        FUSION_REGISTRY.add(recipe);
    }

    public static void addRecipe(IRecipe recipe) {
        activeCrafting.add(recipe);
        GameRegistry.addRecipe(recipe);
    }

    public static enum RecipeDifficulty {
        ALL,
        NORMAL,
        HARD;

        public static RecipeDifficulty getDifficulty() {
            return NORMAL;
        }
    }

    //endregion
}