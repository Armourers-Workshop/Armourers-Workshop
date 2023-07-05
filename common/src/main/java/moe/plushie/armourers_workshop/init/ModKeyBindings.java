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

    public static IKeyBinding OPEN_WARDROBE_KEY = cmd("key.keyboard.p").bind(() -> InputMotionHandler::sendOpenWardrobe).build("open-wardrobe");
    public static IKeyBinding UNDO_KEY = cmd("key.keyboard.z").bind(() -> InputMotionHandler::sendUndo).build("undo");

//    public static IKeyBinding CAMERA_S_KEY = normal("key.keyboard.space").build("undo");
//    public static IKeyBinding CAMERA_N_KEY = normal("key.keyboard.n").build("undo");

    private static IKeyBindingBuilder<IKeyBinding> cmd(String key) {
        return normal(key).modifier(OpenKeyModifier.CONTROL);
    }

    private static IKeyBindingBuilder<IKeyBinding> normal(String key) {
        return BuilderManager.getInstance().createKeyBindingBuilder(key).category("category");
    }

    public static void init() {
    }
}
