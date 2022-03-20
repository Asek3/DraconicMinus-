package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.CCRenderState.IVertexOperation;
import codechicken.lib.render.CCRenderState.IVertexSource;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.MoreInfo;
import ru.nanolive.draconicplus.client.particles.DPParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionCraftingInventory;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.GlStateManager;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.VertexBuffer;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.GlStateManager.DestFactor;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.GlStateManager.SourceFactor;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class EffectTrackerFusionCrafting {
    public static double interpPosX = 0;
    public static double interpPosY = 0;
    public static double interpPosZ = 0;
    private Random rand = new Random();
    private final Vec3D corePos;
    public final IFusionCraftingInventory craftingInventory;
    private int renderBolt = 0;
    private float rotation;
    private float rotationSpeed = 1;
    private float aRandomFloat = 0;
    public boolean positionLocked = false;
    public Vec3D startPos;
    public Vec3D pos;
    public Vec3D prevPos = new Vec3D();
    public Vec3D circlePosition = new Vec3D();
    private World worldObj;
    private long boltSeed = 0;

    public float alpha = 0F;
    public float scale = 1F;
    public float red = 0F;
    public float green = 1F;
    public float blue = 1F;

    public EffectTrackerFusionCrafting(World world, Vec3D pos, Vec3D corePos, IFusionCraftingInventory craftingInventory) {
        this.worldObj = world;
        this.corePos = corePos;
        this.craftingInventory = craftingInventory;
        this.rotation = rand.nextInt(1000);
        this.aRandomFloat = rand.nextFloat();
        this.pos = pos.copy();
        this.startPos = pos.copy();
        this.prevPos.set(pos);
    }

    public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }

    public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
        return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }
    
    public void onUpdate(boolean isMoving) {
        prevPos.set(pos);

        //region Movement
        if (isMoving){
            if (pos.equals(startPos)){
                worldObj.playSound(pos.x, pos.y, pos.z, MoreInfo.MODID + ":" + "fusion_complete", 0.5F, 0.5F, false);
            }

            double distance = getDistanceAtoB(circlePosition, pos);
            if (distance > 0.1 && !positionLocked) {
                if (scale > 1){
                    scale -= 0.05F;
                }

                Vec3D dir = Vec3D.getDirectionVec(pos, circlePosition);
                double speed = 0.1D + (aRandomFloat * 0.1D);
                dir.multiply(speed, speed, speed);
                pos.add(dir.x, dir.y, dir.z);
            }
            else {
                if (!positionLocked){
                    worldObj.playSound(pos.x, pos.y, pos.z, MoreInfo.MODID + ":" + "fusion_complete", 2F, 0.5F, false);
                }
                positionLocked = true;
                pos.set(circlePosition);
            }
        }
        else {
            scale = 1.5F;
        }

        //endregion

        //region Render Logic

        int chance = 22 - (int) ((craftingInventory.getCraftingStage() / 2000D) * 22);
        if (chance < 1){
            chance = 1;
        }

        if (rand.nextInt(chance) == 0){
        	DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, pos));
        }

        if (renderBolt > 0) {
            renderBolt--;
        }

        if (rand.nextInt((chance * 2) + 2) == 0){
            renderBolt = 1;
            boltSeed = rand.nextLong();
            Vec3D pos = corePos.copy().add(0.5, 0.5, 0.5);
            DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, pos));
            worldObj.playSound(pos.x, pos.y, pos.z, MoreInfo.MODID + ":" + "energy_bolt", 1F, 0.9F + rand.nextFloat() * 0.2F, false);
        }

        alpha = craftingInventory.getCraftingStage() / 1000F;
        rotationSpeed = 1 + (craftingInventory.getCraftingStage() / 1000F) * 10;
        if (alpha > 1){
            alpha = 1;
        }

        rotation += rotationSpeed;

        //endregion
    }

    public static Matrix4 getMatrix(Vector3 position, Rotation rotation, double scale) {
        return new Matrix4().translate(position).apply(new Scale(scale)).apply(rotation);
    }
    
    private static final ThreadLocal<CCRenderState> instances = ThreadLocal.withInitial(CCRenderState::new);
    
    public static CCRenderState instance() {
  	    return instances.get();
    }
    
    public void renderEffect(Tessellator tessellator, float partialTicks) {
        VertexBuffer vertexbuffer = tessellator.getBuffer(tessellator);
        CCRenderState ccrs = instance();
        //region Icosahedron

        float relativeX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks - interpPosX);
        float relativeY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks - interpPosY);
        float relativeZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks - interpPosZ);
        float correctX = (float) (this.prevPos.x + (this.pos.x - this.prevPos.x) * (double) partialTicks);
        float correctY = (float) (this.prevPos.y + (this.pos.y - this.prevPos.y) * (double) partialTicks);
        float correctZ = (float) (this.prevPos.z + (this.pos.z - this.prevPos.z) * (double) partialTicks);

        GlStateManager.pushMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200, 200);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.translate(relativeX, relativeY, relativeZ);
        GlStateManager.rotate(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
        GlStateManager.translate(-relativeX, -relativeY, -relativeZ);
        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS);
        Matrix4 pearlMat = getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * scale);
        CCModelLibrary.icosahedron7.render(new IVertexOperation[] { (IVertexOperation)pearlMat });
        ccrs.draw();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);

        //endregion

        GlStateManager.pushMatrix();
        GlStateManager.translate(relativeX, relativeY, relativeZ);

        if (renderBolt > 0){
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), corePos.copy().subtract(correctX - 0.5, correctY - 0.5, correctZ - 0.5), 0.05, 1, 10, boltSeed, true);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
    }

    public static class SubParticle extends DPParticle {

        public SubParticle(World worldIn, Vec3D pos) {
            super(worldIn, pos.x, pos.y, pos.z);

            double speed = 0.1;
            this.motionX = (-0.5 + rand.nextDouble()) * speed;
            this.motionY = (-0.5 + rand.nextDouble()) * speed;
            this.motionZ = (-0.5 + rand.nextDouble()) * speed;

            this.particleMaxAge = 10 + rand.nextInt(10);
            this.particleScale = 1F;
            this.particleTextureIndexY = 1;

            this.particleRed = 0;
        }

        @Override
        public void onUpdate() {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            particleTextureIndexX = rand.nextInt(5);
            int ttd = particleMaxAge - particleAge;
            if (ttd < 10){
                particleScale = ttd / 10F;
            }

            moveEntityNoClip(motionX, motionY, motionZ);

            if (particleAge++ > particleMaxAge) {
                setDead();
            }
        }

        @Override
        public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        	VertexBuffer vertexbuffer = tessellator.getBuffer(tessellator);
        	
            float minU = (float)this.particleTextureIndexX / 8.0F;
            float maxU = minU + 0.125F;
            float minV = (float)this.particleTextureIndexY / 8.0F;
            float maxV = minV + 0.125F;
            float scale = 0.1F * this.particleScale;

            float renderX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float renderY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float renderZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            int brightnessForRender = this.getBrightnessForRender(partialTicks);
            int j = brightnessForRender >> 16 & 65535;
            int k = brightnessForRender & 65535;
            vertexbuffer.pos((double)(renderX - rotationX * scale - rotationXY * scale), (double)(renderY - rotationZ * scale), (double)(renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double)(renderX - rotationX * scale + rotationXY * scale), (double)(renderY + rotationZ * scale), (double)(renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double)(renderX + rotationX * scale + rotationXY * scale), (double)(renderY + rotationZ * scale), (double)(renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
            vertexbuffer.pos((double)(renderX + rotationX * scale - rotationXY * scale), (double)(renderY - rotationZ * scale), (double)(renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        }
    }
}