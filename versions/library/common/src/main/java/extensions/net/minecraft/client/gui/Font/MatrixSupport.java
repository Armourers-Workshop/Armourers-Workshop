package extensions.net.minecraft.client.gui.Font;

import org.joml.Matrix4f;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class MatrixSupport {

    public static int drawInBatch(@This Font font, FormattedCharSequence sequence, float f, float g, int i, boolean bl, Matrix4f pose, MultiBufferSource buffers, boolean bl2, int j, int k) {
        // why bl2 no needs?
        return font.drawInBatch(sequence, f, g, i, bl, pose, buffers, Font.DisplayMode.NORMAL, j, k);
    }
}
