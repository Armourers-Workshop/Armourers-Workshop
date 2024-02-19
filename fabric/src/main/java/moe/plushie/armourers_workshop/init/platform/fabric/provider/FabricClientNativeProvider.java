package moe.plushie.armourers_workshop.init.platform.fabric.provider;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderTooltipEvents;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface FabricClientNativeProvider extends ClientNativeProvider {

    @Override
    default void willRegisterItemColor(Consumer<ItemColorRegistry> consumer) {
        consumer.accept((provider, element) -> ColorProviderRegistry.ITEM.register(provider::getTintColor, element));
    }

    @Override
    default void willRegisterBlockColor(Consumer<BlockColorRegistry> consumer) {
        consumer.accept((provider, element) -> ColorProviderRegistry.BLOCK.register(provider::getTintColor, element));
    }

    @Override
    default void willRegisterModel(Consumer<ModelRegistry> consumer) {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((resourceManager, registry) -> consumer.accept(registry::accept));
    }

    @Override
    default void willRegisterKeyMapping(Consumer<KeyMappingRegistry> consumer) {
        consumer.accept(KeyBindingHelper::registerKeyBinding);
    }

    @Override
    default void willPlayerLogin(Consumer<Player> consumer) {
        ClientPlayConnectionEvents.INIT.register(((handler, client) -> consumer.accept(client.player)));
    }

    @Override
    default void willPlayerLogout(Consumer<Player> consumer) {
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> consumer.accept(client.player)));
    }

    @Override
    default void willTick(Consumer<Boolean> consumer) {
        ClientTickEvents.START_CLIENT_TICK.register(client -> consumer.accept(client.isPaused()));
    }

    @Override
    default void willInput(Consumer<Minecraft> consumer) {
        ClientTickEvents.END_CLIENT_TICK.register(consumer::accept);
    }

    @Override
    default void willGatherTooltip(GatherTooltip consumer) {
        ItemTooltipCallback.EVENT.register(((stack, context, lines) -> consumer.gather(stack, lines, context)));
    }

    @Override
    default void willRenderTooltip(RenderTooltip consumer) {
        RenderTooltipEvents.BEFORE.register((itemStack, x, y, width, height, screenWidth, screenHeight, context) -> {
            if (itemStack.isEmpty()) {
                return;
            }
            CGRect frame = new CGRect(x, y, width, height);
            consumer.render(itemStack, frame, screenWidth, screenHeight, context);
        });
    }

    @Override
    default void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        Registry.willRegisterItemPropertyFA(consumer);
    }

    @Override
    default void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        Registry.willRegisterTextureFA(consumer);
    }
}
