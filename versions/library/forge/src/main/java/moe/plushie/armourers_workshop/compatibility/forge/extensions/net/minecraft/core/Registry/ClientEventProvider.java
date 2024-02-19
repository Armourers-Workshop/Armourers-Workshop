package moe.plushie.armourers_workshop.compatibility.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEvents;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeEventBus;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.19, )")
@Extension
public class ClientEventProvider {

    public static void willRenderBlockHighlightFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderBlockHighlight renderer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.RENDER_HIGHLIGHT, event -> {
            renderer.render(event.getTarget(), event.getCamera(), event.getPoseStack(), event.getMultiBufferSource());
        });
    }

    public static void willRenderLivingEntityFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderLivingEntity renderer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.RENDER_LIVING_ENTITY_PRE, event -> {
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
    }

    public static void didRenderLivingEntityFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderLivingEntity renderer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.RENDER_LIVING_ENTITY_POST, event -> {
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer());
        });
    }

    public static void willRenderSpecificHandFO(@ThisClass Class<?> clazz, ClientNativeProvider.RenderSpecificHand renderer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.RENDER_SPECIFIC_HAND, event -> {
            renderer.render(event.getPlayer(), event.getArm(), event.getPackedLight(), event.getPoseStack(), event.getMultiBufferSource(), () -> event.setCanceled(true));
        });
    }

    public static void willRenderTickStartFO(@ThisClass Class<?> clazz, Consumer<Minecraft> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.TICK, event -> {
            if (event.phase == AbstractForgeClientEvents.TICK_PHASE_START) {
                consumer.accept(Minecraft.getInstance());
            }
        });
    }

    public static void willRenderTickEndFO(@ThisClass Class<?> clazz, Consumer<Minecraft> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.TICK, event -> {
            if (event.phase == AbstractForgeClientEvents.TICK_PHASE_END) {
                consumer.accept(Minecraft.getInstance());
            }
        });
    }

    public static void willPlayerEnterFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.PLAYER_LOGIN, event -> consumer.accept(event.getPlayer()));
    }

    public static void willPlayerLeaveFO(@ThisClass Class<?> clazz, Consumer<Player> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.PLAYER_LOGOUT, event -> consumer.accept(event.getPlayer()));
    }


    public static void willRegisterItemColorFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ItemColorRegistry> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.ITEM_COLOR_REGISTRY, consumer, event -> (provider, values) -> event.getItemColors().register(provider::getTintColor, values));
    }

    public static void willRegisterBlockColorFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.BlockColorRegistry> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.BLOCK_COLOR_REGISTRY, consumer, event -> (provider, values) -> event.getBlockColors().register(provider::getTintColor, values));
    }

    public static void willRegisterModelFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ModelRegistry> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.MODEL_REGISTRY, consumer, event -> event::register);
    }

    public static void willRegisterKeyMappingFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.KeyMappingRegistry> consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.KEY_REGISTRY, consumer, event -> event::register);
    }

    public static void willRegisterItemPropertyFO(@ThisClass Class<?> clazz, Consumer<ClientNativeProvider.ItemPropertyRegistry> consumer) {
        EnvironmentExecutor.didInit(EnvironmentType.COMMON, () -> () -> {
            consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
        });
    }

    public static void willRegisterItemTooltipFO(@ThisClass Class<?> clazz, ClientNativeProvider.GatherTooltip consumer) {
        AbstractForgeEventBus.observer(AbstractForgeClientEvents.ITEM_TOOLTIP_REGISTRY, event -> consumer.gather(event.getItemStack(), event.getToolTip(), event.getFlags()));
    }
}
