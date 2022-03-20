package ru.nanolive.draconicplus.common.fusioncrafting;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import cpw.mods.fml.common.Loader;

/**
 * Created by brandon3055 on 21/06/2016.
 */
public class OreDictHelper {


    /**
     * Takes an object which may be an ore string, an item, a block or an ItemStack and returns true
     * if it matches the given ItemStack.
     *
     * @param oreStack an ore string, itemStack, item or block.
     * @param compare the stack the be compared with
     * @return true if the stacks are the same.
     */
    public static boolean areStacksEqual(Object oreStack, ItemStack compare) {
        if (oreStack instanceof String){
            if (!OreDictionary.doesOreNameExist((String)oreStack)){
                return false;
            }

            int[] ids = OreDictionary.getOreIDs(compare);
            for (int id : ids){
                if (OreDictionary.getOreName(id).equals(oreStack)){
                    return true;
                }
            }
        }
        else if (oreStack instanceof ItemStack){
            return OreDictionary.itemMatches(compare, (ItemStack) oreStack, false);
        }
        else if (oreStack instanceof Item){
            return areStacksEqual(new ItemStack((Item)oreStack), compare);
        }
        else if (oreStack instanceof Block){
            return areStacksEqual(new ItemStack((Block)oreStack), compare);
        }

        return false;
    }

    public static ItemStack findFirstOreMatch(String oreName) {
        if (!OreDictionary.doesOreNameExist(oreName)) {
            return null;
        }

        List<ItemStack> stacks = OreDictionary.getOres(oreName);

        if (stacks.size() == 0){
            return null;
        }
        else if (stacks.size() == 1){
            return stacks.get(0);
        }
        else {
            //Prioritize Thermal Foundation
            if (Loader.isModLoaded("ThermalFoundation")) {
                for (ItemStack stack : stacks) {
                    if (stack.getItem() instanceof ItemBlock) {
                        String name = Block.blockRegistry.getNameForObject(((ItemBlock) stack.getItem()));
                        if (name != null && name.contains("ThermalFoundation")) {
                            return stack;
                        }
                    }
                    else {
                        String name = Item.itemRegistry.getNameForObject(stack.getItem());
                        if (name != null && name.contains("ThermalFoundation")) {
                            return stack;
                        }
                    }
                }
            }
            return stacks.get(0);
        }
    }

    /**
     * Takes some object. Works out of it is an itemstack a string an item or a block and returns a stack based on what it finds.
     */
    public static ItemStack resolveObject(Object object){
        if (object instanceof String){
            return findFirstOreMatch((String)object);
        }
        else if (object instanceof ItemStack) {
            return (ItemStack) object;
        }
        else if (object instanceof Item){
            return new ItemStack((Item) object);
        }
        else if (object instanceof Block){
            return new ItemStack((Block) object);
        }
        return null;
    }
}