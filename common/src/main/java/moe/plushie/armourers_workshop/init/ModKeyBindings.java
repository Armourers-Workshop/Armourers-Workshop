package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.other.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.other.builder.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.client.InputMotionHandler;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.ext.KeyModifierX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {

    public static IKeyBinding OPEN_WARDROBE_KEY = create("key.keyboard.p").bind(() -> InputMotionHandler::sendOpenWardrobe).build("open-wardrobe");
    public static IKeyBinding UNDO_KEY = create("key.keyboard.z").bind(() -> InputMotionHandler::sendUndo).build("undo");

    private static IKeyBindingBuilder<IKeyBinding> create(String key) {
        return BuilderManager.getInstance().createKeyBindingBuilder(key).category("category").modifier(KeyModifierX.CONTROL);
    }

    public static void init() {
    }
}
