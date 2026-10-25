package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.coregraphics.CGGraphicsParameters;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGGraphicsState.Snapshot;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.gui.element.ClipGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement.TransparencyLayer;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement.ComposeLayer.Begin;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement.ComposeLayer.End;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2f;

public class AbstractGuiGraphicsRenderer implements CGGraphicsRenderer {
   private final int width;
   private final int height;
   private final CGGraphicsState state;
   private final GuiGraphicsExtractor graphics;
   private final ArrayList<AbstractGuiGraphicsRenderer.Entry> offscreenEntries = new ArrayList<>();
   private final ArrayList<AbstractComposeGuiGraphicsState> pipelines = new ArrayList<>();

   private AbstractGuiGraphicsRenderer(GuiGraphicsExtractor graphics, float width, float height) {
      this.state = new CGGraphicsState(AbstractPoseStack.wrap(new PoseStack()));
      this.graphics = graphics;
      this.width = (int)width;
      this.height = (int)height;
   }

   public static void wrap(
      GuiGraphicsExtractor graphics, float width, float height, float mouseX, float mouseY, float partialTick, Consumer<CGGraphicsContext> action
   ) {
      CGGraphicsParameters param = new CGGraphicsParameters(width, height, mouseX, mouseY, partialTick, graphics);
      AbstractGuiGraphicsRenderer renderer = new AbstractGuiGraphicsRenderer(graphics, width, height);
      CGGraphicsContext context = new CGGraphicsContext(renderer.state, param, renderer);
      context.draw(StateGuiElement.beginComposeLayer("main"));
      action.accept(context);
      context.draw(StateGuiElement.endComposeLayer("main"));
   }

   public static void unwrap(CGGraphicsContext context, String name, Consumer<GuiGraphicsExtractor> action) {
      GuiGraphicsExtractor graphics = (GuiGraphicsExtractor)context.param().context();
      context.draw(StateGuiElement.beginComposeLayer(name));
      action.accept(graphics);
      context.draw(StateGuiElement.endComposeLayer(name));
   }

   public void render(CGGraphicsElement element) {
      if (!(element instanceof TransparencyLayer)) {
         if (element instanceof Begin composeLayer) {
            switch (composeLayer.name()) {
               case "main":
                  break;
               case "container":
                  this.addPipeline("background", 0);
            }

         } else if (element instanceof End composeLayer) {
            switch (composeLayer.name()) {
               case "container":
                  this.addPipeline("foreground", 100);
                  break;
               case "main":
                  this.endBatch();
            }
         } else {
            this.offscreenEntries.add(new AbstractGuiGraphicsRenderer.Entry(element));
         }
      }
   }

   private void addPipeline(String name, int zIndex) {
      AbstractComposeGuiGraphicsState pipeline = new AbstractComposeGuiGraphicsState(
         name, new Matrix3x2f(this.graphics.pose()), 0, 0, this.width, this.height, 1.0F, zIndex
      );
      try {
         var field = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
         field.setAccessible(true);
         ((net.minecraft.client.renderer.state.gui.GuiRenderState) field.get(this.graphics)).addPicturesInPictureState(pipeline);
      } catch (ReflectiveOperationException exception) {
         throw new IllegalStateException("Cannot submit Armourers Workshop GUI pipeline", exception);
      }
      this.pipelines.add(pipeline);
   }


   private void endBatch() {
      if (this.pipelines.isEmpty()) {
         this.addPipeline("default", 0);
      }

      for (AbstractGuiGraphicsRenderer.Entry entry : this.offscreenEntries) {
         if (entry.element instanceof ClipGuiElement) {
            this.pipelines.forEach(it -> it.submit(entry.element, entry.snapshot));
         } else {
            AbstractComposeGuiGraphicsState selectedPipeline = this.pipelines.get(this.pipelines.size() - 1);
            Iterator var4 = this.pipelines.iterator();

            while (true) {
               if (var4.hasNext()) {
                  AbstractComposeGuiGraphicsState childPipeline = (AbstractComposeGuiGraphicsState)var4.next();
                  if (!(entry.zIndex <= childPipeline.zIndex())) {
                     continue;
                  }

                  selectedPipeline = childPipeline;
               }

               selectedPipeline.submit(entry.element, entry.snapshot);
               break;
            }
         }
      }
   }

   private class Entry {
      private final float zIndex;
      private final Snapshot snapshot = AbstractGuiGraphicsRenderer.this.state.snapshot();
      private final CGGraphicsElement element;

      public Entry(CGGraphicsElement element) {
         this.element = element;
         this.zIndex = this.snapshot.ctm().pose().m32;
      }

      @Override
      public String toString() {
         return this.element.getClass().getName().replaceFirst("^.+\\.([^.]+)$", "$1");
      }
   }
}
