package extensions.net.minecraft.core.Registry;

import com.apple.library.coregraphics.CGRect;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.List;
import java.util.function.Consumer;

@Available("[1.19, )")
@Extension
public class ClientEventProvider {

    public static void willRenderTooltipFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderTooltip consumer) {
        NotificationCenterImpl.observer(RenderTooltipEvent.Pre.class, event -> {
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
            consumer.render(event.getItemStack(), frame, mouseX, mouseY, screenWidth, screenHeight, event.getPoseStack());
        });
    }

    public static void willRenderBlockHighlightFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderBlockHighlight renderer) {
        NotificationCenterImpl.observer(RenderHighlightEvent.Block.class, event -> {
            renderer.render(event.getTarget(), event.getCamera(), event.getPoseStack(), event.getMultiBufferSource());
        });
    }

    public static void willRenderLivingEntityFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Pre.class, event -> {
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
    }

    public static void didRenderLivingEntityFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Post.class, event -> {
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
    }

    public static void willPlayerEnterFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        NotificationCenterImpl.observer(ClientPlayerNetworkEvent.LoggingIn.class, consumer, ClientPlayerNetworkEvent::getPlayer);
    }

    public static void willPlayerLeaveFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        NotificationCenterImpl.observer(ClientPlayerNetworkEvent.LoggingOut.class, consumer, ClientPlayerNetworkEvent::getPlayer);
    }


    public static void willRegisterItemColorFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ItemColorRegistry> consumer) {
        NotificationCenterImpl.observer(RegisterColorHandlersEvent.Item.class, consumer, event -> (provider, values) -> event.getItemColors().register(provider::getTintColor, values));
    }

    public static void willRegisterBlockColorFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.BlockColorRegistry> consumer) {
        NotificationCenterImpl.observer(RegisterColorHandlersEvent.Block.class, consumer, event -> (provider, values) -> event.getBlockColors().register(provider::getTintColor, values));
    }

    public static void willRegisterModelFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ModelRegistry> consumer) {
        NotificationCenterImpl.observer(ModelEvent.RegisterAdditional.class, consumer, event -> event::register);
    }

    public static void willRegisterKeyMappingFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.KeyMappingRegistry> consumer) {
        NotificationCenterImpl.observer(RegisterKeyMappingsEvent.class, consumer, event -> event::register);
    }

    public static void willRegisterItemPropertyFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ItemPropertyRegistry> consumer) {
        EnvironmentExecutor.didInit(EnvironmentType.COMMON, () -> () -> {
            consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
        });
    }
}
