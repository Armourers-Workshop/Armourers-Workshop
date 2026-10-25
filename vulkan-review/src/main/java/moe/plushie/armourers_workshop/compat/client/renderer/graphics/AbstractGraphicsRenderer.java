package moe.plushie.armourers_workshop.compat.client.renderer.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.NativeGuiClip;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.NativeGuiRenderTypes;
import java.util.function.Consumer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractRenderPipeline;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferBuilder;
import moe.plushie.armourers_workshop.core.client.other.SceneGraphicsContext;
import moe.plushie.armourers_workshop.core.client.other.SceneGraphicsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jetbrains.annotations.Nullable;

public class AbstractGraphicsRenderer implements SceneGraphicsRenderer {
   private static final SubmitNodeStorage DEFAULT_SUBMITS = new SubmitNodeStorage();
   public static final AbstractGraphicsRenderer OUTLINE = new AbstractGraphicsRenderer(new PoseStack(), DEFAULT_SUBMITS, null);
   public static final AbstractGraphicsRenderer TESSELATOR = new AbstractGraphicsRenderer(new PoseStack(), DEFAULT_SUBMITS, null);
   protected Consumer<AbstractGraphicsRenderer.Invoker<? super EntityRenderState>> call;
   protected final PoseStack poseStack;
   protected final SubmitNodeCollector submitNodeCollector;
   protected final CameraRenderState cameraRenderState;
   protected final IPoseStack wrappedPoseStack;

   public AbstractGraphicsRenderer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
      this.poseStack = poseStack;
      this.submitNodeCollector = submitNodeCollector;
      this.cameraRenderState = cameraRenderState;
      this.wrappedPoseStack = AbstractPoseStack.wrap(poseStack);
   }

   public static SceneGraphicsContext wrap(PoseStack poseStack) {
      return wrap(poseStack, getDefaultSubmitNodeCollector(), null);
   }

   public static SceneGraphicsContext wrap(PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      return wrap(poseStack, submitNodeCollector, null);
   }

   public static SceneGraphicsContext wrap(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
      return new SceneGraphicsContext(new AbstractGraphicsRenderer(poseStack, submitNodeCollector, cameraRenderState));
   }

   public static SceneGraphicsContext wrap(
      EntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState
   ) {
      AbstractGraphicsRenderer renderer = new AbstractGraphicsRenderer(poseStack, submitNodeCollector, cameraRenderState);
      renderer.call = impl -> impl.accept(renderState, poseStack, submitNodeCollector, cameraRenderState);
      return new SceneGraphicsContext(renderer);
   }

   public static <T extends EntityRenderState> AbstractGraphicsRenderer.Invoker<T> invoke(AbstractGraphicsRenderer.Invoker<T> call) {
      return call;
   }

   public static SubmitNodeCollector getDefaultSubmitNodeCollector() {
      return DEFAULT_SUBMITS;
   }

   public void submit(IGraphicsRenderable renderable) {
      IRenderType renderType = renderable.renderType();
      moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager.getInstance().pin(renderType);
      NativeGuiClip.Snapshot clip = NativeGuiClip.snapshot();
         NativeGuiClip.ordered(this.submitNodeCollector)
            .submitCustomGeometry(
               this.poseStack,
               clip.shapes().isEmpty() ? (RenderType)renderType.get() : NativeGuiRenderTypes.independent((RenderType)renderType.get()),
               (pose, builder) -> {
                  var clipped = clip.wrap(builder, ((RenderType)renderType.get()).primitiveTopology());
                  renderable.render(AbstractPoseStack.wrap(pose), AbstractBufferBuilder.wrap(clipped == null ? builder : clipped));
                  if (clipped != null) clipped.finish();
               }
            );
   }

   public void submit(AbstractGraphicsRenderable renderable) {
      renderable.render(this.poseStack, this.submitNodeCollector, this.cameraRenderState);
   }

   public void flush() {
      if (this.submitNodeCollector == DEFAULT_SUBMITS) {
         Minecraft.getInstance().gameRenderer.featureRenderDispatcher().renderAllFeatures(DEFAULT_SUBMITS);
      }
   }

   public IPoseStack poseStack() {
      return this.wrappedPoseStack;
   }

   @FunctionalInterface
   public interface Invoker<S extends EntityRenderState> extends IGraphicsElement {
      void accept(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4);

      default void prepare(IGraphicsContext context) {
         if (context instanceof SceneGraphicsContext impl && impl.renderer() instanceof AbstractGraphicsRenderer renderer) {
            ((Consumer) renderer.call).accept(this);
         }
      }
   }
}
