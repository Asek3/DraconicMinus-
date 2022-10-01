package ru.nanolive.draconicplus.common.fusioncrafting.client.render.effect;

import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by brandon3055 on 16/06/2016.
 */
public class GuiEffectRenderer {
    private List<GuiEffect> effects = new ArrayList<GuiEffect>();

    public void updateEffects() {

        Iterator<GuiEffect> i = effects.iterator();

        while (i.hasNext()){
            GuiEffect effect = i.next();

            if (effect.isAlive()){
                effect.onUpdate();
            }
            else {
                i.remove();
            }

        }
    }

    public void renderEffects(float partialTick){
        for (GuiEffect effect : effects){

            if (effect.isTransparent()){
            	GL11.glEnable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
            }

            GL11.glDisable(GL11.GL_LIGHTING);

            effect.renderParticle(partialTick);

            if (effect.isTransparent()){
            	GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }
        }
    }

    public void addEffect(GuiEffect effect){
        effects.add(effect);
    }

    public void clearEffects(){
        effects.clear();
    }
}
