package ru.nanolive.draconicplus.common.fusioncrafting.client.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Quaternion;

@SideOnly(Side.CLIENT)
public class GlStateManager {
    private static final FloatBuffer BUF_FLOAT_16 = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer BUF_FLOAT_4 = BufferUtils.createFloatBuffer(4);
    private static GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState((GlStateManager.SyntheticClass_1)null);
    private static GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
    private static GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
    private static GlStateManager.ColorMaterialState colorMaterialState;
    private static GlStateManager.BlendState blendState;
    private static GlStateManager.DepthState depthState;
    private static GlStateManager.FogState fogState;
    private static GlStateManager.CullState cullState;
    private static GlStateManager.PolygonOffsetState polygonOffsetState;
    private static GlStateManager.ColorLogicState colorLogicState;
    private static GlStateManager.TexGenState texGenState;
    private static GlStateManager.ClearState clearState;
    private static GlStateManager.StencilState stencilState;
    private static GlStateManager.BooleanState normalizeState;
    private static int activeTextureUnit;
    private static GlStateManager.TextureState[] textureState;
    private static int activeShadeModel;
    private static GlStateManager.BooleanState rescaleNormalState;
    private static GlStateManager.ColorMask colorMaskState;
    private static GlStateManager.Color colorState;

    public static void pushAttrib() {
        GL11.glPushAttrib(8256);
    }

    public static void popAttrib() {
        GL11.glPopAttrib();
    }

    public static void disableAlpha() {
        alphaState.alphaTest.setDisabled();
    }

    public static void enableAlpha() {
        alphaState.alphaTest.setEnabled();
    }

    public static void alphaFunc(int p_alphaFunc_0_, float p_alphaFunc_1_) {
        if(p_alphaFunc_0_ != alphaState.func || p_alphaFunc_1_ != alphaState.ref) {
            alphaState.func = p_alphaFunc_0_;
            alphaState.ref = p_alphaFunc_1_;
            GL11.glAlphaFunc(p_alphaFunc_0_, p_alphaFunc_1_);
        }

    }

    public static void enableLighting() {
        lightingState.setEnabled();
    }

    public static void disableLighting() {
        lightingState.setDisabled();
    }

    public static void enableLight(int p_enableLight_0_) {
        lightState[p_enableLight_0_].setEnabled();
    }

    public static void disableLight(int p_disableLight_0_) {
        lightState[p_disableLight_0_].setDisabled();
    }

    public static void enableColorMaterial() {
        colorMaterialState.colorMaterial.setEnabled();
    }

    public static void disableColorMaterial() {
        colorMaterialState.colorMaterial.setDisabled();
    }

    public static void colorMaterial(int p_colorMaterial_0_, int p_colorMaterial_1_) {
        if(p_colorMaterial_0_ != colorMaterialState.face || p_colorMaterial_1_ != colorMaterialState.mode) {
            colorMaterialState.face = p_colorMaterial_0_;
            colorMaterialState.mode = p_colorMaterial_1_;
            GL11.glColorMaterial(p_colorMaterial_0_, p_colorMaterial_1_);
        }

    }

    public static void glLight(int p_glLight_0_, int p_glLight_1_, FloatBuffer p_glLight_2_) {
        GL11.glLight(p_glLight_0_, p_glLight_1_, p_glLight_2_);
    }

    public static void glLightModel(int p_glLightModel_0_, FloatBuffer p_glLightModel_1_) {
        GL11.glLightModel(p_glLightModel_0_, p_glLightModel_1_);
    }

    public static void glNormal3f(float p_glNormal3f_0_, float p_glNormal3f_1_, float p_glNormal3f_2_) {
        GL11.glNormal3f(p_glNormal3f_0_, p_glNormal3f_1_, p_glNormal3f_2_);
    }

    public static void disableDepth() {
        depthState.depthTest.setDisabled();
    }

    public static void enableDepth() {
        depthState.depthTest.setEnabled();
    }

    public static void depthFunc(int p_depthFunc_0_) {
        if(p_depthFunc_0_ != depthState.depthFunc) {
            depthState.depthFunc = p_depthFunc_0_;
            GL11.glDepthFunc(p_depthFunc_0_);
        }

    }

    public static void depthMask(boolean p_depthMask_0_) {
        if(p_depthMask_0_ != depthState.maskEnabled) {
            depthState.maskEnabled = p_depthMask_0_;
            GL11.glDepthMask(p_depthMask_0_);
        }

    }

    public static void disableBlend() {
        blendState.blend.setDisabled();
    }

    public static void enableBlend() {
        blendState.blend.setEnabled();
    }

    public static void blendFunc(GlStateManager.SourceFactor p_blendFunc_0_, GlStateManager.DestFactor p_blendFunc_1_) {
        blendFunc(p_blendFunc_0_.factor, p_blendFunc_1_.factor);
    }

    public static void blendFunc(int p_blendFunc_0_, int p_blendFunc_1_) {
        if(p_blendFunc_0_ != blendState.srcFactor || p_blendFunc_1_ != blendState.dstFactor) {
            blendState.srcFactor = p_blendFunc_0_;
            blendState.dstFactor = p_blendFunc_1_;
            GL11.glBlendFunc(p_blendFunc_0_, p_blendFunc_1_);
        }

    }

    public static void tryBlendFuncSeparate(GlStateManager.SourceFactor p_tryBlendFuncSeparate_0_, GlStateManager.DestFactor p_tryBlendFuncSeparate_1_, GlStateManager.SourceFactor p_tryBlendFuncSeparate_2_, GlStateManager.DestFactor p_tryBlendFuncSeparate_3_) {
        tryBlendFuncSeparate(p_tryBlendFuncSeparate_0_.factor, p_tryBlendFuncSeparate_1_.factor, p_tryBlendFuncSeparate_2_.factor, p_tryBlendFuncSeparate_3_.factor);
    }

