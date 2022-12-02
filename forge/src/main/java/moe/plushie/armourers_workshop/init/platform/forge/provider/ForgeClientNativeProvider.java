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

public interface ForgeClientNativeProvider extends ClientNativeProvider {

    void willPlayerEnter(Consumer<Player> consumer);

    void willPlayerLeave(Consumer<Player> consumer);

    void willRenderBlockHighlight(RenderBlockHighlight renderer);

    void willRenderLivingEntity(RenderLivingEntity renderer);

    void didRenderLivingEntity(RenderLivingEntity renderer);

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

    interface RenderBlockHighlight {
        void render(BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers);
    }
}
