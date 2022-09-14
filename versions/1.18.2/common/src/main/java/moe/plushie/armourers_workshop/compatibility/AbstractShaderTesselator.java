package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.RenderType;

public class AbstractShaderTesselator {

    private static final AbstractShaderTesselator INSTANCE = new AbstractShaderTesselator();

    private RenderType renderType = RenderType.lines();

    public static AbstractShaderTesselator getInstance() {
        return INSTANCE;
    }

    public BufferBuilder begin(RenderType renderType) {
        this.renderType = renderType;
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(renderType.mode(), renderType.format());
        return builder;
    }

    public void end() {
        renderType.setupRenderState();
        Tesselator.getInstance().end();
        renderType.clearRenderState();
    }
}