    public static void tryBlendFuncSeparate(int p_tryBlendFuncSeparate_0_, int p_tryBlendFuncSeparate_1_, int p_tryBlendFuncSeparate_2_, int p_tryBlendFuncSeparate_3_) {
        if(p_tryBlendFuncSeparate_0_ != blendState.srcFactor || p_tryBlendFuncSeparate_1_ != blendState.dstFactor || p_tryBlendFuncSeparate_2_ != blendState.srcFactorAlpha || p_tryBlendFuncSeparate_3_ != blendState.dstFactorAlpha) {
            blendState.srcFactor = p_tryBlendFuncSeparate_0_;
            blendState.dstFactor = p_tryBlendFuncSeparate_1_;
            blendState.srcFactorAlpha = p_tryBlendFuncSeparate_2_;
            blendState.dstFactorAlpha = p_tryBlendFuncSeparate_3_;
            OpenGlHelper.glBlendFunc(p_tryBlendFuncSeparate_0_, p_tryBlendFuncSeparate_1_, p_tryBlendFuncSeparate_2_, p_tryBlendFuncSeparate_3_);
        }

    }

    public static void glBlendEquation(int p_glBlendEquation_0_) {
        GL14.glBlendEquation(p_glBlendEquation_0_);
    }

    public static void enableOutlineMode(int p_enableOutlineMode_0_) {
        BUF_FLOAT_4.put(0, (float)(p_enableOutlineMode_0_ >> 16 & 255) / 255.0F);
        BUF_FLOAT_4.put(1, (float)(p_enableOutlineMode_0_ >> 8 & 255) / 255.0F);
        BUF_FLOAT_4.put(2, (float)(p_enableOutlineMode_0_ >> 0 & 255) / 255.0F);
        BUF_FLOAT_4.put(3, (float)(p_enableOutlineMode_0_ >> 24 & 255) / 255.0F);
        glTexEnv(8960, 8705, BUF_FLOAT_4);
        glTexEnvi(8960, 8704, 34160);
        glTexEnvi(8960, 34161, 7681);
        glTexEnvi(8960, 34176, 34166);
        glTexEnvi(8960, 34192, 768);
        glTexEnvi(8960, 34162, 7681);
        glTexEnvi(8960, 34184, 5890);
        glTexEnvi(8960, 34200, 770);
    }

    public static void disableOutlineMode() {
        glTexEnvi(8960, 8704, 8448);
        glTexEnvi(8960, 34161, 8448);
        glTexEnvi(8960, 34162, 8448);
        glTexEnvi(8960, 34176, 5890);
        glTexEnvi(8960, 34184, 5890);
        glTexEnvi(8960, 34192, 768);
        glTexEnvi(8960, 34200, 770);
    }

    public static void enableFog() {
        fogState.fog.setEnabled();
    }

    public static void disableFog() {
        fogState.fog.setDisabled();
    }

    public static void setFog(GlStateManager.FogMode p_setFog_0_) {
        setFog(p_setFog_0_.capabilityId);
    }

    private static void setFog(int p_setFog_0_) {
        if(p_setFog_0_ != fogState.mode) {
            fogState.mode = p_setFog_0_;
            GL11.glFogi(2917, p_setFog_0_);
        }

    }

    public static void setFogDensity(float p_setFogDensity_0_) {
        if(p_setFogDensity_0_ != fogState.density) {
            fogState.density = p_setFogDensity_0_;
            GL11.glFogf(2914, p_setFogDensity_0_);
        }

    }

    public static void setFogStart(float p_setFogStart_0_) {
        if(p_setFogStart_0_ != fogState.start) {
            fogState.start = p_setFogStart_0_;
            GL11.glFogf(2915, p_setFogStart_0_);
        }

    }

    public static void setFogEnd(float p_setFogEnd_0_) {
        if(p_setFogEnd_0_ != fogState.end) {
            fogState.end = p_setFogEnd_0_;
            GL11.glFogf(2916, p_setFogEnd_0_);
        }

    }

    public static void glFog(int p_glFog_0_, FloatBuffer p_glFog_1_) {
        GL11.glFog(p_glFog_0_, p_glFog_1_);
    }

    public static void glFogi(int p_glFogi_0_, int p_glFogi_1_) {
        GL11.glFogi(p_glFogi_0_, p_glFogi_1_);
    }

    public static void enableCull() {
        cullState.cullFace.setEnabled();
    }

    public static void disableCull() {
        cullState.cullFace.setDisabled();
    }

    public static void cullFace(GlStateManager.CullFace p_cullFace_0_) {
        cullFace(p_cullFace_0_.mode);
    }

    private static void cullFace(int p_cullFace_0_) {
        if(p_cullFace_0_ != cullState.mode) {
            cullState.mode = p_cullFace_0_;
            GL11.glCullFace(p_cullFace_0_);
        }

    }

    public static void glPolygonMode(int p_glPolygonMode_0_, int p_glPolygonMode_1_) {
        GL11.glPolygonMode(p_glPolygonMode_0_, p_glPolygonMode_1_);
    }

    public static void enablePolygonOffset() {
        polygonOffsetState.polygonOffsetFill.setEnabled();
    }

    public static void disablePolygonOffset() {
        polygonOffsetState.polygonOffsetFill.setDisabled();
    }

