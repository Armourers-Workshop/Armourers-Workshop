package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.List;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.AbstractGuiGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractRenderPipelineImpl;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

public interface AbstractTextGuiElementImpl {
   default void renderText(
      Collection<FormattedCharSequence> lines, float tx, float ty, int textColor, boolean shadow, Font font, IPoseStack poseStack, IBufferSource bufferSource
   ) {
      PoseStack poseStack1 = AbstractPoseStack.unwrap(poseStack);
      if (bufferSource instanceof AbstractBufferSource source) {
         source.submitText(font, poseStack1, lines, tx, ty, textColor, shadow);
      } else {
         if (!(bufferSource instanceof AbstractRenderPipelineImpl pipeline)) {
            throw new IllegalArgumentException("Unsupported 26.2 GUI text submit target: " + bufferSource.getClass().getName());
         }

         pipeline.submitText(poseStack1, lines, tx, ty, textColor, shadow);
      }
   }

   default void renderTextTooltip(List<FormattedCharSequence> lines, float mouseX, float mouseY, Font font, CGGraphicsContext context) {
      AbstractGuiGraphicsRenderer.unwrap(context, "tooltip", graphics -> graphics.setTooltipForNextFrame(font, lines, (int)mouseX, (int)mouseY));
   }
}
