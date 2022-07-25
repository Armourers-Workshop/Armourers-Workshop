package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;
import moe.plushie.armourers_workshop.utils.ext.KeyModifierX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {

    public static IKeyBinding OPEN_WARDROBE_KEY = create("key.keyboard.p").bind(() -> ClientBuilderManager::sendOpenWardrobe).build("open-wardrobe");
    public static IKeyBinding UNDO_KEY = create("key.keyboard.z").bind(() -> ClientBuilderManager::sendUndo).build("undo");

    private static IKeyBindingBuilder<IKeyBinding> create(String key) {
        return ClientBuilderManager.getInstance().createKeyBindingBuilder(key).category("category").modifier(KeyModifierX.CONTROL);
    }

    public static void init() {
    }
}
