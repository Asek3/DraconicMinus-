package ru.nanolive.draconicplus.common.fusioncrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;

import javax.annotation.Nullable;

import java.util.*;

/**
 * Created by brandon3055 on 11/06/2016.
 * A simple implementation if IFusionRecipe
 * Accepts ore dictionary entries.
 */
public class SimpleFusionRecipe implements IFusionRecipe {

    protected ItemStack result;
    protected ItemStack catalyst;
    protected List<Object> ingredients;
    protected int energyCost;
    protected int craftingTier;

    /**
     * tier 0 = basic, tier 1 = wyvern, tier 2 = awakened, tier 3 = chaotic
     */
    public SimpleFusionRecipe(ItemStack result, ItemStack catalyst, int energyCost, int craftingTier, Object... ingredients) {
        this.result = result;
        this.catalyst = catalyst;
        this.ingredients = new LinkedList<Object>();
        Collections.addAll(this.ingredients, ingredients);
        this.energyCost = energyCost;
        this.craftingTier = craftingTier;
    }

    @Override
    public ItemStack getRecipeOutput(@Nullable ItemStack catalyst) {
        return result;
    }

    @Override
    public boolean isRecipeCatalyst(ItemStack catalyst) {
        return catalyst != null && this.catalyst.isItemEqual(catalyst);
    }

    @Override
    public ItemStack getRecipeCatalyst() {
        return catalyst;
    }

    @Override
    public int getRecipeTier() {
        return craftingTier;
    }

    @Override
    public List<Object> getRecipeIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(IFusionCraftingInventory inventory, World world, BlockPos pos) {
    	TileFusionCraftingCore.updateInjectors(world, pos);
        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());
        //Check the catalyst for this recipe
        
        if (inventory.getStackInCore(0) == null || !inventory.getStackInCore(0).isItemEqual(catalyst) || inventory.getStackInCore(0).stackSize < catalyst.stackSize) {
            return false;
        }
        
        //Check that all of the ingredients are available.
        for (Object ingredient : ingredients) {
            boolean foundIngredient = false;

            for (ICraftingInjector pedestal : pedestals) {
                if (pedestal.getStackInPedestal() != null && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal())) {
                    foundIngredient = true;
                    pedestals.remove(pedestal);
                    break;
                }
            }

            if (!foundIngredient) {
                return false;
            }
        }

        //Check that there are no extra items that are not part of the recipe.
        for (ICraftingInjector pedestal : pedestals) {
            if (pedestal.getStackInPedestal() != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void craft(IFusionCraftingInventory inventory, World world, BlockPos pos) {
        //This shouldn't be needed but cant hurt.
        if (!matches(inventory, world, pos)) {
            return;
        }

        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        //Use Ingredients
        for (Object ingredient : ingredients) {
            for (ICraftingInjector pedestal : pedestals) {
                if (pedestal.getStackInPedestal() != null && OreDictHelper.areStacksEqual(ingredient, pedestal.getStackInPedestal()) && pedestal.getPedestalTier() >= craftingTier) {

                    ItemStack stack = pedestal.getStackInPedestal();
                    if (stack.getItem().hasContainerItem(stack)) {
                        stack = stack.getItem().getContainerItem(stack);
                    } else {
                        stack.stackSize--;
                        if (stack.stackSize <= 0) {
                            stack = null;
                        }
                    }

                    pedestal.setStackInPedestal(stack);
                    pedestals.remove(pedestal);
                    break;
                }
            }
        }

        ItemStack catalyst = inventory.getStackInCore(0);
        catalyst.stackSize -= this.catalyst.stackSize;

        if (catalyst.stackSize <= 0){
            catalyst = null;
        }

        inventory.setStackInCore(0, catalyst);
        inventory.setStackInCore(1, result.copy());
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public void onCraftingTick(IFusionCraftingInventory inventory, World world, BlockPos pos) {
    }

    @Override
    public String canCraft(IFusionCraftingInventory inventory, World world, BlockPos pos) {

        if (inventory.getStackInCore(1) != null){
            return "outputObstructed";
        }

        List<ICraftingInjector> pedestals = new ArrayList<ICraftingInjector>();
        pedestals.addAll(inventory.getInjectors());

        for (ICraftingInjector pedestal : pedestals) {
            if (pedestal.getStackInPedestal() != null && pedestal.getPedestalTier() < craftingTier) {
                return "tierLow";
            }
        }

        return "true";
    }

    @Override
    public String toString() {
        return String.format("SimpleFusionRecipe: {Result: %s, Catalyst: %s}", result, catalyst);
    }
}