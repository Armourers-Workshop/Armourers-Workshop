package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.math.Size2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractMenuToast implements Toast {

    public abstract void render(CGGraphicsContext context);

    public abstract double getDuration();

    public Size2i getScreenSize() {
        return new Size2i(width(), height());
    }

    @Override
    public final Visibility render(GuiGraphics guiGraphics, ToastComponent toast, long l) {
        Font font = toast.getMinecraft().font;
        render(AbstractGraphicsRenderer.of(font, guiGraphics, 0, 0, l));
        double time = getDuration() * toast.getNotificationDisplayTimeMultiplier();
        if (l >= time) {
            return Visibility.HIDE;
        }
        return Toast.Visibility.SHOW;
    }
}
