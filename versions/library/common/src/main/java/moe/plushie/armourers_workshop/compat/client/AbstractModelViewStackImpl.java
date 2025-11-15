package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IModelViewStack;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import org.joml.Matrix4fStack;

@Available("[1.21, )")
public class AbstractModelViewStackImpl implements IModelViewStack {

    private final Matrix4fStack stack = RenderSystem.getModelViewStack();
    private final IMatrix4f entry = AbstractPoseStack.convertMatrix(stack);

    @Override
    public void pushMatrix() {
        stack.pushMatrix();
    }

    @Override
    public void popMatrix() {
        stack.popMatrix();
        apply();
    }

    public void apply() {
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public IMatrix4f last() {
        return entry;
    }
}
