package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import com.google.common.collect.Queues;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import ru.nanolive.draconicplus.client.particles.DPParticle;
import ru.nanolive.draconicplus.common.fusioncrafting.PairKV;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.ResourceHelperDP;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.vertex.DefaultVertexFormats;

import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by brandon3055 on 23/4/2016.
 * Custom effect renderer used by all of my mods
 */
public class DPEffectRenderer {
    public World worldObj;

    //Textured EntityFX Queue
    @SuppressWarnings("unchecked")
    private final Map<ResourceLocation, ArrayDeque<EntityFX>[][]> renderQueue = new HashMap<>();
    private final Queue<PairKV<ResourceLocation, EntityFX>> newParticleQueue = new ArrayDeque<>();
    @SuppressWarnings("unchecked")
    private final Map<IGLFXHandler, ArrayDeque<EntityFX>[]> glRenderQueue = new HashMap<>();
    private final Queue<PairKV<IGLFXHandler, EntityFX>> newGlParticleQueue = Queues.newArrayDeque();

    @SuppressWarnings("unchecked")
    public DPEffectRenderer(World worldObj) {
        this.worldObj = worldObj;
    }

    //region Adders

    public void addRawGLEffect(IGLFXHandler handler, DPParticle particle) {
        if (particle == null) {
            return;
        }

        if (!particle.isRawGLParticle()) {
            throw new RuntimeException("Attempted to spawn a regular particle as a Raw GL particle! This is not allowed!");
        }
        newGlParticleQueue.add(new PairKV<IGLFXHandler, EntityFX>(handler, particle));
    }

    public void addEffect(ResourceLocation resourceLocation, EntityFX particle) {
        if (resourceLocation == null || particle == null) {
            return;
        }

        if (particle instanceof DPParticle && ((DPParticle) particle).isRawGLParticle()) {
            throw new RuntimeException("Attempted to spawn a Raw GL particle using the default spawn call! This is not allowed!");
        }

        newParticleQueue.add(new PairKV<>(resourceLocation, particle));
    }

    //endregion

    //region Update

    @SuppressWarnings("unchecked")
    public void updateEffects() {

        for (int i = 0; i < 4; ++i) {
            updateEffectLayer(i);
        }

        //region Add Queued Effects

        if (!newGlParticleQueue.isEmpty()) {
            for (PairKV<IGLFXHandler, EntityFX> handlerParticle = newGlParticleQueue.poll(); handlerParticle != null; handlerParticle = newGlParticleQueue.poll()) {
                if (!glRenderQueue.containsKey(handlerParticle.getKey())) {
                    glRenderQueue.put(handlerParticle.getKey(), new ArrayDeque[] {new ArrayDeque(), new ArrayDeque(), new ArrayDeque(), new ArrayDeque()});
                }
                int layer = handlerParticle.getValue().getFXLayer();

                if (glRenderQueue.get(handlerParticle.getKey())[layer].size() > 6000) {
                    glRenderQueue.get(handlerParticle.getKey())[layer].removeFirst().setDead();
                }

                glRenderQueue.get(handlerParticle.getKey())[layer].add(handlerParticle.getValue());
            }
        }

        if (!newParticleQueue.isEmpty()) {
            for (PairKV<ResourceLocation, EntityFX> entry = newParticleQueue.poll(); entry != null; entry = newParticleQueue.poll()) {
                if (!renderQueue.containsKey(entry.getKey())) {
                    ArrayDeque[][] array = new ArrayDeque[4][];
                    for (int i = 0; i < 4; i++) {
                        array[i] = new ArrayDeque[2];
                        for (int j = 0; j < 2; ++j) {
                            array[i][j] = new ArrayDeque<>();
                        }
                    }
                    renderQueue.put(entry.getKey(), array);
                }

                ArrayDeque<EntityFX>[][] array = renderQueue.get(entry.getKey());
                EntityFX EntityFX = entry.getValue();

                int layer = EntityFX.getFXLayer();
                int mask = 1;

                if (array[layer][mask].size() >= 6000) {
                    array[layer][mask].removeFirst().setDead();
                }

                array[layer][mask].add(EntityFX);
            }
        }

        //endregion
    }

    private void updateEffectLayer(int layer) {
        for (int i = 0; i < 2; ++i) {
            for (ArrayDeque<EntityFX>[][] queue : renderQueue.values()) {
                tickAndRemoveDead(queue[layer][i]);
            }
        }

        for (ArrayDeque<EntityFX>[] array : glRenderQueue.values()) {
            for (ArrayDeque<EntityFX> queue : array) {
                tickAndRemoveDead(queue);
            }
        }
    }

