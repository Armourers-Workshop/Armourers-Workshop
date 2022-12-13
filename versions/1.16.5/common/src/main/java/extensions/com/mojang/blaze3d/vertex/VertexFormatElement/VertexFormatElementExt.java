package extensions.com.mojang.blaze3d.vertex.VertexFormatElement;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class VertexFormatElementExt {

    public static void setupBufferState(@This VertexFormatElement element, int index, long offset, int strict) {
        element.setupBufferState(offset, strict);
    }
}
