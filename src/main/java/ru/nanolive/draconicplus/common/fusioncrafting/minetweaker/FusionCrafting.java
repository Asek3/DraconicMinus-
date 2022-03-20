package ru.nanolive.draconicplus.common.fusioncrafting.minetweaker;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.item.IngredientItem;
import minetweaker.api.item.IngredientOr;
import minetweaker.api.item.IngredientStack;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import ru.nanolive.draconicplus.common.fusioncrafting.FusionRecipeAPI;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import static ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager.*;
import static ru.nanolive.draconicplus.common.fusioncrafting.RecipeManager.RecipeDifficulty.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@ZenClass("mods.draconicplus.FusionCrafting")
public class FusionCrafting {

    public static boolean matchesForRemoval(Object obj, ItemStack stack) {
        if (stack == null)
            return false;
        if (obj instanceof ItemStack) {
            if (((ItemStack) obj) == null || stack == null)
                return true;
            return ((ItemStack) obj).getItem() == stack.getItem()
                    && (((ItemStack) obj).getItemDamage() == stack.getItemDamage() || ((ItemStack) obj).getItemDamage() == OreDictionary.WILDCARD_VALUE)
                    && ItemStack.areItemStackTagsEqual((ItemStack) obj, stack);
        } else if (obj instanceof Item) {
            return obj == stack.getItem();
        } else if (obj instanceof Block) {
            return Item.getItemFromBlock((Block) obj) == stack.getItem();
        } else if (obj instanceof String) {
            for (ItemStack entry : OreDictionary.getOres((String) obj)) {
                if (matchesForRemoval(entry, stack))
                    return true;
            }
            return false;
        } else if (obj instanceof List<?>) {
            for (Object entry : (List<?>) obj) {
                if (matchesForRemoval(entry, stack))
                    return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public static boolean matchesForRemoval(Object obj, Iterable<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (matchesForRemoval(obj, stack))
                return true;
        }
        return false;
    }

    public static boolean matchesForRemoval(Object obj, ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            if (matchesForRemoval(obj, stack))
                return true;
        }
        return false;
    }

    public static <T> boolean matchesForRemoval(Object obj, Iterable<T> stacks, Function<T, ItemStack> action) {
        for (T stack : stacks) {
            if (matchesForRemoval(obj, action.apply(stack)))
                return true;
        }
        return false;
    }

    public static <T> boolean matchesForRemovalArray(Object obj, Iterable<T> stacks, Function<T, ItemStack[]> action) {
        for (T stack : stacks) {
            if (matchesForRemoval(obj, action.apply(stack)))
                return true;
        }
        return false;
    }

    public static <T> boolean matchesForRemovalFlat(Object obj, Iterable<T> stacks, Function<T, Iterable<ItemStack>> action) {
        for (T stack : stacks) {
            if (matchesForRemoval(obj, action.apply(stack)))
                return true;
        }
        return false;
    }
	
    public static Object getObject(IIngredient ingredient) {
        if (ingredient == null) {
            return new ItemStack(Blocks.air);
        }
        if (ingredient instanceof IItemStack) {
            return toStack((IItemStack) ingredient);
        } else if (ingredient instanceof IngredientStack && ingredient.getInternal() instanceof IItemStack) {
            ItemStack stack = toStack((IItemStack) ingredient.getInternal());
            stack.stackSize = ingredient.getAmount();
            return stack;
        } else if (ingredient instanceof IOreDictEntry) {
            return ((IOreDictEntry) ingredient).getName();
        } else if (ingredient instanceof IngredientStack || ingredient instanceof IngredientItem || ingredient instanceof IngredientOr) {
            List<ItemStack> list = new ArrayList<>();
            for (IItemStack stack : ingredient.getItems()) {
                list.add(toStack(stack));
            }
            return list;
        } else {
            return new ItemStack(Blocks.air);
        }
    }

    public static Object[] getObjects(IIngredient[] ingredients) {
        Object[] objects = new Object[ingredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            objects[i] = getObject(ingredients[i]);
        }
        return objects;
    }
	
    @ZenMethod
    public static void add(IItemStack output, IItemStack catalyst, int tier, int energyCost, IIngredient[] ingredients) {
        ItemStack out = toStack(output);
        ItemStack cl = toStack(catalyst);
        Object[] in = getObjects(ingredients);
        long energyPerIngredient = energyCost / in.length;
        MineTweakerAPI.apply(new IUndoableAction() {
            @Override
            public void apply() {
                addFusion(NORMAL, out, cl, energyCost, tier, in);
            }

            @Override
            public String describe() {
                return "Adds a FusionCrafting-Recipe";
            }

			@Override
			public boolean canUndo() {
				return true;
			}

			@Override
			public String describeUndo() {
				return null;
			}

			@Override
			public Object getOverrideKey() {
				return null;
			}

			@Override
			public void undo() {
                List<IFusionRecipe> removal = new ArrayList<>();
                for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
                    if (matchesForRemoval(out, recipe.getRecipeOutput(recipe.getRecipeCatalyst()))) {
                        removal.add(recipe);
                    }
                }
                for (IFusionRecipe recipe : removal) {
                    FusionRecipeAPI.removeRecipe(recipe);
                }
			}
        });
    }
    
    @ZenMethod
    public static void remove(IIngredient output) {
        Object obj = getObject(output);
        MineTweakerAPI.apply(new IUndoableAction() {
            @Override
            public void apply() {
                List<IFusionRecipe> removal = new ArrayList<>();
                for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
                    if (matchesForRemoval(obj, recipe.getRecipeOutput(recipe.getRecipeCatalyst()))) {
                        removal.add(recipe);
                    }
                }
                for (IFusionRecipe recipe : removal) {
                    FusionRecipeAPI.removeRecipe(recipe);
                }
            }

            @Override
            public String describe() {
                return "Removes FusionCrafting-Recipes by catalyst";
            }

			@Override
			public boolean canUndo() {
				return false;
			}

			@Override
			public String describeUndo() {
				return null;
			}

			@Override
			public Object getOverrideKey() {
				return null;
			}

			@Override
			public void undo() {
				
			}
        });
    }
    
    private static ItemStack toStack(IItemStack item){
        if (item == null) return null;
        else {
            Object internal = item.getInternal();
            if (internal == null || !(internal instanceof ItemStack)) {
                MineTweakerAPI.getLogger().logError("Not a valid item stack: " + item);
            }
            return (ItemStack) internal;
        }
    }
	
}
