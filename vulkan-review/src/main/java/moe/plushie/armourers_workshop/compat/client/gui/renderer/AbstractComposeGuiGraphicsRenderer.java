package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.apple.library.coregraphics.CGGraphicsElement;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement.Flush;
import moe.plushie.armourers_workshop.core.client.gui.element.ClipGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.SkinGuiElement;
import moe.plushie.armourers_workshop.core.client.other.SceneGraphicsContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

public class AbstractComposeGuiGraphicsRenderer extends PictureInPictureRenderer<AbstractComposeGuiGraphicsState> {
   private final NativeGuiLayers<AbstractComposeGuiGraphicsRenderer> layerRenderers = new NativeGuiLayers<>();
   private final boolean layerRenderer;

   public AbstractComposeGuiGraphicsRenderer() { this(false); }
   private AbstractComposeGuiGraphicsRenderer(boolean layerRenderer) { this.layerRenderer = layerRenderer; }

   public void prepare(AbstractComposeGuiGraphicsState renderState, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int i) {
      if (renderState.isEmpty()) return;
      if (layerRenderer) {
         super.prepare(renderState, guiRenderState, featureRenderDispatcher, i);
      } else {
         // BlitRenderState retains the texture view until all GUI layers have been prepared.
         // Give background/foreground independent targets so later clears cannot overwrite them.
         layerRenderers.target(renderState.toString(), () -> new AbstractComposeGuiGraphicsRenderer(true))
            .prepare(renderState, guiRenderState, featureRenderDispatcher, i);
      }
   }

   @Override public void close() {
      layerRenderers.close(AbstractComposeGuiGraphicsRenderer::close);
      super.close();
   }

   protected void renderToTexture(
      AbstractComposeGuiGraphicsState renderState, IPoseStack poseStack, IBufferSource bufferSource, SubmitNodeCollector submitNodeCollector
   ) {
      AbstractComposeGuiGraphicsRenderer.OffscreenDispatcherImpl dispatcher = new AbstractComposeGuiGraphicsRenderer.OffscreenDispatcherImpl(
         poseStack, bufferSource, submitNodeCollector, () -> this.executeSubmits(submitNodeCollector)
      );
      renderState.forEach((snapshot, element) -> {
         if (element instanceof Flush) {
            dispatcher.flush();
         } else {
            if (element instanceof ClipGuiElement) dispatcher.flush();
            poseStack.pushPose();
            var previousLights = RenderSystem.getShaderLights();
            try {
               poseStack.multiply(snapshot.ctm().pose());
               poseStack.multiply(snapshot.ctm().normal());
               if (element instanceof SkinGuiElement) {
                  Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
               }
               dispatcher.submit(element);
               // Keep this element's lights and PiP state bound until its callbacks execute.
               dispatcher.flush();
            } finally {
               RenderSystem.setShaderLights(previousLights);
               poseStack.popPose();
            }
         }
      });
      dispatcher.flush();
   }

   protected void renderToTexture(AbstractComposeGuiGraphicsState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      int width = renderState.x1() - renderState.x0();
      int height = renderState.y1() - renderState.y0();
      poseStack.translate(-width / 2.0F, -height, 0.0F);
      SubmitNodeCollector previous = AbstractBufferSource.setActiveCollector(submitNodeCollector);
      var previousLights = RenderSystem.getShaderLights();
      try (NativeGuiClip.Scope scope = NativeGuiClip.begin(poseStack.last().pose())) {
         this.renderToTexture(renderState, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(submitNodeCollector), submitNodeCollector);
      } finally {
         AbstractBufferSource.setActiveCollector(previous);
         RenderSystem.setShaderLights(previousLights);
      }
   }

   protected void executeSubmits(SubmitNodeCollector collector) {
      if (!(collector instanceof SubmitNodeStorage storage)) {
         throw new IllegalArgumentException("AW PiP requires an executable submit storage");
      }
      // Called between elements, never from a custom geometry callback (which would
      // re-enter the dispatcher's already-active PreparedFrame).
      Minecraft.getInstance().gameRenderer.featureRenderDispatcher().renderAllFeatures(storage);
   }

   public float getNear() {
      return -10000.0F;
   }

   public float getFar() {
      return 10000.0F;
   }

   protected String getTextureLabel() {
      return "core-graphics-implement";
   }

   public Class<AbstractComposeGuiGraphicsState> getRenderStateClass() {
      return AbstractComposeGuiGraphicsState.class;
   }

   static class OffscreenDispatcherImpl {
      private final IPoseStack poseStack;
      private final IBufferSource bufferSource;
      private final SubmitNodeCollector submitNodeCollector;
      private final Runnable executeSubmits;
      private int gamePassTotal = 0;
      private int normalPassTotal = 0;
      private IBufferSource gameBufferSource;
      private SceneGraphicsContext gameContext;

      public OffscreenDispatcherImpl(IPoseStack poseStack, IBufferSource bufferSource, SubmitNodeCollector submitNodeCollector, Runnable executeSubmits) {
         this.poseStack = poseStack;
         this.bufferSource = bufferSource;
         this.submitNodeCollector = submitNodeCollector;
         this.executeSubmits = executeSubmits;
      }

      public void submit(CGGraphicsElement element) {
         if (element instanceof IGraphicsElement element1) {
            this.gameContext().draw(element1);
            this.gamePassTotal++;
         } else {
            element.render(this.poseStack, this.bufferSource);
            this.normalPassTotal++;
         }
      }

      public void flush() {
         boolean hasSubmits = this.gamePassTotal != 0 || this.normalPassTotal != 0;
         if (this.gameBufferSource != null && this.gamePassTotal != 0) {
            this.gameContext.flush();
            this.gameBufferSource.endBatch();
            this.gamePassTotal = 0;
         }

         if (this.normalPassTotal != 0) {
            this.bufferSource.endBatch();
            this.normalPassTotal = 0;
         }

         if (hasSubmits) {
            // endBatch only enqueues callbacks. Execute this element now: native order(n)
            // cannot keep a translucent background ahead of a later solid model phase.
            // Draining also puts this background before the legacy immediate entity helper.
            this.executeSubmits.run();
         }
      }

      public IGraphicsContext gameContext() {
         if (this.gameContext == null) {
            this.gameContext = AbstractGraphicsRenderer.wrap(AbstractPoseStack.unwrap(this.poseStack), this.submitNodeCollector);
            this.gameBufferSource = AbstractBufferSource.wrap(this.submitNodeCollector);
         }

         return this.gameContext;
      }
   }
}
