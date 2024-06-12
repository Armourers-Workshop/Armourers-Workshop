package moe.plushie.armourers_workshop.compatibility.extensions.com.mojang.blaze3d.vertex.VertexFormat;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.21, )")
@Extension
public class ABI {

    public static void setupBufferState(@This VertexFormat format, long offset) {
        int strict = format.getVertexSize();
        var elements = format.getElements();
        for (int index = 0; index < elements.size(); ++index) {
            GlStateManager._enableVertexAttribArray(index);
            var element = elements.get(index);
            element.setupBufferState(index, offset, strict);
            offset += element.byteSize();
        }
    }
}
