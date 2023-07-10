package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.client.InputMotionHandler;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.ext.OpenKeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Referenced: InputConstants.getKey
@Environment(EnvType.CLIENT)
public class ModKeyBindings {

    private static final KeyBuilder MAIN = new KeyBuilder("category", null);
    private static final KeyBuilder GUI = new KeyBuilder("category.gui", "gui");

    public static IKeyBinding OPEN_WARDROBE_KEY = MAIN.cmd("key.keyboard.p").bind(() -> InputMotionHandler::sendOpenWardrobe).build("open-wardrobe");
    public static IKeyBinding UNDO_KEY = MAIN.cmd("key.keyboard.z").bind(() -> InputMotionHandler::sendUndo).build("undo");

    public static IKeyBinding GUI_TOGGLE_LEFT_KEY = GUI.cmd("key.keyboard.1").build("gui.toggleLeft");
    public static IKeyBinding GUI_TOGGLE_RIGHT_KEY = GUI.cmd("key.keyboard.2").build("gui.toggleRight");

    public static void init() {
    }

    private static class KeyBuilder {

        private final String category;
        private final String scope;

        KeyBuilder(String category, String scope) {
            this.category = category;
            this.scope = scope;
        }

        IKeyBindingBuilder<IKeyBinding> cmd(String key) {
            return normal(key).modifier(OpenKeyModifier.CONTROL);
        }

        IKeyBindingBuilder<IKeyBinding> normal(String key) {
            return BuilderManager.getInstance().createKeyBindingBuilder(key).category(category).scope(scope);
        }
    }
}
