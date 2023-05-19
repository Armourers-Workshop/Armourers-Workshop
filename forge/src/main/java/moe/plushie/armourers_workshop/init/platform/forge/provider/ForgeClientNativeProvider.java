package moe.plushie.armourers_workshop.init.platform.forge.provider;

import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.function.Consumer;

public interface ForgeClientNativeProvider extends ClientNativeProvider {

    @Override
    default void willPlayerLogin(Consumer<Player> consumer) {
        Registry.willPlayerEnterFO(player -> {
            if (player != null && player.equals(Minecraft.getInstance().player)) {
                consumer.accept(player);
            }
        });
    }

    @Override
    default void willPlayerLogout(Consumer<Player> consumer) {
        Registry.willPlayerLeaveFO(player -> {
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
