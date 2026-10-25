package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import java.util.List;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.NativeGuiClip;

/** Clip state travels with deferred Vulkan draw nodes rather than mutable OpenGL framebuffer state. */
public class ClipManagerImpl {
    public void addClipPath(CGRect clipBox, List<CGRect> cornerBoxes) {
        float radius = cornerBoxes == null || cornerBoxes.isEmpty() ? 0 : Math.abs(cornerBoxes.get(0).width());
        NativeGuiClip.push(clipBox.x(),clipBox.y(),clipBox.width(),clipBox.height(),radius);
    }
    public void removeClipPath() { NativeGuiClip.pop(); }
}
