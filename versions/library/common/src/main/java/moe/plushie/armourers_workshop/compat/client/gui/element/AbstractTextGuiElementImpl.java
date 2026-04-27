package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.AbstractGuiGraphicsRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collection;
import java.util.List;

@Available("[20, 26)")
@OnlyIn(Dist.CLIENT)
public interface AbstractTextGuiElementImpl {

    default void renderText(Collection<FormattedCharSequence> lines, float tx, float ty, int textColor, boolean shadow, Font font, IPoseStack poseStack, IBufferSource bufferSource) {
        var pose = AbstractPoseStack.unwrap(poseStack).last();
        var bufferSource1 = AbstractBufferSource.unwrap(bufferSource);
        for (var line : lines) {
            // why bl2 no needs?
            var offset = font.drawInBatch(line, tx, ty, textColor, shadow, pose.pose(), bufferSource1, Font.DisplayMode.NORMAL, 0, 0xf000f0);
            if (offset == tx) {
                ty += 7;
            } else {
                ty += 10;
            }
        }
    }

    default void renderTextTooltip(List<FormattedCharSequence> lines, float mouseX, float mouseY, Font font, CGGraphicsContext context) {
        AbstractGuiGraphicsRenderer.unwrap(context, "tooltip", graphics -> {
            graphics.renderTooltip(font, lines, (int) mouseX, (int) mouseY);
        });
    }
}
