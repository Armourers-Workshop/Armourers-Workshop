package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.math.AbstractModelViewStack;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.state.AbstractShaderObjectState;
import moe.plushie.armourers_workshop.compat.client.renderer.shader.state.AbstractShaderUniformState;
import moe.plushie.armourers_workshop.core.client.shader.ShaderContext;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.init.event.client.ClientShaderEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.lwjgl.opengl.GL11;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractShaderContext implements ShaderContext {

    protected final OpenVector2f polygonOffset = new OpenVector2f(0.0f, 0.0f);
    protected final AbstractModelViewStack modelViewStack = AbstractModelViewStack.getInstance();

    protected final AbstractShaderObjectState objects = new AbstractShaderObjectState();
    protected final AbstractShaderUniformState uniforms = new AbstractShaderUniformState();

    protected AbstractShaderContext() {
        EventManager.listen(ClientShaderEvent.Reloading.class, event -> uniforms.invalidateVariables());
    }

    public static AbstractShaderContext newInstance() {
        return AbstractShaderContextImpl.INSTANCE;
    }

    @Override
    public void saveUniforms() {
        uniforms.pushVariables(objects.programId);
    }

    @Override
    public void applyUniforms() {
        uniforms.applyVariables(objects.programId);
    }

    @Override
    public void restoreUniforms() {
        uniforms.popVariables(objects.programId);
    }

    @Override
    public void saveObjects() {
        objects.push();
    }

    @Override
    public void restoreObjects() {
        objects.pop();
    }

    @Override
    public void saveMatrices() {
        modelViewStack.pushMatrix();
        uniforms.pushMatrices();
    }

    @Override
    public void restoreMatrices() {
        uniforms.popMatrices();
        modelViewStack.popMatrix();
    }

    @Override
    public void saveStates() {
    }

    @Override
    public void restoreStates() {
    }

    @Override
    public void setTextureMatrix(OpenMatrix4f matrix) {
        uniforms.textureMatrix.set(matrix);
    }

    @Override
    public void setModelViewMatrix(OpenMatrix4f matrix) {
        uniforms.modelViewMatrix.set(matrix);

        // submit to context
        modelViewStack.last().set(matrix);
        modelViewStack.apply();
    }

    @Override
    public void setOverlayTextureMatrix(OpenMatrix4f matrix) {
        uniforms.overlayTextureMatrix.set(matrix);
    }

    @Override
    public void setLightmapTextureMatrix(OpenMatrix4f matrix) {
        uniforms.lightmapTextureMatrix.set(matrix);
    }

    @Override
    public void setObjectViewMatrix(OpenMatrix4f matrix) {
        uniforms.objectViewMatrix.set(matrix);
    }

    @Override
    public void setObjectNormalMatrix(OpenMatrix3f matrix) {
        uniforms.objectNormalMatrix.set(matrix);
    }

    @Override
    public void setMatrixFlags(int flags) {
        uniforms.matrixFlags.set(flags);
    }

    @Override
    public void setTintColor(float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    @Override
    public void setColorModulator(OpenVector4f value) {
        uniforms.colorModulator.set(value);
    }

    @Override
    public void setPolygonMode(int mode) {
        switch (mode) {
            case 1: { // line
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                break;
            }
            case 0: { // fill
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                break;
            }
        }
    }

    @Override
    public void setPolygonOffset(float factor, float units) {
        var newValue = new OpenVector2f(factor, units);
        if (newValue.equals(OpenVector2f.ZERO)) {
            if (!polygonOffset.equals(OpenVector2f.ZERO)) {
                RenderSystem.disablePolygonOffset();
            }
        } else {
            if (polygonOffset.equals(OpenVector2f.ZERO)) {
                RenderSystem.enablePolygonOffset();
            }
            RenderSystem.polygonOffset(factor, units);
        }
        polygonOffset.set(factor, units);
    }
}
