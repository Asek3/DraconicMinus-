package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import java.util.Random;
import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.client.particles.DPParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.IFusionCraftingInventory;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;

public class EffectTrackerFusionCrafting {
  public static double interpPosX = 0.0D;
  
  public static double interpPosY = 0.0D;
  
  public static double interpPosZ = 0.0D;
  
  private Random rand = new Random();
  
  private final Vec3D corePos;
  
  public final IFusionCraftingInventory craftingInventory;
  
  private int renderBolt = 0;
  
  private float rotation;
  
  private float rotationSpeed = 1.0F;
  
  private float aRandomFloat = 0.0F;
  
  public boolean positionLocked = false;
  
  public Vec3D startPos;
  
  public Vec3D pos;
  
  public Vec3D prevPos = new Vec3D();
  
  public Vec3D circlePosition = new Vec3D();
  
  private World worldObj;
  
  private long boltSeed = 0L;
  
  public float alpha = 0.0F;
  
  public float scale = 1.0F;
  
  public float red = 0.0F;
  
  public float green = 1.0F;
  
  public float blue = 1.0F;
  
  public EffectTrackerFusionCrafting(World world, Vec3D pos, Vec3D corePos, IFusionCraftingInventory craftingInventory) {
    this.worldObj = world;
    this.corePos = corePos;
    this.craftingInventory = craftingInventory;
    this.rotation = this.rand.nextInt(1000);
    this.aRandomFloat = this.rand.nextFloat();
    this.pos = pos.copy();
    this.startPos = pos.copy();
    this.prevPos.set(pos);
  }
  
