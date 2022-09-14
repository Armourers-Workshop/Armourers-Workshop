package moe.plushie.armourers_workshop.compatibility.forge;

import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractForgeClientEventDispatcher {

    public static abstract class Handler {

        public abstract void drawBlockHighlightEvent(BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers);

        public abstract void willRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, Supplier<SkinRenderContext> contextSupplier);

        public abstract void didRenderLivingEntity(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, Supplier<SkinRenderContext> contextSupplier);

        public abstract void didRenderTooltip(ItemStack itemStack, CGRect frame, int mouseX, int mouseY, int screenWidth, int screenHeight, PoseStack poseStack);

        @SubscribeEvent
        public void _drawBlockHighlightEvent(DrawSelectionEvent.HighlightBlock event) {
            drawBlockHighlightEvent(event.getTarget(), event.getCamera(), event.getPoseStack(), event.getMultiBufferSource());
        }

        @SubscribeEvent
        public void _willRenderLivingEntity(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            willRenderLivingEntity(event.getEntity(), event.getRenderer(), () -> {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getPackedLight(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource());
                return context;
            });
        }

        @SubscribeEvent
        public void _didRenderLivingEntity(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
            didRenderLivingEntity(event.getEntity(), event.getRenderer(), () -> {
                SkinRenderContext context = SkinRenderContext.getInstance();
                context.setup(event.getPackedLight(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource());
                return context;
            });
        }

        @SubscribeEvent
        public void _willRenderTooltip(RenderTooltipEvent.Pre event) {
            Font font = event.getFont();
            List<ClientTooltipComponent> tooltips = event.getComponents();
            int mouseX = event.getX();
            int mouseY = event.getY();
            int screenWidth = event.getScreenWidth();
            int screenHeight = event.getScreenHeight();
            int i = 0;
            int j = tooltips.size() == 1 ? -2 : 0;
            for (ClientTooltipComponent tooltip : tooltips) {
                int k = tooltip.getWidth(font);
                if (k > i) {
                    i = k;
                }
                j += tooltip.getHeight();
            }
            int j2 = mouseX + 12;
            int k2 = mouseY - 12;
            if (j2 + i > screenWidth) {
                j2 -= 28 + i;
            }
            if (k2 + j + 6 > screenHeight) {
                k2 = screenHeight - j - 6;
            }
            CGRect frame = new CGRect(j2, k2, i, j);
            didRenderTooltip(event.getItemStack(), frame, mouseX, mouseY, screenWidth, screenHeight, event.getPoseStack());
        }
    }
}
