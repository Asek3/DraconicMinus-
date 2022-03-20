package ru.nanolive.draconicplus.common.fusioncrafting.client.render.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

public class RenderUtils {

    protected static void bindTexture(ResourceLocation p_110776_1_)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(p_110776_1_);
    }
	
    public static Render getEntityClassRenderObject(Class p_78715_1_)
    {
        Render render = (Render)RenderManager.instance.entityRenderMap.get(p_78715_1_);

        if (render == null && p_78715_1_ != Entity.class)
        {
            render = RenderUtils.getEntityClassRenderObject(p_78715_1_.getSuperclass());
            RenderManager.instance.entityRenderMap.put(p_78715_1_, render);
        }

        return render;
    }

    public static Render getEntityRenderObject(Entity p_78713_1_)
    {
        return RenderUtils.getEntityClassRenderObject(p_78713_1_.getClass());
    }
	
    private static void renderEntityOnFire(Entity p_76977_1_, double p_76977_2_, double p_76977_4_, double p_76977_6_, float p_76977_8_)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        IIcon iicon = Blocks.fire.getFireIcon(0);
        IIcon iicon1 = Blocks.fire.getFireIcon(1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)p_76977_2_, (float)p_76977_4_, (float)p_76977_6_);
        float f1 = p_76977_1_.width * 1.4F;
        GL11.glScalef(f1, f1, f1);
        Tessellator tessellator = Tessellator.instance;
        float f2 = 0.5F;
        float f3 = 0.0F;
        float f4 = p_76977_1_.height / f1;
        float f5 = (float)(p_76977_1_.posY - p_76977_1_.boundingBox.minY);
        GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, -0.3F + (float)((int)f4) * 0.02F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f6 = 0.0F;
        int i = 0;
        tessellator.startDrawingQuads();

        while (f4 > 0.0F)
        {
            IIcon iicon2 = i % 2 == 0 ? iicon : iicon1;
            RenderUtils.bindTexture(TextureMap.locationBlocksTexture);
            float f7 = iicon2.getMinU();
            float f8 = iicon2.getMinV();
            float f9 = iicon2.getMaxU();
            float f10 = iicon2.getMaxV();

            if (i / 2 % 2 == 0)
            {
                float f11 = f9;
                f9 = f7;
                f7 = f11;
            }

            tessellator.addVertexWithUV((double)(f2 - f3), (double)(0.0F - f5), (double)f6, (double)f9, (double)f10);
            tessellator.addVertexWithUV((double)(-f2 - f3), (double)(0.0F - f5), (double)f6, (double)f7, (double)f10);
            tessellator.addVertexWithUV((double)(-f2 - f3), (double)(1.4F - f5), (double)f6, (double)f7, (double)f8);
            tessellator.addVertexWithUV((double)(f2 - f3), (double)(1.4F - f5), (double)f6, (double)f9, (double)f8);
            f4 -= 0.45F;
            f5 -= 0.45F;
            f2 *= 0.9F;
            f6 += 0.03F;
            ++i;
        }

        tessellator.draw();
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
    }
    
    public static void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_)
    {
        if (RenderManager.instance.getEntityRenderObject(p_76979_1_).renderManager.options.fancyGraphics && RenderManager.instance.getEntityRenderObject(p_76979_1_).shadowSize > 0.0F && !p_76979_1_.isInvisible())
        {
            double d3 = RenderManager.instance.getEntityRenderObject(p_76979_1_).renderManager.getDistanceToCamera(p_76979_1_.posX, p_76979_1_.posY, p_76979_1_.posZ);
            float f2 = (float)((1.0D - d3 / 256.0D) * (double)RenderManager.instance.getEntityRenderObject(p_76979_1_).shadowOpaque);

            if (f2 > 0.0F)
            {
            	RenderManager.instance.getEntityRenderObject(p_76979_1_).renderShadow(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, f2, p_76979_9_);
            }
        }

        if (p_76979_1_.canRenderOnFire())
        {
        	RenderUtils.renderEntityOnFire(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, p_76979_9_);
        }
    }
    
    public static boolean renderItem(Entity p_147939_1_, double p_147939_2_, double p_147939_4_, double p_147939_6_, float p_147939_8_, float p_147939_9_, boolean p_147939_10_, byte facing)
    {
        Render render = null;

        try
        {
            render = RenderUtils.getEntityRenderObject(p_147939_1_);

            if (render != null && RenderManager.instance.renderEngine != null)
            {
                if (!render.isStaticEntity() || p_147939_10_)
                {
                    try
                    {
                        RenderUtils.doRender((EntityItem) p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_, p_147939_9_, facing);
                    }
                    catch (Throwable throwable2)
                    {
                        throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Rendering entity in world"));
                    }

                    try
                    {
                        doRenderShadowAndFire(p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_, p_147939_9_);
                    }
                    catch (Throwable throwable1)
                    {
                        throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Post-rendering entity in world"));
                    }
                }
            }
            else if (RenderManager.instance.renderEngine != null)
            {
                return false;
            }

            return true;
        }
        catch (Throwable throwable3)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            p_147939_1_.addEntityCrashInfo(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addCrashSection("Assigned renderer", render);
            crashreportcategory1.addCrashSection("Location", CrashReportCategory.func_85074_a(p_147939_2_, p_147939_4_, p_147939_6_));
            crashreportcategory1.addCrashSection("Rotation", Float.valueOf(p_147939_8_));
            crashreportcategory1.addCrashSection("Delta", Float.valueOf(p_147939_9_));
            throw new ReportedException(crashreport);
        }
    }
    
    public static void doRender(EntityItem p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_, byte facing)
    {
        ItemStack itemstack = p_76986_1_.getEntityItem();

        if (itemstack.getItem() != null)
        {
            RenderUtils.bindEntityTexture(p_76986_1_);
            TextureUtil.func_152777_a(false, false, 1.0F);
            RenderUtils.random.setSeed(187L);
            GL11.glPushMatrix();
            float f2 = shouldBob() ? MathHelper.sin(((float)p_76986_1_.age + p_76986_9_) / 10.0F + p_76986_1_.hoverStart) * 0.1F + 0.1F : 0F;
            float f3 = (((float)p_76986_1_.age + p_76986_9_) / 20.0F + p_76986_1_.hoverStart) * (180F / (float)Math.PI);
            byte b0 = 1;

            if (p_76986_1_.getEntityItem().stackSize > 1)
            {
                b0 = 2;
            }

            if (p_76986_1_.getEntityItem().stackSize > 5)
            {
                b0 = 3;
            }

            if (p_76986_1_.getEntityItem().stackSize > 20)
            {
                b0 = 4;
            }

            if (p_76986_1_.getEntityItem().stackSize > 40)
            {
                b0 = 5;
            }

            b0 = getMiniBlockCount(itemstack, b0);

            GL11.glTranslatef((float)p_76986_2_, (float)p_76986_4_ + f2, (float)p_76986_6_);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            float f6;
            float f7;
            int k;

            if (ForgeHooksClient.renderEntityItem(p_76986_1_, itemstack, f2, f3, random, Minecraft.getMinecraft().renderEngine, RenderManager.instance.getEntityRenderObject(p_76986_1_).field_147909_c, b0))
            {
                ;
            }
            else // Code Style break here to prevent the patch from editing RenderUtils line
            if (itemstack.getItemSpriteNumber() == 0 && itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
            {
                Block block = Block.getBlockFromItem(itemstack.getItem());
                GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);

                if (RenderItem.renderInFrame)
                {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                float f9 = 0.25F;
                k = block.getRenderType();

                if (k == 1 || k == 19 || k == 12 || k == 2)
                {
                    f9 = 0.5F;
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                }

                GL11.glScalef(f9, f9, f9);

                for (int l = 0; l < b0; ++l)
                {
                    GL11.glPushMatrix();

                    if (l > 0)
                    {
                        f6 = (RenderUtils.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        f7 = (RenderUtils.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        float f8 = (RenderUtils.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
                        GL11.glTranslatef(f6, f7, f8);
                    }

                    ((RenderItem)RenderManager.instance.getEntityRenderObject(p_76986_1_)).renderBlocksRi.renderBlockAsItem(block, itemstack.getItemDamage(), 1.0F);
                    GL11.glPopMatrix();
                }

                if (block.getRenderBlockPass() > 0)
                {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
            else
            {
                float f5;

                if (itemstack.getItem().requiresMultipleRenderPasses())
                {
                    if (RenderItem.renderInFrame)
                    {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    }
                    else
                    {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    for (int j = 0; j < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++j)
                    {
                        RenderUtils.random.setSeed(187L);
                        IIcon iicon1 = itemstack.getItem().getIcon(itemstack, j);

                        if (((RenderItem)RenderManager.instance.getEntityRenderObject(p_76986_1_)).renderWithColor)
                        {
                            k = itemstack.getItem().getColorFromItemStack(itemstack, j);
                            f5 = (float)(k >> 16 & 255) / 255.0F;
                            f6 = (float)(k >> 8 & 255) / 255.0F;
                            f7 = (float)(k & 255) / 255.0F;
                            GL11.glColor4f(f5, f6, f7, 1.0F);
                            RenderUtils.renderDroppedItem(p_76986_1_, iicon1, b0, p_76986_9_, f5, f6, f7, j, facing);
                        }
                        else
                        {
                            RenderUtils.renderDroppedItem(p_76986_1_, iicon1, b0, p_76986_9_, 1.0F, 1.0F, 1.0F, j, facing);
                        }
                    }
                }
                else
                {
                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth)
                    {
                        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    }

                    if (RenderItem.renderInFrame)
                    {
                        GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                        GL11.glTranslatef(0.0F, -0.05F, 0.0F);
                    }
                    else
                    {
                        GL11.glScalef(0.5F, 0.5F, 0.5F);
                    }

                    IIcon iicon = itemstack.getIconIndex();

                    if (((RenderItem)RenderManager.instance.getEntityRenderObject(p_76986_1_)).renderWithColor)
                    {
                        int i = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                        float f4 = (float)(i >> 16 & 255) / 255.0F;
                        f5 = (float)(i >> 8 & 255) / 255.0F;
                        f6 = (float)(i & 255) / 255.0F;
                        RenderUtils.renderDroppedItem(p_76986_1_, iicon, b0, p_76986_9_, f4, f5, f6, facing);
                    }
                    else
                    {
                        RenderUtils.renderDroppedItem(p_76986_1_, iicon, b0, p_76986_9_, 1.0F, 1.0F, 1.0F, facing);
                    }

                    if (itemstack != null && itemstack.getItem() instanceof ItemCloth)
                    {
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            RenderUtils.bindEntityTexture(p_76986_1_);
            TextureUtil.func_147945_b();
        }
    }
    
    protected static void bindEntityTexture(Entity p_110777_1_)
    {
        RenderUtils.bindTexture(RenderUtils.getEntityTexture((EntityItem) p_110777_1_));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected static ResourceLocation getEntityTexture(EntityItem p_110775_1_)
    {
        return Minecraft.getMinecraft().renderEngine.getResourceLocation(p_110775_1_.getEntityItem().getItemSpriteNumber());
    }

    /**
     * Renders a dropped item
     */
    private static void renderDroppedItem(EntityItem p_77020_1_, IIcon p_77020_2_, int p_77020_3_, float p_77020_4_, float p_77020_5_, float p_77020_6_, float p_77020_7_, byte facing)
    {
        RenderUtils.renderDroppedItem(p_77020_1_, p_77020_2_, p_77020_3_, p_77020_4_, p_77020_5_, p_77020_6_, p_77020_7_, 0, facing);
    }

    private static Random random = new Random();
    
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    
    private static void renderDroppedItem(EntityItem p_77020_1_, IIcon p_77020_2_, int p_77020_3_, float p_77020_4_, float p_77020_5_, float p_77020_6_, float p_77020_7_, int pass, byte facing)
    {
    	Tessellator tessellator = Tessellator.instance;

        if (p_77020_2_ == null)
        {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = texturemanager.getResourceLocation(p_77020_1_.getEntityItem().getItemSpriteNumber());
            p_77020_2_ = ((TextureMap)texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f14 = ((IIcon)p_77020_2_).getMinU();
        float f15 = ((IIcon)p_77020_2_).getMaxU();
        float f4 = ((IIcon)p_77020_2_).getMinV();
        float f5 = ((IIcon)p_77020_2_).getMaxV();
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        float f10;

            GL11.glPushMatrix();

            if (RenderItem.renderInFrame)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef((((float)p_77020_1_.age + p_77020_4_) / 20.0F + p_77020_1_.hoverStart) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            float f9 = 0.0625F;
            f10 = 0.021875F;
            ItemStack itemstack = p_77020_1_.getEntityItem();
            int j = itemstack.stackSize;
            byte b0;

            if (j < 2)
            {
                b0 = 1;
            }
            else if (j < 16)
            {
                b0 = 2;
            }
            else if (j < 32)
            {
                b0 = 3;
            }
            else
            {
                b0 = 4;
            }

            b0 = getMiniItemCount(itemstack, b0);

            GL11.glTranslatef(-f7, -f8, -((f9 + f10) * (float)b0 / 2.0F));

            for (int k = 0; k < b0; ++k)
            {
                GL11.glTranslatef(0f, 0f, f9 + f10);

                if (itemstack.getItemSpriteNumber() == 0)
                {
                    bindTexture(TextureMap.locationBlocksTexture);
                }
                else
                {
                    bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(p_77020_5_, p_77020_6_, p_77020_7_, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, f15, f4, f14, f5, ((IIcon)p_77020_2_).getIconWidth(), ((IIcon)p_77020_2_).getIconHeight(), f9);

            }

            GL11.glPopMatrix();
    }
    
    public static boolean shouldBob()
    {
        return false;
    }
    
    public static byte getMiniBlockCount(ItemStack stack, byte original)
    {
        return original;
    }
	
    public static byte getMiniItemCount(ItemStack stack, byte original)
    {
        return original;
    }
    
    public static boolean shouldSpreadItems()
    {
        return true;
    }
    
}
