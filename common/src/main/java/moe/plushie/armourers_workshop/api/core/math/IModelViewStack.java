package moe.plushie.armourers_workshop.api.core.math;

public interface IModelViewStack {

    void pushMatrix();

    void popMatrix();

    IMatrix4f last();
}
