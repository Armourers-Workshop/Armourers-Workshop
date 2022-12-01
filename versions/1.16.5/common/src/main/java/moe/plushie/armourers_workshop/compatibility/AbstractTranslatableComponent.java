package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Optional;

public class AbstractTranslatableComponent extends TranslatableComponent {

    public AbstractTranslatableComponent(String string, Object... objects) {
        super(string, objects);
    }

    public static AbstractTranslatableComponent of(String key, Object... args) {
        return new AbstractTranslatableComponent(key, args);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> acceptor, Style initStyle) {
        String[] lastStyle = {""};
        return super.visitSelf((style1, value) -> {
            String embeddedStyle = lastStyle[0];
            lastStyle[0] = embeddedStyle + TranslateUtils.getEmbeddedStyle(value);
            return acceptor.accept(style1, embeddedStyle + TranslateUtils.getFormattedString(value));
        }, initStyle);
    }
}
