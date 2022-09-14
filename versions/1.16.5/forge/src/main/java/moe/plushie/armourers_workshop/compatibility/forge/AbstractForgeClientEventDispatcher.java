package moe.plushie.armourers_workshop.compatibility.forge;

import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Supplier;

public abstract class AbstractForgeClientEventDispatcher {

    public static abstract class Handler {

        private static CGRect SCREEN_BOUNDS = CGRect.ZERO;

        public abstract void drawBlockHighlightEvent(BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers);

        public abstract void willRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, Supplier<SkinRenderContext> contextSupplier);

        public abstract void didRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, Supplier<SkinRenderContext> contextSupplier);

        public abstract void didRenderTooltip(ItemStack itemStack, CGRect frame, int mouseX, int mouseY, int screenWidth, int screenHeight, PoseStack poseStack);

        @SubscribeEvent
        public void _drawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
            drawBlockHighlightEvent(event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
        }

        @SubscribeEvent
        public void _willRenderLivingEntity(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            willRenderLivingEntity(event.getEntity(), event.getRenderer(), () -> {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
                return context;
            });
        }

        @SubscribeEvent
        public void _didRenderLivingEntity(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
            didRenderLivingEntity(event.getEntity(), event.getRenderer(), () -> {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getLight(), event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers());
                return context;
            });
        }

        @SubscribeEvent
        public void _willRenderTooltip(RenderTooltipEvent.Pre event) {
            SCREEN_BOUNDS = new CGRect(event.getX(), event.getY(), event.getScreenWidth(), event.getScreenHeight());
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public void _didRenderTooltip(RenderTooltipEvent.PostText event) {
            int mouseX = SCREEN_BOUNDS.getX();
            int mouseY = SCREEN_BOUNDS.getY();
            int screenWidth = SCREEN_BOUNDS.getWidth();
            int screenHeight = SCREEN_BOUNDS.getHeight();
            CGRect frame = new CGRect(event.getX(), event.getY(), event.getWidth(), event.getHeight());
            didRenderTooltip(event.getStack(), frame, mouseX, mouseY, screenWidth, screenHeight, event.getMatrixStack());
        }
    }
}
