package moe.plushie.armourers_workshop.compatibility.forge;

import com.apple.library.coregraphics.CGRect;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeModelBakery;

import java.util.List;
import java.util.function.Consumer;

public class AbstractForgeClientNativeImpl extends AbstractClientNativeImpl implements AbstractForgeClientNativeProvider {

    @Override
    public void willRegisterItemColor(Consumer<ItemColorRegistry> consumer) {
        NotificationCenterImpl.observer(ColorHandlerEvent.Item.class, consumer, event -> (provider, values) -> event.getItemColors().register(provider::getTintColor, values));
    }

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        EnvironmentExecutor.didInit(EnvironmentType.COMMON, () -> () -> {
            consumer.accept((registryName, item, property) -> ItemProperties.register(item, registryName, property::getValue));
        });
    }

    @Override
    public void willRegisterBlockColor(Consumer<BlockColorRegistry> consumer) {
        NotificationCenterImpl.observer(ColorHandlerEvent.Block.class, consumer, event -> (provider, values) -> event.getBlockColors().register(provider::getTintColor, values));
    }

    @Override
    public void willRegisterTexture(Consumer<TextureRegistry> consumer) {
        NotificationCenterImpl.observer(TextureStitchEvent.Pre.class, consumer, event -> registryName -> {
            if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
                event.addSprite(registryName);
            }
        });
    }

    @Override
    public void willRegisterModel(Consumer<ModelRegistry> consumer) {
        NotificationCenterImpl.observer(ModelRegistryEvent.class, consumer, event -> ForgeModelBakery::addSpecialModel);
    }

    @Override
    public void willRegisterKeyMapping(Consumer<KeyMappingRegistry> consumer) {
        consumer.accept(ClientRegistry::registerKeyBinding);
    }

    @Override
    public void willRenderTooltip(RenderTooltip consumer) {
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
            PoseStack poseStack = event.getPoseStack();
            consumer.render(event.getItemStack(), frame, mouseX, mouseY, screenWidth, screenHeight, poseStack);
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
        NotificationCenterImpl.observer(DrawSelectionEvent.HighlightBlock.class, event -> {
            PoseStack poseStack = event.getPoseStack();
            renderer.render(event.getTarget(), event.getCamera(), poseStack, event.getMultiBufferSource());
        });
    }

    @Override
    public void willRenderLivingEntity(RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Pre.class, event -> {
            PoseStack poseStack = event.getPoseStack();
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), poseStack, event.getMultiBufferSource(), event.getRenderer());
        });
    }

    @Override
    public void didRenderLivingEntity(RenderLivingEntity renderer) {
        NotificationCenterImpl.observer(RenderLivingEvent.Post.class,  event -> {
            PoseStack poseStack = event.getPoseStack();
            renderer.render(event.getEntity(), event.getPartialTick(), event.getPackedLight(), poseStack, event.getMultiBufferSource(), event.getRenderer());
        });
    }
}
