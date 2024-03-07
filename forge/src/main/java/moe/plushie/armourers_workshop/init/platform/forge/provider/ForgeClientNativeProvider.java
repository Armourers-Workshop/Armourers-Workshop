package moe.plushie.armourers_workshop.init.platform.forge.provider;

import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface ForgeClientNativeProvider extends ClientNativeProvider {

    @Override
    default void willPlayerLogin(Consumer<Player> consumer) {
        Registry.willPlayerEnterFO(player -> {
            if (player != null && player.equals(EnvironmentManager.getPlayer())) {
                consumer.accept(player);
            }
        });
    }

    @Override
    default void willPlayerLogout(Consumer<Player> consumer) {
        Registry.willPlayerLeaveFO(player -> {
            if (player == null || player.equals(EnvironmentManager.getPlayer())) {
                consumer.accept(player);
            }
        });
    }

    @Override
    default void willTick(Consumer<Boolean> consumer) {
        Registry.willRenderTickStartFO(minecraft -> consumer.accept(minecraft.isPaused()));
    }

    @Override
    default void willInput(Consumer<Minecraft> consumer) {
        Registry.willRenderTickEndFO(consumer);
    }

    @Override
    default void willGatherTooltip(GatherTooltip consumer) {
        Registry.willRegisterItemTooltipFO(consumer);
    }

    @Override
    default void willRenderTooltip(RenderTooltip consumer) {
        Registry.willRenderTooltipFO(consumer);
    }

    @Override
    default void willRegisterItemColor(Consumer<ItemColorRegistry> consumer) {
        Registry.willRegisterItemColorFO(consumer);
    }

    @Override
    default void willRegisterBlockColor(Consumer<BlockColorRegistry> consumer) {
        Registry.willRegisterBlockColorFO(consumer);
    }

    @Override
    default void willRegisterModel(Consumer<ModelRegistry> consumer) {
        Registry.willRegisterModelFO(consumer);
    }

    @Override
    default void willRegisterKeyMapping(Consumer<KeyMappingRegistry> consumer) {
        Registry.willRegisterKeyMappingFO(consumer);
    }

    @Override
    default void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        Registry.willRegisterItemPropertyFO(consumer);
    }

    @Override
    default void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        Registry.willRegisterTextureFO(consumer);
    }
}
