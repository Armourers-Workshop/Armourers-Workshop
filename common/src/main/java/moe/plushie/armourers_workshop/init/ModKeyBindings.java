package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IKeyMappingBuilder;
import moe.plushie.armourers_workshop.init.client.ClientMenuHandler;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;

@OnlyIn(Dist.CLIENT)
public class ModKeyBindings {

    private static final KeyBuilder MAIN = new KeyBuilder("main");

    public static IRegistryHolder<IKeyMapping> OPEN_WARDROBE = MAIN.cmd("key.keyboard.p").bind(() -> ClientMenuHandler::sendOpenWardrobePacket).build("open-wardrobe");
    public static IRegistryHolder<IKeyMapping> UNDO = MAIN.cmd("key.keyboard.z").bind(() -> ClientMenuHandler::sendUndoPacket).build("undo");

    public static void init() {
    }

    private static class KeyBuilder {

        private final IRegistryHolder<IKeyCategory> category;

        KeyBuilder(String category) {
            this.category = BuilderManager.getInstance().createKeyCategoryBuilder().build(category);
        }

        IKeyMappingBuilder<IKeyMapping> cmd(String key) {
            return normal(key).modifier(OpenKeyModifier.CONTROL);
        }

        IKeyMappingBuilder<IKeyMapping> normal(String key) {
            return BuilderManager.getInstance().createKeyMappingBuilder(key, category.get());
        }
    }
}
