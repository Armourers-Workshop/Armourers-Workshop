package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotListView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ClientMenuHandler {

    public static boolean shouldRenderExtendScreen(Screen screen) {
        // The delegate screen should never render extend.
        if (screen instanceof SlotListView.DelegateScreen) {
            return false;
        }
        // we respect users settings.
        if (screen instanceof ContainerMenuScreen) {
            return ((ContainerMenuScreen<?, ?>) screen).shouldRenderExtendScreen();
        }
        return true;
    }
}
