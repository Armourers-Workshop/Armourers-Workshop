package extensions.net.minecraft.client.renderer.RenderType;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.RenderType;

@Extension
@Available("[1.20, )")
public class ABI {

    public static void end(@This RenderType renderType, BufferBuilder bufferBuilder, int a, int b, int c) {
        renderType.end(bufferBuilder, VertexSorting.DISTANCE_TO_ORIGIN);
    }
}
