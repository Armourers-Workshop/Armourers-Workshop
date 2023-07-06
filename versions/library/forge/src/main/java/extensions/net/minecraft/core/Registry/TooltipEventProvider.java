package extensions.net.minecraft.core.Registry;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;

import java.util.List;

@Available("[1.20, )")
@Extension
public class TooltipEventProvider {

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
            CGGraphicsContext context = AbstractGraphicsRenderer.of(font, event.getGraphics(), mouseX, mouseY, 0);
            consumer.render(event.getItemStack(), frame, screenWidth, screenHeight, context);
        });
    }
}
