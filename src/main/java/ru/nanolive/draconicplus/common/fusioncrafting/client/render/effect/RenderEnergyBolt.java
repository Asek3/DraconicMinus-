package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import ru.nanolive.draconicplus.common.fusioncrafting.Vec3D;
import ru.nanolive.draconicplus.common.fusioncrafting.utils.Utils;

public class RenderEnergyBolt {
  public static void renderBoltBetween(Vec3D point1, Vec3D point2, double scale, double maxDeflection, int maxSegments, long boltSeed, boolean corona) {
    Tessellator tessellator = Tessellator.instance;
    Random random = new Random(boltSeed);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    double distance = Utils.getDistanceAtoB(point1, point2);
    Vec3D dirVec = Vec3D.getDirectionVec(point1, point2);
    Vec3D invDir = (new Vec3D(1.0D, 1.0D, 1.0D)).subtract(dirVec);
    Vec3D[] vectors = new Vec3D[maxSegments / 2 + random.nextInt(maxSegments / 2)];
    vectors[0] = point1;
    vectors[vectors.length - 1] = point2;
    for (int i = 1; i < vectors.length - 1; i++) {
      double pos = i / vectors.length * distance;
      Vec3D point = point1.copy();
      point.add(dirVec.copy().multiply(pos, pos, pos));
      double randX = (-0.5D + random.nextDouble()) * maxDeflection * invDir.x;
      double randY = (-0.5D + random.nextDouble()) * maxDeflection * invDir.y;
      double randZ = (-0.5D + random.nextDouble()) * maxDeflection * invDir.z;
      point.add(randX, randY, randZ);
      vectors[i] = point;
    } 
    double rScale = scale * (0.5D + random.nextDouble() * 0.5D);
    for (int j = 1; j < vectors.length; j++)
      drawBoltSegment(tessellator, vectors[j - 1], vectors[j], (float)rScale); 
    if (corona) {
      Vec3D[][] coronaVecs = new Vec3D[2 + random.nextInt(4)][2 + random.nextInt(3)];
      int k;
      for (k = 0; k < coronaVecs.length; k++) {
        coronaVecs[k][0] = point1;
        double d = distance / (2.0D + random.nextDouble() * 2.0D);
        for (int v = 1; v < (coronaVecs[k]).length; v++) {
          double pos = v / (coronaVecs[k]).length * d;
          Vec3D point = point1.copy();
          point.add(dirVec.copy().multiply(pos, pos, pos));
          double randX = (-0.5D + random.nextDouble()) * maxDeflection * invDir.x * 0.5D;
          double randY = (-0.5D + random.nextDouble()) * maxDeflection * invDir.y * 0.5D;
          double randZ = (-0.5D + random.nextDouble()) * maxDeflection * invDir.z * 0.5D;
          point.add(randX, randY, randZ);
          coronaVecs[k][v] = point;
        } 
      } 
      for (k = 0; k < coronaVecs.length; k++) {
        float f = 0.1F + random.nextFloat() * 0.5F;
        for (int v = 1; v < (coronaVecs[k]).length; v++)
          drawBoltSegment(tessellator, coronaVecs[k][v - 1], coronaVecs[k][v], (float)scale * f); 
      } 
    }
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  
  public static void renderCorona(Vec3D source, Vec3D target, double scale, double maxDeflection, int maxSegments, long boltSeed) {
    Tessellator tessellator = Tessellator.instance;
    Random random = new Random(boltSeed);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    double distance = Utils.getDistanceAtoB(source, target);
    Vec3D dirVec = Vec3D.getDirectionVec(source, target);
    Vec3D invDir = (new Vec3D(1.0D, 1.0D, 1.0D)).subtract(dirVec);
    Vec3D[][] coronaVecs = new Vec3D[2 + random.nextInt(maxSegments * 2)][2 + random.nextInt(2)];
    int i;
    for (i = 0; i < coronaVecs.length; i++) {
      coronaVecs[i][0] = source;
      Vec3D newDir = invDir.copy();
      newDir.multiply(0.9D + random.nextDouble() * 0.5D, 0.9D + random.nextDouble() * 0.5D, 0.9D + random.nextDouble() * 0.5D);
      for (int v = 1; v < (coronaVecs[i]).length; v++) {
        double pos = v / (coronaVecs[i]).length * distance;
        Vec3D point = source.copy();
        point.add(dirVec.copy().multiply(pos, pos, pos));
        double randX = (-0.5D + random.nextDouble()) * maxDeflection * newDir.x;
        double randY = (-0.5D + random.nextDouble()) * maxDeflection * newDir.y;
        double randZ = (-0.5D + random.nextDouble()) * maxDeflection * newDir.z;
        point.add(randX, randY, randZ);
        coronaVecs[i][v] = point;
      } 
    } 
    for (i = 0; i < coronaVecs.length; i++) {
      float f = 0.1F + random.nextFloat() * 0.5F;
      for (int v = 1; v < (coronaVecs[i]).length; v++)
        drawBoltSegment(tessellator, coronaVecs[i][v - 1], coronaVecs[i][v], (float)scale * f); 
    } 
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
  }
  
  private static void drawBoltSegment(Tessellator tessellator, Vec3D p1, Vec3D p2, float scale) {
    tessellator.startDrawing(0);
    GL11.glPushMatrix();
    GL11.glTranslated(p1.x, p1.y, p1.z);
    double dist = Utils.getDistanceAtoB(p1, p2);
    float xd = (float)(p1.x - p2.x);
    float yd = (float)(p1.y - p2.y);
    float zd = (float)(p1.z - p2.z);
    double var7 = MathHelper.sqrt_double((xd * xd + zd * zd));
    float rotYaw = (float)(Math.atan2(xd, zd) * 180.0D / Math.PI);
    float rotPitch = (float)(Math.atan2(yd, var7) * 180.0D / Math.PI);
    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
    GL11.glRotatef(180.0F + rotYaw, 0.0F, 0.0F, -1.0F);
    GL11.glRotatef(rotPitch, 1.0F, 0.0F, 0.0F);
    tessellator.draw();
    tessellator.startDrawing(5);
    for (int i = 0; i <= 9; i++) {
      float f = (i + 1.0F) / 9.0F;
      float verX = MathHelper.sin((i % 3) * 3.1415927F * 2.0F / 3.0F) * f * scale;
      float verZ = MathHelper.cos((i % 3) * 3.1415927F * 2.0F / 3.0F) * f * scale;
      tessellator.setColorRGBA_F(0.35F, 0.65F, 0.9F, 0.3F);
      tessellator.addVertex(verX, dist, verZ);
      tessellator.addVertex(verX, 0.0D, verZ);
    } 
    tessellator.draw();
    GL11.glPopMatrix();
  }
}
