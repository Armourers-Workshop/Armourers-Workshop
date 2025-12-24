package moe.plushie.armourers_workshop.compat.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGSize;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.AbstractGuiGraphicsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;

@Available("[1.20, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractMenuToast implements Toast {

    public abstract void render(CGPoint point, CGGraphicsContext context);

    public abstract double duration();

    public CGSize screenSize() {
        return new CGSize(width(), height());
    }

    @Override
    public final Visibility render(GuiGraphics guiGraphics, ToastComponent toast, long l) {
        AbstractGuiGraphicsRenderer.wrap(guiGraphics, width(), height(), 0, 0, l, context -> {
            this.render(CGPoint.ZERO, context);
        });
        double time = duration() * toast.getNotificationDisplayTimeMultiplier();
        if (l >= time) {
            return Visibility.HIDE;
        }
        return Toast.Visibility.SHOW;
    }

    public static void showToast(AbstractMenuToast toast) {
        Minecraft.getInstance().getToasts().addToast(toast);
    }
}
