package extensions.net.minecraft.network.chat.Component;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.TextFormatter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Optional;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.16, 1.19)")
public class TranslatableProvider {

    public static MutableComponent literal(@ThisClass Class<?> clazz, String text) {
        return new TextComponent(text);
    }

    public static MutableComponent translatable(@ThisClass Class<?> clazz, String text, Object ...args) {
        return new TranslatableComponent(text, args);
    }

    public static MutableComponent translatable(@ThisClass Class<?> clazz, TextFormatter formatter, String key, Object... args) {
        return new TranslatableComponentProxy(formatter, key, args);
    }

    public static class TranslatableComponentProxy extends TranslatableComponent {

        private final TextFormatter formatter;

        public TranslatableComponentProxy(TextFormatter formatter, String string, Object ... objects) {
            super(string, objects);
            this.formatter = formatter;
        }

        @Override
        public <T> Optional<T> visit(StyledContentConsumer<T> acceptor, Style initStyle) {
            String[] lastStyle = {""};
            return super.visit((style1, value) -> {
                String embeddedStyle = lastStyle[0];
                lastStyle[0] = embeddedStyle + formatter.getEmbeddedStyle(value);
                return acceptor.accept(style1, embeddedStyle + formatter.getFormattedString(value));
            }, initStyle);
        }
    }
}
