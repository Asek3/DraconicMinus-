package ru.nanolive.draconicplus.common.fusioncrafting.nei;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import ru.nanolive.draconicplus.common.fusioncrafting.FusionRecipeAPI;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionRecipe;
import ru.nanolive.draconicplus.common.fusioncrafting.OreDictHelper;
import ru.nanolive.draconicplus.common.fusioncrafting.client.gui.GuiFusionCraftingCore;

@SideOnly(Side.CLIENT)
public class FusionCraftingNEI extends TemplateRecipeHandler {
	
    @SideOnly(Side.CLIENT)
    private FontRenderer fontRender;

    @SideOnly(Side.CLIENT)
    public FusionCraftingNEI(){
        super();
        fontRender = Minecraft.getMinecraft().fontRenderer;
    }
    
    public class CachedCompression extends CachedRecipe
    {
        private int count;
        private int x;
        private int y;

        public CachedCompression(IFusionRecipe recipe) {
            this.ingred = new PositionedStack(recipe.getRecipeCatalyst(), 74, 27);
            this.result = new PositionedStack(recipe.getRecipeOutput(recipe.getRecipeCatalyst()), 74, 68);
            ingredList.add(new PositionedStack(recipe.getRecipeCatalyst(), 74, 27));
            int size = recipe.getRecipeIngredients().size();
            int j = size%2;
            if(j != 0)
            	count = (size/2)+1;
            else
            	count = size/2;
        	int times = 0;
        	
        	
            for (int i = 0; i < recipe.getRecipeIngredients().size(); i++) {
            	Object o = recipe.getRecipeIngredients().get(i);
            	if(o == null)
            		continue;
            	
            	this.x = i < count ? 16 : 132;
            	
            		if(times == 0 || times == count)
            			y = 6;
            		else
            			y += 16;
            	
                if (o instanceof Item) {
                	ingredList.add(new PositionedStack((new ItemStack((Item) o)), x, y));
                }
                else if (o instanceof Block) {
                	ingredList.add(new PositionedStack((new ItemStack((Block) o)), x, y));
                }
                else if(o instanceof ItemStack) {
                	ingredList.add(new PositionedStack((ItemStack) o, x, y));
                }
                else if(o instanceof String) {
                	ingredList.add(new PositionedStack(OreDictHelper.resolveObject(o), x, y));
                }
            	
            	times++;
                
            }
            
            this.cost = recipe.getEnergyCost();
            this.tier = recipe.getRecipeTier();
        }

        @Override
        public List<PositionedStack> getIngredients() {
        	return ingredList;
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        @Override
        public PositionedStack getIngredient() {
            return ingred;
        }
        
        @Override
        public List<PositionedStack> getOtherStacks() {
        	return ingredList;
        }
        
        public void computeVisuals() {
            ingred.generatePermutations();
        }

        public int getCost(){
            return cost;
        }

        public int getTier(){
            return tier;
        }
        
        private int cost;
        private int tier;

        List<PositionedStack> ingredList = new ArrayList<>();
        PositionedStack ingred;
        PositionedStack result;
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }
    
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiFusionCraftingCore.class;
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("crafting.fusioncrafting");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("fusioncrafting") && getClass() == FusionCraftingNEI.class) {
            for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
                if(safeOre(recipe)) {
                    CachedCompression r = new CachedCompression(recipe);
                    r.computeVisuals();
                    arecipes.add(r);
                }
            }
        } else
            super.loadCraftingRecipes(outputId, results);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
            if(safeOre(recipe) && NEIServerUtils.areStacksSameTypeCrafting(recipe.getRecipeOutput(recipe.getRecipeCatalyst()), result)){
                CachedCompression r = new CachedCompression(recipe);
                r.computeVisuals();
                arecipes.add(r);
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("fusioncrafting") && getClass() == FusionCraftingNEI.class)
            loadCraftingRecipes("fusioncrafting");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (IFusionRecipe recipe : FusionRecipeAPI.getRecipes()) {
            if(safeOre(recipe) && ingredient.isItemEqual(recipe.getRecipeCatalyst())){
                CachedCompression r = new CachedCompression(recipe);
                arecipes.add(r);
            }
        }
    }

    private boolean safeOre(IFusionRecipe recipe){
        if(!(recipe instanceof IFusionRecipe))
            return true;
        return recipe.getRecipeCatalyst() != null;
    }

    @Override
    public String getGuiTexture() {
        return "draconicplus:textures/gui/fusioncrafting_nei.png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawForeground(int recipe){
        super.drawForeground(recipe);
                
        switch(((CachedCompression)arecipes.get(recipe)).getTier()) {
        case(0): {
        	GuiDraw.drawStringC(StatCollector.translateToLocal("info.tier.txt") + ": " + StatCollector.translateToLocal("info.tier0.txt"), 83, 5, 0x5050FF);
        	break;
        }
        case(1): {
        	GuiDraw.drawStringC(StatCollector.translateToLocal("info.tier.txt") + ": " + StatCollector.translateToLocal("info.tier1.txt"), 83, 5, 0x8000FF);
        	break;
        }
        case(2): {
        	GuiDraw.drawStringC(StatCollector.translateToLocal("info.tier.txt") + ": " + StatCollector.translateToLocal("info.tier2.txt"), 83, 5, 0xFF6600);
        	break;
        }
        case(3): {
        	GuiDraw.drawStringC(StatCollector.translateToLocal("info.tier.txt") + ": " + StatCollector.translateToLocal("info.tier3.txt"), 83, 5, 0x505050);
        	break;
        }
        }
        
        WorldClient world = Minecraft.getMinecraft().theWorld;
                
        GuiDraw.drawStringC(EnumChatFormatting.BLUE + StatCollector.translateToLocal("info.energyCost.txt"), 83, 90, 0);
        GuiDraw.drawStringC(EnumChatFormatting.AQUA + "" + ((CachedCompression)arecipes.get(recipe)).getCost() + "RF", 83, 100, 0);
    }

    @Override
    public String getOverlayIdentifier() {
        return "fusioncrafting";
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 256, 208);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
}