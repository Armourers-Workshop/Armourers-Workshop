package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.Optional;

public class AbstractTranslatableComponent {

    public static MutableComponent of(String key, Object... args) {
        return MutableComponent.create(new TranslatableContents(key, args) {
            @Override
            public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> consumer, Style style) {
                String[] lastStyle = {""};
                return super.visit((style1, value) -> {
                    String embeddedStyle = lastStyle[0];
                    lastStyle[0] = embeddedStyle + TranslateUtils.getEmbeddedStyle(value);
                    return consumer.accept(style1, embeddedStyle + TranslateUtils.getFormattedString(value));
                }, style);
            }
        });
    }
}
