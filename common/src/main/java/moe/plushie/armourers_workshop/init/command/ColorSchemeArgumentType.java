package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ColorSchemeArgumentType implements ArgumentType<SkinPaintScheme> {

    private static final Collection<String> EXAMPLES = Collections.newList("<dyeIndex=[paintType:]#RRGGBB>", "<dyeIndex=[paintType:]R,G,B>");

    public ColorSchemeArgumentType() {
        super();
    }

    public static SkinPaintScheme getColorScheme(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, SkinPaintScheme.class);
    }

    @Override
    public SkinPaintScheme parse(final StringReader reader) throws CommandSyntaxException {
        var parser = new ColorSchemeParser(reader).parse();
        var colorScheme = new SkinPaintScheme();
        for (var entry : parser.properties().entrySet()) {
            colorScheme.setColor(entry.getKey(), entry.getValue());
        }
        return colorScheme;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        var parser = new ColorSchemeParser(stringReader);
        try {
            parser.parse();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return parser.fillSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class Serializer implements IArgumentSerializer<ColorSchemeArgumentType> {

        @Override
        public void serializeToNetwork(ColorSchemeArgumentType argument, IFriendlyByteBuf buffer) {
        }

        @Override
        public ColorSchemeArgumentType deserializeFromNetwork(IFriendlyByteBuf buffer) {
            return new ColorSchemeArgumentType();
        }

        @Override
        public void serializeToJson(ColorSchemeArgumentType argument, JsonObject json) {
        }
    }
}
