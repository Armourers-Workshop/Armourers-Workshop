package moe.plushie.armourers_workshop.compat.forge.event.client;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

@Available("[1.21, 1.26)")
public class AbstractForgeItemTooltipEventImpl {

    public static CGRect compute(RenderTooltipEvent.Pre event) {
        var font = event.getFont();
        var tooltips = event.getComponents();
        int mouseX = event.getX();
        int mouseY = event.getY();
        int screenWidth = event.getScreenWidth();
        int screenHeight = event.getScreenHeight();
        int i = 0;
        int j = tooltips.size() == 1 ? -2 : 0;
        for (var tooltip : tooltips) {
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
        return new CGRect(j2, k2, i, j);
    }

//                    var font = event.getFont();
//                    var tooltips = event.getComponents();
//                    int mouseX = event.getX();
//                    int mouseY = event.getY();
//                    int screenWidth = event.getScreenWidth();
//                    int screenHeight = event.getScreenHeight();
//                    int i = 0;
//                    int j = tooltips.size() == 1 ? -2 : 0;
//                    for (var tooltip : tooltips) {
//                        int k = tooltip.getWidth(font);
//                        if (k > i) {
//                            i = k;
//                        }
//                        j += tooltip.getHeight();
//                    }
//                    int j2 = mouseX + 12;
//                    int k2 = mouseY - 12;
//                    if (j2 + i > screenWidth) {
//                        j2 -= 28 + i;
//                    }
//                    if (k2 + j + 6 > screenHeight) {
//                        k2 = screenHeight - j - 6;
//                    }
//                    return new CGRect(j2, k2, i, j);
//
}
