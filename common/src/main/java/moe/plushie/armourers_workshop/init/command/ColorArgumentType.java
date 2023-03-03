package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ColorArgumentType implements IArgumentType<PaintColor> {

    private static final Collection<String> EXAMPLES = Arrays.asList("[paintType:]#RRGGBB", "[paintType:]R,G,B");

    public ColorArgumentType() {
        super();
    }

    public static PaintColor getColor(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, PaintColor.class);
    }

    @Override
    public PaintColor parse(final StringReader reader) throws CommandSyntaxException {
        ColorParser parser = new ColorParser(reader).parse();
        return parser.getPaintColor();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        ColorParser parser = new ColorParser(stringReader);
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

    public static class Serializer implements IArgumentSerializer<ColorArgumentType> {

        @Override
        public void serializeToNetwork(ColorArgumentType argument, FriendlyByteBuf buffer) {
        }

        @Override
        public ColorArgumentType deserializeFromNetwork(FriendlyByteBuf buffer) {
            return new ColorArgumentType();
        }

        @Override
        public void serializeToJson(ColorArgumentType argument, JsonObject json) {
        }
    }
}