    public static void doPolygonOffset(float p_doPolygonOffset_0_, float p_doPolygonOffset_1_) {
        if(p_doPolygonOffset_0_ != polygonOffsetState.factor || p_doPolygonOffset_1_ != polygonOffsetState.units) {
            polygonOffsetState.factor = p_doPolygonOffset_0_;
            polygonOffsetState.units = p_doPolygonOffset_1_;
            GL11.glPolygonOffset(p_doPolygonOffset_0_, p_doPolygonOffset_1_);
        }

    }

    public static void enableColorLogic() {
        colorLogicState.colorLogicOp.setEnabled();
    }

    public static void disableColorLogic() {
        colorLogicState.colorLogicOp.setDisabled();
    }

    public static void colorLogicOp(GlStateManager.LogicOp p_colorLogicOp_0_) {
        colorLogicOp(p_colorLogicOp_0_.opcode);
    }

    public static void colorLogicOp(int p_colorLogicOp_0_) {
        if(p_colorLogicOp_0_ != colorLogicState.opcode) {
            colorLogicState.opcode = p_colorLogicOp_0_;
            GL11.glLogicOp(p_colorLogicOp_0_);
        }

    }

    public static void enableTexGenCoord(GlStateManager.TexGen p_enableTexGenCoord_0_) {
        texGenCoord(p_enableTexGenCoord_0_).textureGen.setEnabled();
    }

    public static void disableTexGenCoord(GlStateManager.TexGen p_disableTexGenCoord_0_) {
        texGenCoord(p_disableTexGenCoord_0_).textureGen.setDisabled();
    }

    public static void texGen(GlStateManager.TexGen p_texGen_0_, int p_texGen_1_) {
        GlStateManager.TexGenCoord lvt_2_1_ = texGenCoord(p_texGen_0_);
        if(p_texGen_1_ != lvt_2_1_.param) {
            lvt_2_1_.param = p_texGen_1_;
            GL11.glTexGeni(lvt_2_1_.coord, 9472, p_texGen_1_);
        }

    }

    public static void texGen(GlStateManager.TexGen p_texGen_0_, int p_texGen_1_, FloatBuffer p_texGen_2_) {
        GL11.glTexGen(texGenCoord(p_texGen_0_).coord, p_texGen_1_, p_texGen_2_);
    }

