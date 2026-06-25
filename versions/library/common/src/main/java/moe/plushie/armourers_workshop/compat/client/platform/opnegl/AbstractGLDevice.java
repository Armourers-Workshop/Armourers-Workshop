package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.compat.client.math.AbstractModelViewStack;
import moe.plushie.armourers_workshop.compat.client.platform.AbstractRenderDevice;
import moe.plushie.armourers_workshop.core.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.event.client.ClientShaderEvent;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class AbstractGLDevice implements AbstractRenderDevice {

    protected final OpenVector2f polygonOffset = new OpenVector2f(0.0f, 0.0f);
    protected final AbstractModelViewStack modelViewStack = AbstractModelViewStack.getInstance();

    protected final AbstractGLObjectState objects = new AbstractGLObjectState();
    protected final AbstractGLUniformState uniforms = new AbstractGLUniformState();

    public AbstractGLDevice() {
        EventManager.listen(ClientShaderEvent.Reloading.class, event -> uniforms.invalidateVariables());
    }

    @Override
    public void beginTransaction() {
        modelViewStack.pushMatrix();
        uniforms.pushMatrices();

        setTintColor(1.0f, 1.0f, 1.0f, 1.0f);
        setModelViewMatrix(OpenMatrix4f.identity());
        setMatrixFlags(0x01);

        if (ModDebugger.wireframeRender) {
            setPolygonMode(1);
        }
    }

    @Override
    public void beginGroup() {
        objects.push();
        uniforms.pushVariables(objects.programId);
    }

    @Override
    public void draw(IMeshData meshData) {
        // yes, we need update the uniform every render call.
        // maybe need query uniform from current shader.
        uniforms.applyVariables(objects.programId);

        // submit draw into renderer.
        if (meshData instanceof Runnable handler) {
            handler.run();
        }
    }

    @Override
    public void endGroup() {
        uniforms.popVariables(objects.programId);
        objects.pop();
    }

    @Override
    public void endTransaction() {
        if (ModDebugger.wireframeRender) {
            setPolygonMode(0);
        }

        setMatrixFlags(0x00);

        uniforms.popMatrices();
        modelViewStack.popMatrix();
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


    @Override
    public AbstractGLVertexBuffer createVertexBuffer(ByteBuffer bytes) {
        return new AbstractGLVertexBuffer(bytes);
    }
}
