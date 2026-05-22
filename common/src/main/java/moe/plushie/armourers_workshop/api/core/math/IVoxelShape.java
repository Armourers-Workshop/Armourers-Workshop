package moe.plushie.armourers_workshop.api.core.math;

public interface IVoxelShape {

    IRectangle3f bounds();

    void visit(LineConsumer consumer);

    interface LineConsumer {

        void accept(float x0, float y0, float z0, float x1, float y1, float z1);
    }
}
