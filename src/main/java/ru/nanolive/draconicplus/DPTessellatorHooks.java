package ru.nanolive.draconicplus;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import net.minecraft.client.renderer.Tessellator;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.VertexBuffer;
import ru.nanolive.draconicplus.common.fusioncrafting.client.render.WorldVertexBufferUploader;

import static net.minecraft.client.renderer.Tessellator.instance;

import alexsocol.hooks.HookField;

public class DPTessellatorHooks {

    @HookField(targetClassName = "net.minecraft.client.renderer.Tessellator")
    public VertexBuffer worldRenderer;
    @HookField(targetClassName = "net.minecraft.client.renderer.Tessellator")
    public WorldVertexBufferUploader vboUploader;
	
    @Hook(targetMethod = "<init>")
    public static void Tessellator(Tessellator ts, int p_i1250_1_){
        ts.worldRenderer = new VertexBuffer(p_i1250_1_);
        ts.vboUploader = new WorldVertexBufferUploader();
    }

    @Hook(targetMethod = "<init>")
    public static void Tessellator(Tessellator ts){
        ts.vboUploader = new WorldVertexBufferUploader();
    }

    @Hook
    public static void draw(Tessellator ts){
        ts.worldRenderer.finishDrawing();
        ts.vboUploader.draw(ts.worldRenderer);
    }
    
    @Hook(createMethod = true, returnCondition = ReturnCondition.ALWAYS)
    public static VertexBuffer getBuffer(Tessellator ts, Tessellator ts2){
        return ts2.worldRenderer;
    }

}