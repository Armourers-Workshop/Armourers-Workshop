package moe.plushie.armourers_workshop.init.platform.forge.provider;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.function.Consumer;

public interface ClientNativeProviderImpl extends ClientNativeProvider {

    void willPlayerEnter(Consumer<Player> consumer);

    void willPlayerLeave(Consumer<Player> consumer);

    void willRenderBlockHighlight(RenderBlockHighlight renderer);

    void willRenderLivingEntity(RenderLivingEntity renderer);

    void didRenderLivingEntity(RenderLivingEntity renderer);

//    @Override
//    default void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
//        EnvironmentExecutor.didInit(EnvironmentType.COMMON, () -> () -> {
//            consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
//        });
//    }
//
//    @Override
//    default void willRegisterTexture(Consumer<TextureRegistry> consumer) {
//        NotificationCenterImpl.observer(TextureStitchEvent.Pre.class, consumer, event -> registryName -> {
//            if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
//                event.addSprite(registryName);
//            }
//        });
//    }

    @Override
    default void willPlayerLogin(Consumer<Player> consumer) {
        willPlayerEnter(player -> {
            // TODO: test in server @SAGESSE
            if (player != null && player.equals(Minecraft.getInstance().player)) {
                consumer.accept(player);
            }
        });
    }

    @Override
    default void willPlayerLogout(Consumer<Player> consumer) {
        willPlayerLeave(player -> {
            // TODO: test in server @SAGESSE
            if (player == null || player.equals(Minecraft.getInstance().player)) {
                consumer.accept(player);
            }
        });
    }

    @Override
    default void willTick(Consumer<Boolean> consumer) {
        NotificationCenterImpl.observer(TickEvent.RenderTickEvent.class, event -> {
            if (event.phase == TickEvent.Phase.START) {
                consumer.accept(Minecraft.getInstance().isPaused());
            }
        });
    }

    @Override
    default void willInput(Consumer<Minecraft> consumer) {
        NotificationCenterImpl.observer(TickEvent.ClientTickEvent.class, event -> {
            if (event.phase == TickEvent.Phase.END) {
                consumer.accept(Minecraft.getInstance());
            }
        });
    }

    @Override
    default void willGatherTooltip(GatherTooltip consumer) {
        NotificationCenterImpl.observer(ItemTooltipEvent.class, event -> consumer.gather(event.getItemStack(), event.getToolTip(), event.getFlags()));
    }

//    @Override
//    default void willRenderTooltip(RenderTooltip consumer) {
//        NotificationCenterImpl.observer(RenderTooltipEvent.Pre.class, event -> {
//            Font font = event.getFont();
//            List<ClientTooltipComponent> tooltips = event.getComponents();
//            int mouseX = event.getX();
//            int mouseY = event.getY();
//            int screenWidth = event.getScreenWidth();
//            int screenHeight = event.getScreenHeight();
//            int i = 0;
//            int j = tooltips.size() == 1 ? -2 : 0;
//            for (ClientTooltipComponent tooltip : tooltips) {
//                int k = tooltip.getWidth(font);
//                if (k > i) {
//                    i = k;
//                }
//                j += tooltip.getHeight();
//            }
//            int j2 = mouseX + 12;
//            int k2 = mouseY - 12;
//            if (j2 + i > screenWidth) {
//                j2 -= 28 + i;
//            }
//            if (k2 + j + 6 > screenHeight) {
//                k2 = screenHeight - j - 6;
//            }
//            CGRect frame = new CGRect(j2, k2, i, j);
//            consumer.render(event.getItemStack(), frame, mouseX, mouseY, screenWidth, screenHeight, event.getPoseStack());
//        });
//    }

    interface RenderBlockHighlight {
        void render(BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers);
    }
}
