package ru.nanolive.draconicplus.client.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.VertexBuffer;

public class DPParticle extends EntityFX {

	protected DPParticle(World p_i1218_1_, double p_i1218_2_, double p_i1218_4_, double p_i1218_6_) {
		super(p_i1218_1_, p_i1218_2_, p_i1218_4_, p_i1218_6_);
	}
	
	public DPParticle(World p_i1219_1_, double p_i1219_2_, double p_i1219_4_, double p_i1219_6_, double p_i1219_8_,
			double p_i1219_10_, double p_i1219_12_) {
		super(p_i1219_1_, p_i1219_2_, p_i1219_4_, p_i1219_6_, p_i1219_8_, p_i1219_10_, p_i1219_12_);
	}
	
	protected float texturesPerRow = 16F;

    /**
     * Valid Range range 0-3
     */
    @Override
    public int getFXLayer() {
        return super.getFXLayer();
    }

    /**
     * This is to ensure particles are spawned using the correct methods because raw gl particles are handled very differently<br>
     * and attempting to render them with the default pipeline will break things.<br><br>
     *
     * Raw gl particles are pretty much what they sound like. The renderer wont bind a texture or start a draw call before rendering them.<br>
     * So you can do whatever you like!<br><br>
     *
     * Raw gl particles are rendered with blend enabled. depthMask disabled and GL_GREATER set to 0.<br>
     * Be sure to leave the render call in this state when you are done!
     *
     * @return true if this particle needs raw gl access.
     */
    public boolean isRawGLParticle() {
        return false;
    }

    public DPParticle setScale(float scale) {
        this.particleScale = scale;
        return this;
    }

    public DPParticle setColour(float red, float green, float blue) {
        super.setRBGColorF(red, green, blue);
        return this;
    }

    public DPParticle setMaxAge(int age, int randAdditive) {
        super.particleMaxAge = age + rand.nextInt(randAdditive);
        return this;
    }

    public DPParticle setGravity(double gravity) {
        this.particleGravity = (float) gravity;
        return this;
    }

    public DPParticle setSizeAndRandMotion(double scale, double xMotion, double yMotion, double zMotion) {
        this.particleScale = (float) scale;

        this.motionX = (-0.5 + rand.nextDouble()) * xMotion;
        this.motionY = (-0.5 + rand.nextDouble()) * yMotion;
        this.motionZ = (-0.5 + rand.nextDouble()) * zMotion;

        return this;
    }

    public Vec3D getPos() {
        return new Vec3D(posX, posY, posZ);
    }

    public World getWorld() {
        return worldObj;
    }

    protected void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        if(axisalignedbb != null) {
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = axisalignedbb.minY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
        }
    }
    
    public void moveEntityNoClip(double x, double y, double z) {
    	//this.boundingBox = (this.getBoundingBox().offset(0.0D, y, 0.0D));
    	//this.boundingBox = (this.getBoundingBox().offset(x, 0.0D, 0.0D));
    	//this.boundingBox = (this.getBoundingBox().offset(0.0D, 0.0D, z));
        resetPositionToBB();
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
    	VertexBuffer vertexbuffer = tessellator.getBuffer(tessellator);
        float minU = (float) this.particleTextureIndexX / texturesPerRow;
        float maxU = minU + 1F / texturesPerRow;//0.0624375F;
        float minV = (float) this.particleTextureIndexY / texturesPerRow;
        float maxV = minV + 1F / texturesPerRow;//0.0624375F;
        float scale = 0.1F * this.particleScale;
                
        if (this.particleIcon != null) {
            minU = this.particleIcon.getMinU();
            maxU = this.particleIcon.getMaxU();
            minV = this.particleIcon.getMinV();
            maxV = this.particleIcon.getMaxV();
        }

        float renderX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float renderY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float renderZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        int brightnessForRender = this.getBrightnessForRender(partialTicks);
        int j = brightnessForRender >> 16 & 65535;
        int k = brightnessForRender & 65535;
        
        vertexbuffer.pos((double) (renderX - rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ - rotationYZ * scale - rotationXZ * scale)).tex((double) maxU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX - rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ - rotationYZ * scale + rotationXZ * scale)).tex((double) maxU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * scale + rotationXY * scale), (double) (renderY + rotationZ * scale), (double) (renderZ + rotationYZ * scale + rotationXZ * scale)).tex((double) minU, (double) minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        vertexbuffer.pos((double) (renderX + rotationX * scale - rotationXY * scale), (double) (renderY - rotationZ * scale), (double) (renderZ + rotationYZ * scale - rotationXZ * scale)).tex((double) minU, (double) maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
    }
	
}
