package moe.plushie.armourers_workshop.init.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ColorParser {

    public static final DynamicCommandExceptionType ERROR_INVALID_COLOR_FORMAT = new DynamicCommandExceptionType((obj) -> Component.translatable("commands.armourers.invalidColorFormat", obj));

    private static final IntegerArgumentType INTEGER_ARGUMENT = IntegerArgumentType.integer(0, 255);

    private static final List<String> DEFAULT_COLORS = Lists.newArrayList("#ffffff", "0xffffff", "255,255,255");

    private static final HashMap<String, ISkinPaintType> PAINT_TYPES = Util.make(() -> {
        HashMap<String, ISkinPaintType> map = new HashMap<>();
        for (ISkinPaintType paintType : SkinPaintTypes.values()) {
            String name = paintType.getRegistryName().getPath();
            map.put(name.replaceAll("_", ""), paintType);
        }
        return map;
    });
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;

    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    private final StringReader reader;
    private PaintColor paintColor;

    public ColorParser(StringReader stringReader) {
        this.reader = stringReader;
    }

    public ColorParser parse() throws CommandSyntaxException {
        suggestions = this::suggestPropertyTypeAndColor;
        ISkinPaintType paintType = SkinPaintTypes.NORMAL;
        if (reader.canRead(2)) {
            paintType = readPaintType();
        }
        if (!reader.canRead()) {
            throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, reader.getString());
        }
        int color = readPaintColor();
        paintColor = PaintColor.of(color, paintType);
        suggestions = SUGGEST_NOTHING;
        return this;
    }

    public PaintColor getPaintColor() {
        return paintColor;
    }

    private ISkinPaintType readPaintType() throws CommandSyntaxException {
        // hair:#333 rainbow:12,12,12 #333333 hair:red
        int start = reader.getCursor();
        String name = reader.readString();
        if (!reader.canRead() || reader.peek() != ':') {
            reader.setCursor(start);
            return SkinPaintTypes.NORMAL;
        }
        reader.skip();
        ISkinPaintType paintType = PAINT_TYPES.get(name.toLowerCase(Locale.ROOT));
        if (paintType == null) {
            throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, name);
        }
        suggestions = this::suggestPropertyColor;
        return paintType;
    }

    private int readPaintColor() throws CommandSyntaxException {
        String colorString = getColorString();
        // #RRGGBB 0xRRGGBB R,G,B
        if (colorString.startsWith("#")) {
            // suggestions #000000
            suggestions = addHexSuggestions(colorString, "#ffffff");
            if (colorString.length() != 7) {
                throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
            }
            int value = parseRGB(colorString);
            reader.setCursor(reader.getCursor() + colorString.length());
            return value;
        }
        if (colorString.startsWith("0x")) {
            // suggestions 0x000000
            suggestions = addHexSuggestions(colorString, "0xffffff");
            if (colorString.length() != 8) {
                throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
            }
            int value = parseRGB(colorString);
            reader.setCursor(reader.getCursor() + colorString.length());
            return value;
        }
        if (colorString.contains(",")) {
            // suggestions 255,0,0
            suggestions = addDecimalSuggestions(colorString, "255,255,255");
            StringReader newReader = new StringReader(reader);
            int r = parseInt(newReader);
            if (!newReader.canRead() || newReader.peek() != ',') {
                throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
            }
            newReader.skip();
            int g = parseInt(newReader);
            if (!newReader.canRead() || newReader.peek() != ',') {
                throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
            }
            newReader.skip();
            int b = parseInt(newReader);
            reader.setCursor(newReader.getCursor());
            return (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
        }
        try {
            return reader.readInt();
        } catch (Exception e) {
            throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
        }
    }

    private int parseInt(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead()) {
            return INTEGER_ARGUMENT.parse(reader);
        }
        throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, "");
    }

    private int parseRGB(String colorString) throws CommandSyntaxException {
        try {
            long value = Long.decode(colorString);
            if ((value & 0xff000000) == 0) {
                value |= 0xff000000;
            }
            return (int) value;
        } catch (NumberFormatException e) {
            throw ERROR_INVALID_COLOR_FORMAT.createWithContext(reader, colorString);
        }
    }

    private String getColorString() {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedColorString(reader.peek())) {
            reader.skip();
        }
        int end = reader.getCursor();
        reader.setCursor(start);
        return reader.getString().substring(start, end);
    }

    private boolean isAllowedColorString(final char c) {
        return c >= '0' && c <= '9'
                || c >= 'A' && c <= 'Z'
                || c >= 'a' && c <= 'z'
                || c == '-' || c == '+'
                || c == ',' || c == '#';
    }

    private CompletableFuture<Suggestions> suggestPropertyTypeAndColor(SuggestionsBuilder builder) {
        String value = builder.getRemaining();
        for (String key : DEFAULT_COLORS) {
            if (value.isEmpty() || key.startsWith(value)) {
                builder.suggest(key);
            }
        }
        for (String key : PAINT_TYPES.keySet()) {
            if (value.isEmpty() || key.startsWith(value)) {
                builder.suggest(key + ":");
            }
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestPropertyColor(SuggestionsBuilder builder) {
        String value = builder.getRemaining();
        for (String key : DEFAULT_COLORS) {
            if (value.isEmpty() || key.startsWith(value)) {
                builder.suggest(key);
            }
        }
        return builder.buildFuture();
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> addDecimalSuggestions(String inputValue, String value) {
        String[] values = inputValue.split(String.valueOf(','));
        String[] targets = value.split(String.valueOf(','));
        StringBuilder targetBuilder = new StringBuilder();
        try {
            for (int i = 0; i < targets.length; ++i) {
                if (i != 0) {
                    targetBuilder.append(',');
                }
                String newValue = targets[i];
                if (i < values.length) {
                    newValue = values[i];
                }
                int intValue = Integer.decode(newValue);
                if (intValue < 0 || intValue > 255) {
                    return SUGGEST_NOTHING;
                }
                targetBuilder.append(newValue);
            }
        } catch (Exception e) {
            return SUGGEST_NOTHING;
        }
        String targetValue = targetBuilder.toString();
        return (builder) -> builder.suggest(targetValue).buildFuture();
    }

    private static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> addHexSuggestions(String inputValue, String value) {
        if (inputValue.length() >= value.length()) {
            return SUGGEST_NOTHING;
        }
        String newValue = inputValue + value.substring(inputValue.length());
        if (newValue.matches("(0x|#)[0-9A-Fa-f]{6}")) {
            return (builder) -> builder.suggest(newValue).buildFuture();
        }
        return SUGGEST_NOTHING;
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        return suggestions.apply(builder.createOffset(reader.getCursor()));
    }
}
