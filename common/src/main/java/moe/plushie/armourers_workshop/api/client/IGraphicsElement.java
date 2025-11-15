package moe.plushie.armourers_workshop.api.client;

public interface IGraphicsElement {

    default void prepare(IGraphicsContext context) {
        // nop
    }
}