    private void tickAndRemoveDead(Queue<EntityFX> queue) {
        if (!queue.isEmpty()) {
            Iterator<EntityFX> iterator = queue.iterator();

            while (iterator.hasNext()) {
                EntityFX EntityFX = iterator.next();
                tickParticle(EntityFX);

                if (!EntityFX.isEntityAlive()) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * This should never be fired manually!
     * */
    protected void clearEffects(World worldIn) {
        this.worldObj = worldIn;

        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 2; ++k) {
                for (ArrayDeque<EntityFX>[][] list : renderQueue.values()) {
                    for (EntityFX particle : list[j][k]) {
                        particle.setDead();
                    }
                }
            }
        }

        for (ArrayDeque<EntityFX>[] array : glRenderQueue.values()) {
            for (ArrayDeque<EntityFX> queue : array) {
                for (EntityFX particle : queue) {
                    particle.setDead();
                }
            }
        }

//        for (ArrayDeque<EntityFX>[][] array : renderQueue.values()) {
//            for (int layer = 0; layer < 4; ++layer) {
//                for (int mask = 0; mask < 2; ++mask) {
//                    array[layer][mask].clear();
//                }
//            }
//        }
//
//
//        for (ArrayDeque<EntityFX>[] array : glRenderQueue.values()) {
//            for (ArrayDeque<EntityFX> queue : array) {
//                queue.clear();
//            }
//        }
    }

    private void tickParticle(final EntityFX particle) {
        try {
            particle.onUpdate();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking EntityFX");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("EntityFX being ticked");
            final int i = particle.getFXLayer();
            crashreportcategory.addCrashSection("EntityFX", new Callable<String>() {
                public String call() throws Exception {
                    return particle.toString();
                }
            });
            crashreportcategory.addCrashSection("EntityFX Type", new Callable<String>() {
                public String call() throws Exception {
                    return i == 0 ? "MISC_TEXTURE" : (i == 1 ? "TERRAIN_TEXTURE" : (i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i));
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    //endregion

    //region Render

    public void renderParticles(Entity entityIn, float partialTicks) {
        float f = ActiveRenderInfo.rotationX;
        float f1 = ActiveRenderInfo.rotationZ;
        float f2 = ActiveRenderInfo.rotationYZ;
        float f3 = ActiveRenderInfo.rotationXY;
        float f4 = ActiveRenderInfo.rotationXZ;
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(516, 0.003921569F);
        Tessellator tessellator = Tessellator.instance;

        for (int layer = 0; layer < 4; layer++) {
            renderGlParticlesInLayer(layer, tessellator, entityIn, partialTicks, f, f1, f2, f3, f4);
            renderTexturedParticlesInLayer(layer, tessellator, entityIn, partialTicks, f, f1, f2, f3, f4);
        }

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(516, 0.1F);
    }

    private void renderGlParticlesInLayer(int layer, Tessellator tessellator, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        for (IGLFXHandler handler : glRenderQueue.keySet()) {

            handler.preDraw(layer, tessellator, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

            for (final EntityFX particle : glRenderQueue.get(handler)[layer]) {
                try {
                    particle.renderParticle(tessellator, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                }
                catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering EntityFX");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("EntityFX being rendered");
                    crashreportcategory.addCrashSection("EntityFX", new Callable<String>() {
                        public String call() throws Exception {
                            return particle.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }

            handler.postDraw(layer, tessellator);
        }
    }

    private void renderTexturedParticlesInLayer(int layer, Tessellator tessellator, Entity entityIn, float partialTicks, float f, float f1, float f2, float f3, float f4) {
        for (ResourceLocation resourceLocation : renderQueue.keySet()) {
            ResourceHelperDP.bindTexture(resourceLocation);

            ArrayDeque<EntityFX>[][] texRenderQueue = renderQueue.get(resourceLocation);
            
            for (int j = 0; j < 2; ++j) {
                final int i_f = layer;

                if (!texRenderQueue[layer][j].isEmpty()) {
                    switch (j) {
                        case 0:
                        	GL11.glDepthMask(false);
                        	GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
                            break;
                        case 1:
                        	GL11.glDepthMask(true);
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    tessellator.startDrawingQuads();

                    for (final EntityFX particle : texRenderQueue[layer][j]) {
                        try {
                            particle.renderParticle(tessellator, partialTicks, f, f4, f1, f2, f3);
                        }
                        catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering EntityFX");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("EntityFX being rendered");
                            crashreportcategory.addCrashSection("EntityFX", new Callable<String>() {
                                public String call() throws Exception {
                                    return particle.toString();
                                }
                            });
                            crashreportcategory.addCrashSection("EntityFX Type", new Callable<String>() {
                                public String call() throws Exception {
                                    return i_f == 0 ? "MISC_TEXTURE" : (i_f == 1 ? "TERRAIN_TEXTURE" : (i_f == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i_f));
                                }
                            });
                            throw new ReportedException(crashreport);
                        }
                    }
                    //tessellator.startDrawing(0);

                    tessellator.draw();
                }
            }
        }
    }

    //endregion

    public String getStatistics() {
        int i = 0;

        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 2; ++k) {
                for (ArrayDeque<EntityFX>[][] list : renderQueue.values()) {
                    i += list[j][k].size();
                }
            }
        }

        int g = 0;
        for (ArrayDeque<EntityFX>[] array : glRenderQueue.values()) {
            for (ArrayDeque<EntityFX> queue : array) {
                g += queue.size();
            }
        }

        return "" + i + " GLFX: " + g;
    }

    public static final IGLFXHandler DEFAULT_IGLFX_HANDLER = new IGLFXHandler() {
        @Override
        public void preDraw(int layer, Tessellator tessellator, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(false);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
        }

        @Override
        public void postDraw(int layer, Tessellator tessellator) {

        }
    };
}