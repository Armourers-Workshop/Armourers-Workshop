package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.network.chat.Component;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[20, )")
public class TranslatableExt {

    public static MutableComponent translatable(@ThisClass Class<?> clazz, TranslateUtils.TextFormatter formatter, String key, Object... args) {
        return MutableComponent.create(new TranslatableContents(key, null, args) {
            @Override
            public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> consumer, Style style) {
                var lastStyle = new AtomicReference<>("");
                return super.visit((style1, value) -> {
                    var embeddedStyle = lastStyle.get();
                    lastStyle.set(embeddedStyle + formatter.getEmbeddedStyle(value));
                    return consumer.accept(style1, embeddedStyle + formatter.getFormattedString(value));
                }, style);
            }
        });
    }
}
