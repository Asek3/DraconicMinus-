package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileCraftingInjector;
import ru.nanolive.draconicplus.common.fusioncrafting.tiles.TileFusionCraftingCore;
import ru.nanolive.draconicplus.proxy.ClientProxy;

public class RenderTileCraftingInjector extends TileEntitySpecialRenderer {

	public static final ResourceLocation textureBasic = new ResourceLocation(MoreInfo.MODID, "textures/models/blocks/crafting_injector_basic.png");
	public static final ResourceLocation textureWyvern = new ResourceLocation(MoreInfo.MODID, "textures/models/blocks/crafting_injector_wyvern.png");
	public static final ResourceLocation textureDraconic = new ResourceLocation(MoreInfo.MODID, "textures/models/blocks/crafting_injector_draconic.png");
	public static final ResourceLocation textureChaotic = new ResourceLocation(MoreInfo.MODID, "textures/models/blocks/crafting_injector_chaotic.png");
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
		TileCraftingInjector tileI = (TileCraftingInjector) tile;
		if(tileI.getStackInPedestal() != null) {
	    EntityItem entityItem = new EntityItem(Minecraft.getMinecraft().theWorld, x, y, z, tileI.getStackInPedestal());
	    entityItem.hoverStart = 0.0F;
	    entityItem.age = Minecraft.getMinecraft().thePlayer.ticksExisted;
		//this.renderItem(entityItem, x, y+0.25, z+0.5, f, f, true);
	    switch(tileI.facing.value) {
		case (1): {
			RenderUtils.renderItem(entityItem, x+0.5, y+0.8, z+0.5, f, f, true, tileI.facing.value);
			break;
		}
		case (2): {
			RenderUtils.renderItem(entityItem, x+0.5, y+0.4, z+0.15, f, f, true, tileI.facing.value);
			break;
		}
		case (3): {
			RenderUtils.renderItem(entityItem, x+0.5, y+0.4, z+0.85, f, f, true, tileI.facing.value);
			break;
		}
		case (4): {
			RenderUtils.renderItem(entityItem, x+0.15, y+0.4, z+0.5, f, f, true, tileI.facing.value);
			break;
		}
		case (5): {
			RenderUtils.renderItem(entityItem, x+0.85, y+0.4, z+0.5, f, f, true, tileI.facing.value);
			break;
		}
	    }
		}
		render((TileCraftingInjector)tile, x, y, z, f);
	}

	private void render(TileCraftingInjector tile, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		switch(tile.blockMetadata) {
		case (0): {
			bindTexture(textureBasic);
			break;
			}
		case (1): {
			bindTexture(textureWyvern);
			break;
			}
		case (2): {
			bindTexture(textureDraconic);
			break;
			}
		case (3): {
			bindTexture(textureChaotic);
			break;
		}
		}
		//System.out.println(tile.facing.value);
		switch(tile.facing.value) {
		case (0): {break;}
		case (1): {break;}
		case (2): {
			GL11.glRotated(90, -1, 0, 0);
			break;
			}
		case (3): {
			GL11.glRotated(90, 1, 0, 0);
			break;
			}
		case (4): {
			GL11.glRotated(90, 0, 0, 1);
			break;
			}
		case (5): {
			GL11.glRotated(90, 0, 0, -1);
			break;
			}
		}
		GL11.glCallList(ClientProxy.displayInjectorList[0]);
		GL11.glPopMatrix();
	}
}
