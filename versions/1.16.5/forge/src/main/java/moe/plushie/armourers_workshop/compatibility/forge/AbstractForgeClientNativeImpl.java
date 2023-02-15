package moe.plushie.armourers_workshop.compatibility.forge;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.compatibility.ext.AbstractClientNativeExt_V1618;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.function.Consumer;

public class AbstractForgeClientNativeImpl extends AbstractClientNativeImpl implements AbstractForgeClientNativeProvider, AbstractClientNativeExt_V1618 {

    private CGRect screenLayout = CGRect.ZERO;

    @Override
    public void willRegisterItemColor(Consumer<ItemColorRegistry> consumer) {
        NotificationCenterImpl.observer(ColorHandlerEvent.Item.class, consumer, event -> (provider, values) -> event.getItemColors().register(provider::getTintColor, values));
    }

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        NotificationCenterImpl.observer(ModelRegistryEvent.class, event -> consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, (itemStack, level, entity) -> property.getValue(itemStack, level, entity, 0))));
    }

    @Override
    public void willRegisterBlockColor(Consumer<BlockColorRegistry> consumer) {
        NotificationCenterImpl.observer(ColorHandlerEvent.Block.class, consumer, event -> (provider, values) -> event.getBlockColors().register(provider::getTintColor, values));
    }

    @Override
    public void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        NotificationCenterImpl.observer(TextureStitchEvent.Pre.class, consumer, event -> registryName -> {
            if (event.getMap().location().equals(InventoryMenu.BLOCK_ATLAS)) {
                event.addSprite(registryName);
            }
        });
    }

    @Override
    public void willRegisterModel(Consumer<ModelRegistry> consumer) {
        NotificationCenterImpl.observer(ModelRegistryEvent.class, event -> consumer.accept(ModelLoader::addSpecialModel));
    }

    @Override
    public void willRegisterKeyMapping(Consumer<KeyMappingRegistry> consumer) {
        consumer.accept(ClientRegistry::registerKeyBinding);
    }

    @Override
    public void willRenderTooltip(RenderTooltip consumer) {
        NotificationCenterImpl.observer(RenderTooltipEvent.Pre.class, event -> {
            screenLayout = new CGRect(event.getX(), event.getY(), event.getScreenWidth(), event.getScreenHeight());
        });
        NotificationCenterImpl.observer(RenderTooltipEvent.PostText.class, event -> {
            int mouseX = screenLayout.getX();
            int mouseY = screenLayout.getY();
            int screenWidth = screenLayout.getWidth();
            int screenHeight = screenLayout.getHeight();
            CGRect frame = new CGRect(event.getX(), event.getY(), event.getWidth(), event.getHeight());
            IPoseStack poseStack = MatrixUtils.of(event.getMatrixStack());
            consumer.render(event.getStack(), frame, mouseX, mouseY, screenWidth, screenHeight, poseStack);
        });
    }

    @Override
    public void willPlayerEnter(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(ClientPlayerNetworkEvent.LoggedInEvent.class, consumer, ClientPlayerNetworkEvent::getPlayer);
    }

    @Override
    public void willPlayerLeave(Consumer<Player> consumer) {
        NotificationCenterImpl.observer(ClientPlayerNetworkEvent.LoggedOutEvent.class, consumer, ClientPlayerNetworkEvent::getPlayer);
    }

    @Override
    public void willRenderBlockHighlight(RenderBlockHighlight renderer) {
        NotificationCenterImpl.observer(DrawHighlightEvent.HighlightBlock.class, event -> {
            IPoseStack poseStack = MatrixUtils.of(event.getMatrix());
            renderer.render(event.getTarget(), event.getInfo(), poseStack, event.getBuffers());
        });
    }

    @Override
    public void willRenderLivingEntity(RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Pre.class, event -> {
            IPoseStack poseStack = MatrixUtils.of(event.getMatrixStack());
            renderer.render(event.getEntity(), event.getPartialRenderTick(), event.getLight(), poseStack, event.getBuffers(), event.getRenderer());
        });
    }

    @Override
    public void didRenderLivingEntity(RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Post.class, event -> {
            IPoseStack poseStack = MatrixUtils.of(event.getMatrixStack());
            renderer.render(event.getEntity(), event.getPartialRenderTick(), event.getLight(), poseStack, event.getBuffers(), event.getRenderer());
        });
    }
}