    private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen p_texGenCoord_0_) {
        switch(GlStateManager.SyntheticClass_1.field_179175_a[p_texGenCoord_0_.ordinal()]) {
            case 1:
                return texGenState.s;
            case 2:
                return texGenState.t;
            case 3:
                return texGenState.r;
            case 4:
                return texGenState.q;
            default:
                return texGenState.s;
        }
    }

    public static void setActiveTexture(int p_setActiveTexture_0_) {
        if(activeTextureUnit != p_setActiveTexture_0_ - OpenGlHelper.defaultTexUnit) {
            activeTextureUnit = p_setActiveTexture_0_ - OpenGlHelper.defaultTexUnit;
            OpenGlHelper.setActiveTexture(p_setActiveTexture_0_);
        }

    }

    public static void enableTexture2D() {
        textureState[activeTextureUnit].texture2DState.setEnabled();
    }

    public static void disableTexture2D() {
        textureState[activeTextureUnit].texture2DState.setDisabled();
    }

    public static void glTexEnv(int p_glTexEnv_0_, int p_glTexEnv_1_, FloatBuffer p_glTexEnv_2_) {
        GL11.glTexEnv(p_glTexEnv_0_, p_glTexEnv_1_, p_glTexEnv_2_);
    }

    public static void glTexEnvi(int p_glTexEnvi_0_, int p_glTexEnvi_1_, int p_glTexEnvi_2_) {
        GL11.glTexEnvi(p_glTexEnvi_0_, p_glTexEnvi_1_, p_glTexEnvi_2_);
    }

    public static void glTexEnvf(int p_glTexEnvf_0_, int p_glTexEnvf_1_, float p_glTexEnvf_2_) {
        GL11.glTexEnvf(p_glTexEnvf_0_, p_glTexEnvf_1_, p_glTexEnvf_2_);
    }

    public static void glTexParameterf(int p_glTexParameterf_0_, int p_glTexParameterf_1_, float p_glTexParameterf_2_) {
        GL11.glTexParameterf(p_glTexParameterf_0_, p_glTexParameterf_1_, p_glTexParameterf_2_);
    }

    public static void glTexParameteri(int p_glTexParameteri_0_, int p_glTexParameteri_1_, int p_glTexParameteri_2_) {
        GL11.glTexParameteri(p_glTexParameteri_0_, p_glTexParameteri_1_, p_glTexParameteri_2_);
    }

    public static int glGetTexLevelParameteri(int p_glGetTexLevelParameteri_0_, int p_glGetTexLevelParameteri_1_, int p_glGetTexLevelParameteri_2_) {
        return GL11.glGetTexLevelParameteri(p_glGetTexLevelParameteri_0_, p_glGetTexLevelParameteri_1_, p_glGetTexLevelParameteri_2_);
    }

    public static int generateTexture() {
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int p_deleteTexture_0_) {
        GL11.glDeleteTextures(p_deleteTexture_0_);
        GlStateManager.TextureState[] lvt_1_1_ = textureState;
        int lvt_2_1_ = lvt_1_1_.length;

        for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
            GlStateManager.TextureState lvt_4_1_ = lvt_1_1_[lvt_3_1_];
            if(lvt_4_1_.textureName == p_deleteTexture_0_) {
                lvt_4_1_.textureName = -1;
            }
        }

    }

    public static void bindTexture(int p_bindTexture_0_) {
        if(p_bindTexture_0_ != textureState[activeTextureUnit].textureName) {
            textureState[activeTextureUnit].textureName = p_bindTexture_0_;
            GL11.glBindTexture(3553, p_bindTexture_0_);
        }

    }

    public static void glTexImage2D(int p_glTexImage2D_0_, int p_glTexImage2D_1_, int p_glTexImage2D_2_, int p_glTexImage2D_3_, int p_glTexImage2D_4_, int p_glTexImage2D_5_, int p_glTexImage2D_6_, int p_glTexImage2D_7_, IntBuffer p_glTexImage2D_8_) {
        GL11.glTexImage2D(p_glTexImage2D_0_, p_glTexImage2D_1_, p_glTexImage2D_2_, p_glTexImage2D_3_, p_glTexImage2D_4_, p_glTexImage2D_5_, p_glTexImage2D_6_, p_glTexImage2D_7_, p_glTexImage2D_8_);
    }

    public static void glTexSubImage2D(int p_glTexSubImage2D_0_, int p_glTexSubImage2D_1_, int p_glTexSubImage2D_2_, int p_glTexSubImage2D_3_, int p_glTexSubImage2D_4_, int p_glTexSubImage2D_5_, int p_glTexSubImage2D_6_, int p_glTexSubImage2D_7_, IntBuffer p_glTexSubImage2D_8_) {
        GL11.glTexSubImage2D(p_glTexSubImage2D_0_, p_glTexSubImage2D_1_, p_glTexSubImage2D_2_, p_glTexSubImage2D_3_, p_glTexSubImage2D_4_, p_glTexSubImage2D_5_, p_glTexSubImage2D_6_, p_glTexSubImage2D_7_, p_glTexSubImage2D_8_);
    }

    public static void glCopyTexSubImage2D(int p_glCopyTexSubImage2D_0_, int p_glCopyTexSubImage2D_1_, int p_glCopyTexSubImage2D_2_, int p_glCopyTexSubImage2D_3_, int p_glCopyTexSubImage2D_4_, int p_glCopyTexSubImage2D_5_, int p_glCopyTexSubImage2D_6_, int p_glCopyTexSubImage2D_7_) {
        GL11.glCopyTexSubImage2D(p_glCopyTexSubImage2D_0_, p_glCopyTexSubImage2D_1_, p_glCopyTexSubImage2D_2_, p_glCopyTexSubImage2D_3_, p_glCopyTexSubImage2D_4_, p_glCopyTexSubImage2D_5_, p_glCopyTexSubImage2D_6_, p_glCopyTexSubImage2D_7_);
    }

    public static void glGetTexImage(int p_glGetTexImage_0_, int p_glGetTexImage_1_, int p_glGetTexImage_2_, int p_glGetTexImage_3_, IntBuffer p_glGetTexImage_4_) {
        GL11.glGetTexImage(p_glGetTexImage_0_, p_glGetTexImage_1_, p_glGetTexImage_2_, p_glGetTexImage_3_, p_glGetTexImage_4_);
    }

    public static void enableNormalize() {
        normalizeState.setEnabled();
    }

    public static void disableNormalize() {
        normalizeState.setDisabled();
    }

    public static void shadeModel(int p_shadeModel_0_) {
        if(p_shadeModel_0_ != activeShadeModel) {
            activeShadeModel = p_shadeModel_0_;
            GL11.glShadeModel(p_shadeModel_0_);
        }

    }

    public static void enableRescaleNormal() {
        rescaleNormalState.setEnabled();
    }

    public static void disableRescaleNormal() {
        rescaleNormalState.setDisabled();
    }

    public static void viewport(int p_viewport_0_, int p_viewport_1_, int p_viewport_2_, int p_viewport_3_) {
        GL11.glViewport(p_viewport_0_, p_viewport_1_, p_viewport_2_, p_viewport_3_);
    }

    public static void colorMask(boolean p_colorMask_0_, boolean p_colorMask_1_, boolean p_colorMask_2_, boolean p_colorMask_3_) {
        if(p_colorMask_0_ != colorMaskState.red || p_colorMask_1_ != colorMaskState.green || p_colorMask_2_ != colorMaskState.blue || p_colorMask_3_ != colorMaskState.alpha) {
            colorMaskState.red = p_colorMask_0_;
            colorMaskState.green = p_colorMask_1_;
            colorMaskState.blue = p_colorMask_2_;
            colorMaskState.alpha = p_colorMask_3_;
            GL11.glColorMask(p_colorMask_0_, p_colorMask_1_, p_colorMask_2_, p_colorMask_3_);
        }

    }

    public static void clearDepth(double p_clearDepth_0_) {
        if(p_clearDepth_0_ != clearState.depth) {
            clearState.depth = p_clearDepth_0_;
            GL11.glClearDepth(p_clearDepth_0_);
        }

    }

    public static void clearColor(float p_clearColor_0_, float p_clearColor_1_, float p_clearColor_2_, float p_clearColor_3_) {
        if(p_clearColor_0_ != clearState.color.red || p_clearColor_1_ != clearState.color.green || p_clearColor_2_ != clearState.color.blue || p_clearColor_3_ != clearState.color.alpha) {
            clearState.color.red = p_clearColor_0_;
            clearState.color.green = p_clearColor_1_;
            clearState.color.blue = p_clearColor_2_;
            clearState.color.alpha = p_clearColor_3_;
            GL11.glClearColor(p_clearColor_0_, p_clearColor_1_, p_clearColor_2_, p_clearColor_3_);
        }

    }

    public static void clear(int p_clear_0_) {
        GL11.glClear(p_clear_0_);
    }

    public static void matrixMode(int p_matrixMode_0_) {
        GL11.glMatrixMode(p_matrixMode_0_);
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void getFloat(int p_getFloat_0_, FloatBuffer p_getFloat_1_) {
        GL11.glGetFloat(p_getFloat_0_, p_getFloat_1_);
    }

    public static void ortho(double p_ortho_0_, double p_ortho_2_, double p_ortho_4_, double p_ortho_6_, double p_ortho_8_, double p_ortho_10_) {
        GL11.glOrtho(p_ortho_0_, p_ortho_2_, p_ortho_4_, p_ortho_6_, p_ortho_8_, p_ortho_10_);
    }

    public static void rotate(float p_rotate_0_, float p_rotate_1_, float p_rotate_2_, float p_rotate_3_) {
        GL11.glRotatef(p_rotate_0_, p_rotate_1_, p_rotate_2_, p_rotate_3_);
    }

    public static void scale(float p_scale_0_, float p_scale_1_, float p_scale_2_) {
        GL11.glScalef(p_scale_0_, p_scale_1_, p_scale_2_);
    }

    public static void scale(double p_scale_0_, double p_scale_2_, double p_scale_4_) {
        GL11.glScaled(p_scale_0_, p_scale_2_, p_scale_4_);
    }

    public static void translate(float p_translate_0_, float p_translate_1_, float p_translate_2_) {
        GL11.glTranslatef(p_translate_0_, p_translate_1_, p_translate_2_);
    }

    public static void translate(double p_translate_0_, double p_translate_2_, double p_translate_4_) {
        GL11.glTranslated(p_translate_0_, p_translate_2_, p_translate_4_);
    }

    public static void multMatrix(FloatBuffer p_multMatrix_0_) {
        GL11.glMultMatrix(p_multMatrix_0_);
    }

    public static void rotate(Quaternion p_rotate_0_) {
        multMatrix(quatToGlMatrix(BUF_FLOAT_16, p_rotate_0_));
    }

    public static FloatBuffer quatToGlMatrix(FloatBuffer p_quatToGlMatrix_0_, Quaternion p_quatToGlMatrix_1_) {
        p_quatToGlMatrix_0_.clear();
        float lvt_2_1_ = p_quatToGlMatrix_1_.x * p_quatToGlMatrix_1_.x;
        float lvt_3_1_ = p_quatToGlMatrix_1_.x * p_quatToGlMatrix_1_.y;
        float lvt_4_1_ = p_quatToGlMatrix_1_.x * p_quatToGlMatrix_1_.z;
        float lvt_5_1_ = p_quatToGlMatrix_1_.x * p_quatToGlMatrix_1_.w;
        float lvt_6_1_ = p_quatToGlMatrix_1_.y * p_quatToGlMatrix_1_.y;
        float lvt_7_1_ = p_quatToGlMatrix_1_.y * p_quatToGlMatrix_1_.z;
        float lvt_8_1_ = p_quatToGlMatrix_1_.y * p_quatToGlMatrix_1_.w;
        float lvt_9_1_ = p_quatToGlMatrix_1_.z * p_quatToGlMatrix_1_.z;
        float lvt_10_1_ = p_quatToGlMatrix_1_.z * p_quatToGlMatrix_1_.w;
        p_quatToGlMatrix_0_.put(1.0F - 2.0F * (lvt_6_1_ + lvt_9_1_));
        p_quatToGlMatrix_0_.put(2.0F * (lvt_3_1_ + lvt_10_1_));
        p_quatToGlMatrix_0_.put(2.0F * (lvt_4_1_ - lvt_8_1_));
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(2.0F * (lvt_3_1_ - lvt_10_1_));
        p_quatToGlMatrix_0_.put(1.0F - 2.0F * (lvt_2_1_ + lvt_9_1_));
        p_quatToGlMatrix_0_.put(2.0F * (lvt_7_1_ + lvt_5_1_));
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(2.0F * (lvt_4_1_ + lvt_8_1_));
        p_quatToGlMatrix_0_.put(2.0F * (lvt_7_1_ - lvt_5_1_));
        p_quatToGlMatrix_0_.put(1.0F - 2.0F * (lvt_2_1_ + lvt_6_1_));
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(0.0F);
        p_quatToGlMatrix_0_.put(1.0F);
        p_quatToGlMatrix_0_.rewind();
        return p_quatToGlMatrix_0_;
    }

    public static void func_179131_c(float p_color_0_, float p_color_1_, float p_color_2_, float p_color_3_) {
        if(p_color_0_ != colorState.red || p_color_1_ != colorState.green || p_color_2_ != colorState.blue || p_color_3_ != colorState.alpha) {
            colorState.red = p_color_0_;
            colorState.green = p_color_1_;
            colorState.blue = p_color_2_;
            colorState.alpha = p_color_3_;
            GL11.glColor4f(p_color_0_, p_color_1_, p_color_2_, p_color_3_);
        }

    }



    public static void color(float p_color_0_, float p_color_1_, float p_color_2_, float p_color_3_) {
        if(p_color_0_ != colorState.red || p_color_1_ != colorState.green || p_color_2_ != colorState.blue || p_color_3_ != colorState.alpha) {
            colorState.red = p_color_0_;
            colorState.green = p_color_1_;
            colorState.blue = p_color_2_;
            colorState.alpha = p_color_3_;
            GL11.glColor4f(p_color_0_, p_color_1_, p_color_2_, p_color_3_);
        }

    }

    public static void color(float p_color_0_, float p_color_1_, float p_color_2_) {
        color(p_color_0_, p_color_1_, p_color_2_, 1.0F);
    }

    public static void glTexCoord2f(float p_glTexCoord2f_0_, float p_glTexCoord2f_1_) {
        GL11.glTexCoord2f(p_glTexCoord2f_0_, p_glTexCoord2f_1_);
    }

    public static void glVertex3f(float p_glVertex3f_0_, float p_glVertex3f_1_, float p_glVertex3f_2_) {
        GL11.glVertex3f(p_glVertex3f_0_, p_glVertex3f_1_, p_glVertex3f_2_);
    }

    public static void resetColor() {
        colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
    }

    public static void glNormalPointer(int p_glNormalPointer_0_, int p_glNormalPointer_1_, ByteBuffer p_glNormalPointer_2_) {
        GL11.glNormalPointer(p_glNormalPointer_0_, p_glNormalPointer_1_, p_glNormalPointer_2_);
    }

    public static void glTexCoordPointer(int p_glTexCoordPointer_0_, int p_glTexCoordPointer_1_, int p_glTexCoordPointer_2_, int p_glTexCoordPointer_3_) {
        GL11.glTexCoordPointer(p_glTexCoordPointer_0_, p_glTexCoordPointer_1_, p_glTexCoordPointer_2_, (long)p_glTexCoordPointer_3_);
    }

    public static void glTexCoordPointer(int p_glTexCoordPointer_0_, int p_glTexCoordPointer_1_, int p_glTexCoordPointer_2_, ByteBuffer p_glTexCoordPointer_3_) {
        GL11.glTexCoordPointer(p_glTexCoordPointer_0_, p_glTexCoordPointer_1_, p_glTexCoordPointer_2_, p_glTexCoordPointer_3_);
    }

    public static void glVertexPointer(int p_glVertexPointer_0_, int p_glVertexPointer_1_, int p_glVertexPointer_2_, int p_glVertexPointer_3_) {
        GL11.glVertexPointer(p_glVertexPointer_0_, p_glVertexPointer_1_, p_glVertexPointer_2_, (long)p_glVertexPointer_3_);
    }

    public static void glVertexPointer(int p_glVertexPointer_0_, int p_glVertexPointer_1_, int p_glVertexPointer_2_, ByteBuffer p_glVertexPointer_3_) {
        GL11.glVertexPointer(p_glVertexPointer_0_, p_glVertexPointer_1_, p_glVertexPointer_2_, p_glVertexPointer_3_);
    }

    public static void glColorPointer(int p_glColorPointer_0_, int p_glColorPointer_1_, int p_glColorPointer_2_, int p_glColorPointer_3_) {
        GL11.glColorPointer(p_glColorPointer_0_, p_glColorPointer_1_, p_glColorPointer_2_, (long)p_glColorPointer_3_);
    }

    public static void glColorPointer(int p_glColorPointer_0_, int p_glColorPointer_1_, int p_glColorPointer_2_, ByteBuffer p_glColorPointer_3_) {
        GL11.glColorPointer(p_glColorPointer_0_, p_glColorPointer_1_, p_glColorPointer_2_, p_glColorPointer_3_);
    }

    public static void glDisableClientState(int p_glDisableClientState_0_) {
        GL11.glDisableClientState(p_glDisableClientState_0_);
    }

    public static void glEnableClientState(int p_glEnableClientState_0_) {
        GL11.glEnableClientState(p_glEnableClientState_0_);
    }

    public static void glBegin(int p_glBegin_0_) {
        GL11.glBegin(p_glBegin_0_);
    }

    public static void glEnd() {
        GL11.glEnd();
    }

    public static void glDrawArrays(int p_glDrawArrays_0_, int p_glDrawArrays_1_, int p_glDrawArrays_2_) {
        GL11.glDrawArrays(p_glDrawArrays_0_, p_glDrawArrays_1_, p_glDrawArrays_2_);
    }

    public static void glLineWidth(float p_glLineWidth_0_) {
        GL11.glLineWidth(p_glLineWidth_0_);
    }

    public static void callList(int p_callList_0_) {
        GL11.glCallList(p_callList_0_);
    }

    public static void glDeleteLists(int p_glDeleteLists_0_, int p_glDeleteLists_1_) {
        GL11.glDeleteLists(p_glDeleteLists_0_, p_glDeleteLists_1_);
    }

    public static void glNewList(int p_glNewList_0_, int p_glNewList_1_) {
        GL11.glNewList(p_glNewList_0_, p_glNewList_1_);
    }

    public static void glEndList() {
        GL11.glEndList();
    }

    public static int glGenLists(int p_glGenLists_0_) {
        return GL11.glGenLists(p_glGenLists_0_);
    }

    public static void glPixelStorei(int p_glPixelStorei_0_, int p_glPixelStorei_1_) {
        GL11.glPixelStorei(p_glPixelStorei_0_, p_glPixelStorei_1_);
    }

    public static void glReadPixels(int p_glReadPixels_0_, int p_glReadPixels_1_, int p_glReadPixels_2_, int p_glReadPixels_3_, int p_glReadPixels_4_, int p_glReadPixels_5_, IntBuffer p_glReadPixels_6_) {
        GL11.glReadPixels(p_glReadPixels_0_, p_glReadPixels_1_, p_glReadPixels_2_, p_glReadPixels_3_, p_glReadPixels_4_, p_glReadPixels_5_, p_glReadPixels_6_);
    }

    public static int glGetError() {
        return GL11.glGetError();
    }

    public static String glGetString(int p_glGetString_0_) {
        return GL11.glGetString(p_glGetString_0_);
    }

    public static void glGetInteger(int p_glGetInteger_0_, IntBuffer p_glGetInteger_1_) {
        GL11.glGetInteger(p_glGetInteger_0_, p_glGetInteger_1_);
    }

    public static int glGetInteger(int p_glGetInteger_0_) {
        return GL11.glGetInteger(p_glGetInteger_0_);
    }

    static {
        int lvt_0_2_;
        for(lvt_0_2_ = 0; lvt_0_2_ < 8; ++lvt_0_2_) {
            lightState[lvt_0_2_] = new GlStateManager.BooleanState(16384 + lvt_0_2_);
        }

        colorMaterialState = new GlStateManager.ColorMaterialState((GlStateManager.SyntheticClass_1)null);
        blendState = new GlStateManager.BlendState((GlStateManager.SyntheticClass_1)null);
        depthState = new GlStateManager.DepthState((GlStateManager.SyntheticClass_1)null);
        fogState = new GlStateManager.FogState((GlStateManager.SyntheticClass_1)null);
        cullState = new GlStateManager.CullState((GlStateManager.SyntheticClass_1)null);
        polygonOffsetState = new GlStateManager.PolygonOffsetState((GlStateManager.SyntheticClass_1)null);
        colorLogicState = new GlStateManager.ColorLogicState((GlStateManager.SyntheticClass_1)null);
        texGenState = new GlStateManager.TexGenState((GlStateManager.SyntheticClass_1)null);
        clearState = new GlStateManager.ClearState((GlStateManager.SyntheticClass_1)null);
        stencilState = new GlStateManager.StencilState((GlStateManager.SyntheticClass_1)null);
        normalizeState = new GlStateManager.BooleanState(2977);
        activeTextureUnit = 0;
        textureState = new GlStateManager.TextureState[8];

        for(lvt_0_2_ = 0; lvt_0_2_ < 8; ++lvt_0_2_) {
            textureState[lvt_0_2_] = new GlStateManager.TextureState((GlStateManager.SyntheticClass_1)null);
        }

        activeShadeModel = 7425;
        rescaleNormalState = new GlStateManager.BooleanState(32826);
        colorMaskState = new GlStateManager.ColorMask((GlStateManager.SyntheticClass_1)null);
        colorState = new GlStateManager.Color();
    }

    // $FF: synthetic class
    @SideOnly(Side.CLIENT)
    static class SyntheticClass_1 {
        // $FF: synthetic field
        static final int[] field_179175_a = new int[GlStateManager.TexGen.values().length];

        static {
            try {
                field_179175_a[GlStateManager.TexGen.S.ordinal()] = 1;
            } catch (NoSuchFieldError var4) {
                ;
            }

            try {
                field_179175_a[GlStateManager.TexGen.T.ordinal()] = 2;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                field_179175_a[GlStateManager.TexGen.R.ordinal()] = 3;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_179175_a[GlStateManager.TexGen.Q.ordinal()] = 4;
            } catch (NoSuchFieldError var1) {
                ;
            }

        }
    }

    @SideOnly(Side.CLIENT)
    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private DestFactor(int p_i46519_3_) {
            this.factor = p_i46519_3_;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int factor;

        private SourceFactor(int p_i46514_3_) {
            this.factor = p_i46514_3_;
        }
    }

    @SideOnly(Side.CLIENT)
    static class BooleanState {
        private final int capability;
        private boolean currentState = false;

        public BooleanState(int p_i46267_1_) {
            this.capability = p_i46267_1_;
        }

        public void setDisabled() {
            this.setState(false);
        }

        public void setEnabled() {
            this.setState(true);
        }

        public void setState(boolean p_setState_1_) {
            if(p_setState_1_ != this.currentState) {
                this.currentState = p_setState_1_;
                if(p_setState_1_) {
                    GL11.glEnable(this.capability);
                } else {
                    GL11.glDisable(this.capability);
                }
            }

        }
    }

    @SideOnly(Side.CLIENT)
    static class Color {
        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public Color() {
        }

        public Color(float p_i46265_1_, float p_i46265_2_, float p_i46265_3_, float p_i46265_4_) {
            this.red = p_i46265_1_;
            this.green = p_i46265_2_;
            this.blue = p_i46265_3_;
            this.alpha = p_i46265_4_;
        }
    }

    @SideOnly(Side.CLIENT)
    static class ColorMask {
        public boolean red;
        public boolean green;
        public boolean blue;
        public boolean alpha;

        private ColorMask() {
            this.red = true;
            this.green = true;
            this.blue = true;
            this.alpha = true;
        }

        // $FF: synthetic method
        ColorMask(GlStateManager.SyntheticClass_1 p_i46485_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum TexGen {
        S,
        T,
        R,
        Q;
    }

    @SideOnly(Side.CLIENT)
    static class TexGenCoord {
        public GlStateManager.BooleanState textureGen;
        public int coord;
        public int param = -1;

        public TexGenCoord(int p_i46254_1_, int p_i46254_2_) {
            this.coord = p_i46254_1_;
            this.textureGen = new GlStateManager.BooleanState(p_i46254_2_);
        }
    }

    @SideOnly(Side.CLIENT)
    static class TexGenState {
        public GlStateManager.TexGenCoord s;
        public GlStateManager.TexGenCoord t;
        public GlStateManager.TexGenCoord r;
        public GlStateManager.TexGenCoord q;

        private TexGenState() {
            this.s = new GlStateManager.TexGenCoord(8192, 3168);
            this.t = new GlStateManager.TexGenCoord(8193, 3169);
            this.r = new GlStateManager.TexGenCoord(8194, 3170);
            this.q = new GlStateManager.TexGenCoord(8195, 3171);
        }

        // $FF: synthetic method
        TexGenState(GlStateManager.SyntheticClass_1 p_i46477_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class StencilState {
        public GlStateManager.StencilFunc field_179078_a;
        public int field_179076_b;
        public int field_179077_c;
        public int field_179074_d;
        public int field_179075_e;

        private StencilState() {
            this.field_179078_a = new GlStateManager.StencilFunc((GlStateManager.SyntheticClass_1)null);
            this.field_179076_b = -1;
            this.field_179077_c = 7680;
            this.field_179074_d = 7680;
            this.field_179075_e = 7680;
        }

        // $FF: synthetic method
        StencilState(GlStateManager.SyntheticClass_1 p_i46478_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class StencilFunc {
        public int field_179081_a;
        public int field_179079_b;
        public int field_179080_c;

        private StencilFunc() {
            this.field_179081_a = 519;
            this.field_179079_b = 0;
            this.field_179080_c = -1;
        }

        // $FF: synthetic method
        StencilFunc(GlStateManager.SyntheticClass_1 p_i46479_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class ClearState {
        public double depth;
        public GlStateManager.Color color;
        public int field_179204_c;

        private ClearState() {
            this.depth = 1.0D;
            this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
            this.field_179204_c = 0;
        }

        // $FF: synthetic method
        ClearState(GlStateManager.SyntheticClass_1 p_i46487_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class ColorLogicState {
        public GlStateManager.BooleanState colorLogicOp;
        public int opcode;

        private ColorLogicState() {
            this.colorLogicOp = new GlStateManager.BooleanState(3058);
            this.opcode = 5379;
        }

        // $FF: synthetic method
        ColorLogicState(GlStateManager.SyntheticClass_1 p_i46486_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class PolygonOffsetState {
        public GlStateManager.BooleanState polygonOffsetFill;
        public GlStateManager.BooleanState polygonOffsetLine;
        public float factor;
        public float units;

        private PolygonOffsetState() {
            this.polygonOffsetFill = new GlStateManager.BooleanState(32823);
            this.polygonOffsetLine = new GlStateManager.BooleanState(10754);
            this.factor = 0.0F;
            this.units = 0.0F;
        }

        // $FF: synthetic method
        PolygonOffsetState(GlStateManager.SyntheticClass_1 p_i46480_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class CullState {
        public GlStateManager.BooleanState cullFace;
        public int mode;

        private CullState() {
            this.cullFace = new GlStateManager.BooleanState(2884);
            this.mode = 1029;
        }

        // $FF: synthetic method
        CullState(GlStateManager.SyntheticClass_1 p_i46483_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class FogState {
        public GlStateManager.BooleanState fog;
        public int mode;
        public float density;
        public float start;
        public float end;

        private FogState() {
            this.fog = new GlStateManager.BooleanState(2912);
            this.mode = 2048;
            this.density = 1.0F;
            this.start = 0.0F;
            this.end = 1.0F;
        }

        // $FF: synthetic method
        FogState(GlStateManager.SyntheticClass_1 p_i46481_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class DepthState {
        public GlStateManager.BooleanState depthTest;
        public boolean maskEnabled;
        public int depthFunc;

        private DepthState() {
            this.depthTest = new GlStateManager.BooleanState(2929);
            this.maskEnabled = true;
            this.depthFunc = 513;
        }

        // $FF: synthetic method
        DepthState(GlStateManager.SyntheticClass_1 p_i46482_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class BlendState {
        public GlStateManager.BooleanState blend;
        public int srcFactor;
        public int dstFactor;
        public int srcFactorAlpha;
        public int dstFactorAlpha;

        private BlendState() {
            this.blend = new GlStateManager.BooleanState(3042);
            this.srcFactor = 1;
            this.dstFactor = 0;
            this.srcFactorAlpha = 1;
            this.dstFactorAlpha = 0;
        }

        // $FF: synthetic method
        BlendState(GlStateManager.SyntheticClass_1 p_i46488_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class ColorMaterialState {
        public GlStateManager.BooleanState colorMaterial;
        public int face;
        public int mode;

        private ColorMaterialState() {
            this.colorMaterial = new GlStateManager.BooleanState(2903);
            this.face = 1032;
            this.mode = 5634;
        }

        // $FF: synthetic method
        ColorMaterialState(GlStateManager.SyntheticClass_1 p_i46484_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class AlphaState {
        public GlStateManager.BooleanState alphaTest;
        public int func;
        public float ref;

        private AlphaState() {
            this.alphaTest = new GlStateManager.BooleanState(3008);
            this.func = 519;
            this.ref = -1.0F;
        }

        // $FF: synthetic method
        AlphaState(GlStateManager.SyntheticClass_1 p_i46489_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    static class TextureState {
        public GlStateManager.BooleanState texture2DState;
        public int textureName;

        private TextureState() {
            this.texture2DState = new GlStateManager.BooleanState(3553);
            this.textureName = 0;
        }

        // $FF: synthetic method
        TextureState(GlStateManager.SyntheticClass_1 p_i46476_1_) {
            this();
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int opcode;

        private LogicOp(int p_i46517_3_) {
            this.opcode = p_i46517_3_;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum CullFace {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int mode;

        private CullFace(int p_i46520_3_) {
            this.mode = p_i46520_3_;
        }
    }

    @SideOnly(Side.CLIENT)
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int capabilityId;

        private FogMode(int p_i46518_3_) {
            this.capabilityId = p_i46518_3_;
        }
    }
}