package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.client.ClientMenuHandler;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;

@OnlyIn(Dist.CLIENT)
public class ModKeyBindings {

    private static final KeyBuilder MAIN = new KeyBuilder("category", null);

    public static IKeyBinding OPEN_WARDROBE_KEY = MAIN.cmd("key.keyboard.p").bind(() -> ClientMenuHandler::sendOpenWardrobePacket).build("open-wardrobe");
    public static IKeyBinding UNDO_KEY = MAIN.cmd("key.keyboard.z").bind(() -> ClientMenuHandler::sendUndoPacket).build("undo");

    public static void init() {
    }

    private static class KeyBuilder {

        private final String category;

        KeyBuilder(String category, String scope) {
            this.category = category;
        }

        IKeyBindingBuilder<IKeyBinding> cmd(String key) {
            return normal(key).modifier(OpenKeyModifier.CONTROL);
        }

        IKeyBindingBuilder<IKeyBinding> normal(String key) {
            return BuilderManager.getInstance().createKeyBindingBuilder(key).category(category);
        }
    }
}
