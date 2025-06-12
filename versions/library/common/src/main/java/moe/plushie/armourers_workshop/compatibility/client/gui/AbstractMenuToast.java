package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenSize2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractMenuToast implements Toast {

    public abstract void render(CGGraphicsContext context);

    public abstract double duration();

    public OpenSize2i screenSize() {
        return new OpenSize2i(width(), height());
    }

    @Override
    public final Visibility render(GuiGraphics guiGraphics, ToastComponent toast, long l) {
        render(AbstractGraphicsRenderer.of(guiGraphics, 0, 0, l));
        double time = duration() * toast.getNotificationDisplayTimeMultiplier();
        if (l >= time) {
            return Visibility.HIDE;
        }
        return Toast.Visibility.SHOW;
    }
}
