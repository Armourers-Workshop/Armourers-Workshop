package extensions.net.minecraft.network.chat.Component;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@Extension
public class ComponentExt {

    public static MutableComponent literal(@ThisClass Class self, String text) {
        return new TextComponent(text);
    }

    public static MutableComponent translatable(@ThisClass Class self, String text, Object ...args) {
        return new TranslatableComponent(text, args);
    }
}
