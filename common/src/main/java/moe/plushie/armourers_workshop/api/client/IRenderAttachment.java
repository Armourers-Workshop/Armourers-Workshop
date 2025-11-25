package moe.plushie.armourers_workshop.api.client;

public interface IRenderAttachment {

    void setupRenderState();

    void clearRenderState();

    default boolean shouldRemove() {
        return false;
    }
}
