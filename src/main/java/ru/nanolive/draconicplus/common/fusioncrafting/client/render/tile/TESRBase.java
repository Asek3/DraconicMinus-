package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by brandon3055 on 6/5/2016.
 */
public class TESRBase<T extends TileEntity> extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

    }

    //TODO
    public void renderItem(ItemStack stack) {
        if (stack != null) {
            //Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        }
    }

    private boolean isLightSet = false;
    private float lastBrightnessX = 0;
    private float lastBrightnessY = 0;

    public void setLighting(float light) {
        if (!isLightSet) {
            lastBrightnessX = OpenGlHelper.lastBrightnessX;
            lastBrightnessY = OpenGlHelper.lastBrightnessY;
            isLightSet = true;
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
    }

    public void resetLighting() {
        if (isLightSet) {
            isLightSet = false;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void translateScaleTranslate(double translate, double x, double y, double z) {
    	GL11.glTranslated(translate, translate, translate);
    	GL11.glScaled(x, y, z);
    	GL11.glTranslated(-translate, -translate, -translate);
    }

    public void translateRotateTranslate(double translate, float angle, float x, float y, float z) {
    	GL11.glTranslated(translate, translate, translate);
    	GL11.glRotatef(angle, x, y, z);
        GL11.glTranslated(-translate, -translate, -translate);
    }

    public void preRenderFancy() {
    	GL11.glTexParameteri(3553, 10242, 10497);
    	GL11.glTexParameteri(3553, 10243, 10497);
    	GL11.glDisable(GL11.GL_CULL_FACE);
    	GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
    }

    /**
     * Call before rendering transparent
     */
    public void midRenderFancy() {
    	GL11.glEnable(GL11.GL_BLEND);
    	OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDepthMask(false);
    }

    public void postRenderFancy() {
    	GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }


//    GL_LINES = 0x1,
//    GL_LINE_LOOP = 0x2,
//    GL_LINE_STRIP = 0x3,
//    GL_TRIANGLES = 0x4,
//    GL_TRIANGLE_STRIP = 0x5,
//    GL_TRIANGLE_FAN = 0x6,
//    GL_QUADS = 0x7,
//    GL_QUAD_STRIP = 0x8,
//    GL_POLYGON = 0x9,
}