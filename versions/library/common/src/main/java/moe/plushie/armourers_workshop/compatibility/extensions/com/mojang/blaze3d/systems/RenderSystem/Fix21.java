package moe.plushie.armourers_workshop.compatibility.extensions.com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.annotation.Available;

import java.util.function.IntSupplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class Fix21 {

    public static void glBindBuffer(@ThisClass Class<?> clazz, int i, IntSupplier j) {
        RenderSystem.glBindBuffer(i, j.getAsInt());
    }

    public static void glBindVertexArray(@ThisClass Class<?> clazz, IntSupplier i) {
        RenderSystem.glBindVertexArray(i.getAsInt());
    }
}
