package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;
import ru.nanolive.draconicplus.proxy.ClientProxy;

public class RenderTileFusionCraftingCore extends TileEntitySpecialRenderer {

	public static final ResourceLocation texture = new ResourceLocation(MoreInfo.MODID, "textures/models/blocks/crafting_core.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		TileFusionCraftingCore tileF = (TileFusionCraftingCore) tile;
		if(tileF.getStackInCore(1) != null) {
	    EntityItem entityItem = new EntityItem(Minecraft.getMinecraft().theWorld, x, y, z, tileF.getStackInCore(1));
	    entityItem.hoverStart = 0.0F;
	    entityItem.age = Minecraft.getMinecraft().thePlayer.ticksExisted;
		
	    RenderUtils.renderItem(entityItem, x + 0.5, y + 0.4, z + 0.5, f, f, true, (byte) 0);
		}
		else if(tileF.getStackInCore(0) != null) {
		    EntityItem entityItem = new EntityItem(Minecraft.getMinecraft().theWorld, x, y, z, tileF.getStackInCore(0));
		    entityItem.hoverStart = 0.0F;
		    entityItem.age = Minecraft.getMinecraft().thePlayer.ticksExisted;
			
		    RenderUtils.renderItem(entityItem, x + 0.5, y + 0.4, z + 0.5, f, f, true, (byte) 0);
			}
				
		render((TileFusionCraftingCore)tile, x, y, z, f);
	}

	private void render(TileFusionCraftingCore tile, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		bindTexture(texture);
		GL11.glCallList(ClientProxy.displayList[0]);
		GL11.glPopMatrix();
	}
}