  public static double getDistanceAtoB(double x1, double y1, double z1, double x2, double y2, double z2) {
    double dx = x1 - x2;
    double dy = y1 - y2;
    double dz = z1 - z2;
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  
  public static double getDistanceAtoB(Vec3D pos1, Vec3D pos2) {
    return getDistanceAtoB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
  }
  
  public void onUpdate(boolean isMoving) {
    this.prevPos.set(this.pos);
    if (isMoving) {
      if (this.pos.equals(this.startPos))
        this.worldObj.playSound(this.pos.x, this.pos.y, this.pos.z, "draconicplus:fusion_complete", 0.5F, 0.5F, false); 
      double distance = getDistanceAtoB(this.circlePosition, this.pos);
      if (distance > 0.1D && !this.positionLocked) {
        if (this.scale > 1.0F)
          this.scale -= 0.05F; 
        Vec3D dir = Vec3D.getDirectionVec(this.pos, this.circlePosition);
        double speed = 0.1D + this.aRandomFloat * 0.1D;
        dir.multiply(speed, speed, speed);
        this.pos.add(dir.x, dir.y, dir.z);
      } else {
        if (!this.positionLocked)
          this.worldObj.playSound(this.pos.x, this.pos.y, this.pos.z, "draconicplus:fusion_complete", 2.0F, 0.5F, false); 
        this.positionLocked = true;
        this.pos.set(this.circlePosition);
      } 
    } else {
      this.scale = 1.5F;
    } 
    int chance = 22 - (int)(this.craftingInventory.getCraftingStage() / 2000.0D * 22.0D);
    if (chance < 1)
      chance = 1; 
    if (this.rand.nextInt(chance) == 0)
      DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), (EntityFX)new SubParticle(this.worldObj, this.pos)); 
    if (this.renderBolt > 0)
      this.renderBolt--; 
    if (this.rand.nextInt(chance * 2 + 2) == 0) {
      this.renderBolt = 1;
      this.boltSeed = this.rand.nextLong();
      Vec3D pos = this.corePos.copy().add(0.5D, 0.5D, 0.5D);
      DPEffectHandler.effectRenderer.addEffect(ResourceHelperDP.getResource("textures/particle/particles.png"), (EntityFX)new SubParticle(this.worldObj, pos));
      this.worldObj.playSound(pos.x, pos.y, pos.z, "draconicplus:energy_bolt", 1.0F, 0.9F + this.rand.nextFloat() * 0.2F, false);
    } 
    this.alpha = this.craftingInventory.getCraftingStage() / 1000.0F;
    this.rotationSpeed = 1.0F + this.craftingInventory.getCraftingStage() / 1000.0F * 10.0F;
    if (this.alpha > 1.0F)
      this.alpha = 1.0F; 
    this.rotation += this.rotationSpeed;
  }
  
  public static Matrix4 getMatrix(Vector3 position, Rotation rotation, double scale) {
    return (new Matrix4()).translate(position).apply((Transformation)new Scale(scale)).apply((Transformation)rotation);
  }
  
  private static final ThreadLocal<CCRenderState> instances = ThreadLocal.withInitial(CCRenderState::new);
  
  public static CCRenderState instance() {
    return instances.get();
  }
  
  public void renderEffect(Tessellator tessellator, float partialTicks) {
    CCRenderState ccrs = instance();
    float relativeX = (float)(this.prevPos.x + (this.pos.x - this.prevPos.x) * partialTicks - interpPosX);
    float relativeY = (float)(this.prevPos.y + (this.pos.y - this.prevPos.y) * partialTicks - interpPosY);
    float relativeZ = (float)(this.prevPos.z + (this.pos.z - this.prevPos.z) * partialTicks - interpPosZ);
    float correctX = (float)(this.prevPos.x + (this.pos.x - this.prevPos.x) * partialTicks);
    float correctY = (float)(this.prevPos.y + (this.pos.y - this.prevPos.y) * partialTicks);
    float correctZ = (float)(this.prevPos.z + (this.pos.z - this.prevPos.z) * partialTicks);
    GL11.glPushMatrix();
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
    GL11.glTranslatef(relativeX, relativeY, relativeZ);
    GL11.glRotatef(this.rotation + partialTicks * this.rotationSpeed, 0.0F, 1.0F, 0.0F);
    GL11.glTranslatef(-relativeX, -relativeY, -relativeZ);
    CCRenderState.reset();
    CCRenderState.startDrawing(7);
    Matrix4 pearlMat = getMatrix(new Vector3(relativeX, relativeY, relativeZ), new Rotation(0.0D, new Vector3(0.0D, 0.0D, 0.0D)), 0.15D * this.scale);
    CCModelLibrary.icosahedron7.render(new CCRenderState.IVertexOperation[] { (CCRenderState.IVertexOperation)pearlMat });
    CCRenderState.draw();
    tessellator.draw();
    GL11.glPopMatrix();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPushMatrix();
    GL11.glTranslatef(relativeX, relativeY, relativeZ);
    if (this.renderBolt > 0)
      RenderEnergyBolt.renderBoltBetween(new Vec3D(), this.corePos.copy().subtract(correctX - 0.5D, correctY - 0.5D, correctZ - 0.5D), 0.05D, 1.0D, 10, this.boltSeed, true); 
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glPopMatrix();
  }
  
  public static class SubParticle extends DPParticle {
    public SubParticle(World worldIn, Vec3D pos) {
      super(worldIn, pos.x, pos.y, pos.z);
      double speed = 0.1D;
      this.motionX = (-0.5D + this.rand.nextDouble()) * speed;
      this.motionY = (-0.5D + this.rand.nextDouble()) * speed;
      this.motionZ = (-0.5D + this.rand.nextDouble()) * speed;
      this.particleMaxAge = 10 + this.rand.nextInt(10);
      this.particleScale = 1.0F;
      this.particleTextureIndexY = 1;
      this.particleRed = 0.0F;
    }
    
    public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.particleTextureIndexX = this.rand.nextInt(5);
      int ttd = this.particleMaxAge - this.particleAge;
      if (ttd < 10)
        this.particleScale = ttd / 10.0F; 
      moveEntityNoClip(this.motionX, this.motionY, this.motionZ);
      if (this.particleAge++ > this.particleMaxAge)
        setDead(); 
    }
    
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float minU = this.particleTextureIndexX / 8.0F;
      float maxU = minU + 0.125F;
      float minV = this.particleTextureIndexY / 8.0F;
      float maxV = minV + 0.125F;
      float scale = 0.1F * this.particleScale;
      float renderX = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
      float renderY = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
      float renderZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
      int brightnessForRender = getBrightnessForRender(partialTicks);
      int j = brightnessForRender >> 16 & 0xFFFF;
      int k = brightnessForRender & 0xFFFF;
      tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
      tessellator.addVertexWithUV((renderX - rotationX * scale - rotationXY * scale), (renderY - rotationZ * scale), (renderZ - rotationYZ * scale - rotationXZ * scale), maxU, maxV);
      tessellator.addVertexWithUV((renderX - rotationX * scale + rotationXY * scale), (renderY + rotationZ * scale), (renderZ - rotationYZ * scale + rotationXZ * scale), maxU, minV);
      tessellator.addVertexWithUV((renderX + rotationX * scale + rotationXY * scale), (renderY + rotationZ * scale), (renderZ + rotationYZ * scale + rotationXZ * scale), minU, minV);
      tessellator.addVertexWithUV((renderX + rotationX * scale - rotationXY * scale), (renderY - rotationZ * scale), (renderZ + rotationYZ * scale - rotationXZ * scale), minU, maxV);
    }
  }
}
