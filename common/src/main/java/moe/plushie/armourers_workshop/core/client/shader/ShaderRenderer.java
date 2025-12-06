package moe.plushie.armourers_workshop.core.client.shader;

public interface ShaderRenderer {

    void draw(ShaderVertexObject object, ShaderContext context);

    default void setupRenderState(ShaderVertexGroup group) {
    }

    default void clearRenderState(ShaderVertexGroup group) {
    }
}
