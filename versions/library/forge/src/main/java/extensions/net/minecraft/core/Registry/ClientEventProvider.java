package extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class ClientEventProvider {

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
