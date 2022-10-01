package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.CCRenderState.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.client.particles.DPParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionCraftingInventory;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect.EffectTrackerFusionCrafting.SubParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.DefaultVertexFormats;
import ru.nanolive.draconicplus.common.fusioncrafting.utils.Utils;

import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 23/06/2016.
 */
public class ParticleFusionCrafting extends DPParticle {

    private final Vec3D corePos;
    private final IFusionCraftingInventory craftingInventory;
    private boolean renderBolt = false;
    private float rotation;
    private float rotationSpeed = 1;
    private boolean circleDir = false;
    private float circlePos = 0;
    private float circleSpeed = 0;
    private float rotYAngle = 0;
    private float aRandomFloat = 0;
    private boolean rotationLock = false;

    public ParticleFusionCrafting(World worldIn, Vec3D pos, Vec3D corePos, IFusionCraftingInventory craftingInventory) {
    	super(worldIn, pos.x, pos.y, pos.z);
        this.corePos = corePos;
        this.craftingInventory = craftingInventory;
        this.particleAlpha = 0;
        this.rotation = rand.nextInt(1000);
        this.motionX = this.motionY = this.motionZ = 0;
        this.circlePos = rand.nextFloat() * 1000F;
        this.circleDir = rand.nextBoolean();
        this.aRandomFloat = rand.nextFloat();
        this.rotYAngle = rand.nextFloat() * 1000;
        this.particleScale = 1F;
    }

    @Override
    public void onUpdate() {
        if (particleAge++ > 20 && (craftingInventory == null || !craftingInventory.craftingInProgress() || ((TileEntity) craftingInventory).isInvalid())) {
            for (int i = 0; i < 10; i++){
                DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, new Vec3D(posX, posY, posZ)));
            }
            setDead();
            return;
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        //region Movement
        if (craftingInventory.getCraftingStage() > 1000){
            double distFromCore = 1.2;

            if (craftingInventory.getCraftingStage() > 1600){
                distFromCore *= 1D - (craftingInventory.getCraftingStage() - 1600) / 400D;
            }

            particleScale = 0.7F + ((float)(distFromCore / 1.2D) * 0.3F);
            particleGreen = particleBlue = (float)(distFromCore - 0.2);
            particleRed = 1F - (float)(distFromCore - 0.2);

            double targetX = corePos.x - 0.5 + (Math.cos(circlePos) * distFromCore);
            double targetZ = corePos.z + 0.5 + (Math.sin(circlePos) * distFromCore);
            double targetY = corePos.y + 0.5;// + (Math.cos(circlePos + rotYAngle) * 0.7 * distFromCore);

            double distance = Utils.getDistanceAtoB(targetX, targetY, targetZ, posX, posY, posZ);

            if (distance > 0.1 && !rotationLock) {
                Vec3D dir = Vec3D.getDirectionVec(new Vec3D(posX, posY, posZ), new Vec3D(targetX, targetY, targetZ));
                double speed = 0.1D + (aRandomFloat * 0.1D);
                dir.multiply(speed, speed, speed);
                moveEntityNoClip(dir.x, dir.y, dir.z);
            }
            else {
                float rotSpeed = (0.6F * ((craftingInventory.getCraftingStage() - 1000) / 1000F)) + (1.2F - (float)distFromCore);
                rotationLock = true;
                if (circleDir){
                    circleSpeed = rotSpeed;
                }
                else if (!circleDir){
                    circleSpeed = -rotSpeed;
                }

                //setPosition(targetX, targetY, targetZ);
                circlePos += circleSpeed;
            }
        }

        //endregion

        //region Render Logic

        int chance = 22 - (int) ((craftingInventory.getCraftingStage() / 2000D) * 22);
        if (chance < 1){
            chance = 1;
        }

        if (rand.nextInt(chance) == 0){
        	DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, new Vec3D(posX, posY, posZ)));
        }

        renderBolt = rand.nextInt(chance * 2) == 0;
        if (renderBolt){
            Vec3D pos = corePos.copy().add(0.5, 0.5, 0.5);
            DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), new SubParticle(worldObj, new Vec3D(posX, posY, posZ)));
        }

        particleAlpha = craftingInventory.getCraftingStage() / 1000F;
        rotationSpeed = 1 + (craftingInventory.getCraftingStage() / 1000F) * 10;
        if (particleAlpha > 1){
            particleAlpha = 1;
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
    
    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    	tessellator.draw();
    	CCRenderState ccrs = instance();
        ccrs.startDrawing();
        ccrs.draw(); //End Draw
        //region Icosahedron

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        float correctX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks);
        float correctY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks);
        float correctZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks);

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(particleRed, particleGreen, particleBlue, particleAlpha);
       // GlStateManager.color(1, 0, 0, 1);
        GL11.glTranslated(x, y + 0.5, z);

        GL11.glRotatef(rotation + (partialTicks * rotationSpeed), 0F, 1F, 0F);
        //GlStateManager.rotate((float)Math.sin((rotation + partialTicks) * rotationSpeed / 100F) * 20F, 1F, 0F, 0F);
        GL11.glTranslatef(-x, -y, -z);

        ccrs.reset();
        ccrs.startDrawing(GL11.GL_QUADS);
        Matrix4 pearlMat = getMatrix(new Vector3(x, y, z), new Rotation(0F, new Vector3(0, 0, 0)), 0.15 * particleScale);
        CCModelLibrary.icosahedron7.render(new IVertexOperation[] { (IVertexOperation)pearlMat });
        ccrs.draw();

        GL11.glPopMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);

        //endregion

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);

        if (renderBolt){
            renderBolt = false;
            RenderEnergyBolt.renderBoltBetween(new Vec3D(), corePos.copy().subtract(correctX - 0.5, correctY - 0.5, correctZ - 0.5), 0.05, 1, 10, rand.nextLong(), true);
        }

//        if (rand.nextInt(1) == 0 || true){
//            rand.setSeed(1);
//            Vec3D t = new Vec3D(-0.5 + rand.nextDouble(), -0.5 + rand.nextDouble(), -0.5 + rand.nextDouble());
//            double l = 1;
//            t.multiply(l, l, l);
//            RenderEnergyBolt.renderCorona(new Vec3D(), t, 0.01, 0.2, 4, worldObj.rand.nextLong());
//        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        //Restore Draw State
        //tessellator.draw();
        tessellator.startDrawingQuads();
    }

    public class SubParticle extends DPParticle {

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
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
            tessellator.addVertexWithUV((renderX - rotationX * scale - rotationXY * scale), (renderY - rotationZ * scale), (renderZ - rotationYZ * scale - rotationXZ * scale), maxU, maxV);
            tessellator.addVertexWithUV((renderX - rotationX * scale + rotationXY * scale), (renderY + rotationZ * scale), (renderZ - rotationYZ * scale + rotationXZ * scale), maxU, maxV);
            tessellator.addVertexWithUV((renderX + rotationX * scale + rotationXY * scale), (renderY + rotationZ * scale), (renderZ + rotationYZ * scale + rotationXZ * scale), maxU, maxV);
            tessellator.addVertexWithUV((renderX + rotationX * scale - rotationXY * scale), (renderY - rotationZ * scale), (renderZ + rotationYZ * scale - rotationXZ * scale), maxU, maxV);
        }
    }
}