package moe.plushie.armourers_workshop.compat.client.gui.event;

import com.apple.library.impl.event.InputKeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Minecraft;

@Available("[1.16, 1.22)")
public class AbstractKeyEvent implements InputKeyEvent {

    public static final boolean ON_OSX = Minecraft.ON_OSX;

    public final int code;
    public final int modifiers;
    public final int scancode;

    public AbstractKeyEvent(int code, int modifiers, int scancode) {
        this.code = code;
        this.modifiers = modifiers;
        this.scancode = scancode;
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    public static InputConstants.Key create(int code, int modifier) {
        return InputConstants.getKey(code, modifier) ;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public int modifiers() {
        return modifiers;
    }
}
