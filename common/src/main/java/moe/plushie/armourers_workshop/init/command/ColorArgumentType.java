package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ColorArgumentType implements IArgumentType<SkinPaintColor> {

    private static final Collection<String> EXAMPLES = Collections.newList("[paintType:]#RRGGBB", "[paintType:]R,G,B");

    public static final IArgumentSerializer<ColorArgumentType> TYPE = new IArgumentSerializer<>() {

        @Override
        public Class<ColorArgumentType> type() {
            return ColorArgumentType.class;
        }

        @Override
        public void serializeToNetwork(ColorArgumentType argument, IFriendlyByteBuf buffer) {
        }

        @Override
        public ColorArgumentType deserializeFromNetwork(IFriendlyByteBuf buffer) {
            return new ColorArgumentType();
        }

        @Override
        public void serializeToJson(ColorArgumentType argument, JsonObject json) {
        }
    };

    public ColorArgumentType() {
        super();
    }

    public static SkinPaintColor getColor(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, SkinPaintColor.class);
    }

    @Override
    public SkinPaintColor parse(final StringReader reader) throws CommandSyntaxException {
        var parser = new ColorParser(reader).parse();
        return parser.paintColor();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        var parser = new ColorParser(stringReader);
        try {
            parser.parse();
        } catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return parser.fillSuggestions(builder);
    }

    @Override
    public Collection<String> examples() {
        return EXAMPLES;
    }
}
