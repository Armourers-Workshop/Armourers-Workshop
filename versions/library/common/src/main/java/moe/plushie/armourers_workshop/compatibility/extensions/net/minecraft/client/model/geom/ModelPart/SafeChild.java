package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.model.geom.ModelPart;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.model.geom.ModelPart;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.18, )")
public class SafeChild {

    public static ModelPart getSafeChild(@This ModelPart part, String name) {
        try {
            if (part == null) {
                return null;
            }
            return part.getChild(name);
        } catch (Exception e) {
            // vanilla throws an exception when the node doesn't exist.
            // but we think it is not a fatal exception, ignore it.
            return null;
        }
    }
}
