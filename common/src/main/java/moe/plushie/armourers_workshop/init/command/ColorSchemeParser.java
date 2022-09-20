package moe.plushie.armourers_workshop.init.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ColorSchemeParser {

    public static final DynamicCommandExceptionType ERROR_INVALID_DYE_FORMAT = new DynamicCommandExceptionType((obj) -> new TranslatableComponent("commands.armourers.invalidDyeFormat", obj));

    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;


    private static final HashMap<String, ISkinPaintType> DYE_TYPES = Util.make(() -> {
        HashMap<String, ISkinPaintType> map = new HashMap<>();
        for (int i = 0; i < 8; ++i) {
            ISkinPaintType paintType = SkinPaintTypes.byId(i + 1);
            String name = paintType.getRegistryName().getPath();
            map.put(name.replaceAll("_", ""), paintType);
        }
        return map;
    });

    private final StringReader reader;
    private final ColorParser colorParser;
    private final Map<ISkinPaintType, PaintColor> properties = Maps.newHashMap();

    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    public ColorSchemeParser(StringReader stringReader) {
        this.reader = stringReader;
        this.colorParser = new ColorParser(stringReader);
    }

    public ColorSchemeParser parse() throws CommandSyntaxException {
        suggestions = this::suggestOpenProperties;
        if (reader.canRead() && reader.peek() == '<') {
            reader.skip();
            suggestions = this::suggestPropertyNameOrEnd;
            reader.skipWhitespace();
            while (reader.canRead() && reader.peek() != '>') {
                reader.skipWhitespace();
                int i = reader.getCursor();
                String string = reader.readString();
                ISkinPaintType property = DYE_TYPES.get(string);
                if (property == null) {
                    reader.setCursor(i);
                    throw ERROR_INVALID_DYE_FORMAT.createWithContext(reader, string);
                }
                if (properties.containsKey(string)) {
                    reader.setCursor(i);
                    throw ERROR_INVALID_DYE_FORMAT.createWithContext(reader, string);
                }
                reader.skipWhitespace();
                suggestions = this::suggestEquals;
                if (!reader.canRead() || reader.peek() != '=') {
                    throw ERROR_INVALID_DYE_FORMAT.createWithContext(reader, string);
                }
                reader.skip();
                reader.skipWhitespace();
                suggestions = colorParser::fillSuggestions;
                properties.put(property, colorParser.parse().getPaintColor());
                suggestions = this::suggestNextPropertyOrEnd;
                if (!reader.canRead()) {
                    break;
                }
                if (reader.peek() == ' ') {
                    reader.skip();
                    suggestions = this::suggestPropertyName;
                    continue;
                }
                if (reader.peek() == '>') {
                    break;
                }
            }
            if (!reader.canRead()) {
                throw ERROR_INVALID_DYE_FORMAT.createWithContext(reader, reader.getString());
            }
            reader.skip();
            suggestions = SUGGEST_NOTHING;
        }
        return this;
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        return suggestions.apply(builder.createOffset(reader.getCursor()));
    }

    public Map<ISkinPaintType, PaintColor> getProperties() {
        return properties;
    }

    private CompletableFuture<Suggestions> suggestOpenProperties(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('<'));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('>'));
        }
        if (suggestionsBuilder.getRemaining().isEmpty() && properties.size() < DYE_TYPES.size()) {
            suggestionsBuilder.suggest(String.valueOf(' '));
        }
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('>'));
        }
        return suggestPropertyName(builder);
    }

    private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, ISkinPaintType> entry : DYE_TYPES.entrySet()) {
            String name = entry.getKey();
            if (properties.containsKey(entry.getValue()) || !name.startsWith(string)) {
                continue;
            }
            builder.suggest(name + "=");
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('='));
        }
        return suggestionsBuilder.buildFuture();
    }
}
