package extensions.net.minecraft.client.gui.Font;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;

@Extension
public class FontExt {

    public static int drawInBatch2(@This Font font, FormattedCharSequence sequence, float f, float g, int i, boolean bl, IMatrix4f matrix, MultiBufferSource buffers, boolean bl2, int j, int k) {
        return font.drawInBatch(sequence, f, g, i, bl, AbstractPoseStack.of(matrix), buffers, bl2, j, k);
    }
}
