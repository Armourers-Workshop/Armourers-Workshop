package extensions.net.minecraft.network.chat.Component;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.TextFormatter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Optional;

@Extension
@Available("[1.20, )")
public class TranslatableProvider {

    public static MutableComponent translatable(@ThisClass Class<?> clazz, TextFormatter formatter, String key, Object... args) {
        return MutableComponent.create(new TranslatableContents(key, null, args) {
            @Override
            public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> consumer, Style style) {
                String[] lastStyle = {""};
                return super.visit((style1, value) -> {
                    String embeddedStyle = lastStyle[0];
                    lastStyle[0] = embeddedStyle + formatter.getEmbeddedStyle(value);
                    return consumer.accept(style1, embeddedStyle + formatter.getFormattedString(value));
                }, style);
            }
        });
    }
}
