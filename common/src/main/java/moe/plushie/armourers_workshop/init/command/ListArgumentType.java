package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ListArgumentType implements IArgumentType<String> {

    public static final IArgumentSerializer<ListArgumentType> TYPE = new IArgumentSerializer<ListArgumentType>() {

        @Override
        public Class<ListArgumentType> type() {
            return ListArgumentType.class;
        }

        @Override
        public void serializeToNetwork(ListArgumentType argument, IFriendlyByteBuf buffer) {
            var lists = new ArrayList<>(argument.list);
            buffer.writeInt(lists.size());
            lists.forEach(buffer::writeUtf);
        }

        @Override
        public ListArgumentType deserializeFromNetwork(IFriendlyByteBuf buffer) {
            var size = buffer.readInt();
            var lists = new ArrayList<String>(size);
            for (int i = 0; i < size; ++i) {
                lists.add(buffer.readUtf());
            }
            return new ListArgumentType(lists);
        }

        @Override
        public void serializeToJson(ListArgumentType argument, JsonObject json) {
            var array = new JsonArray();
            argument.list.forEach(array::add);
            json.add("items", array);
        }
    };

    private final Collection<String> list;

    public ListArgumentType(Collection<String> list) {
        super();
        this.list = list;
    }

    public static ListArgumentType list(Iterable<String> values) {
        return new ListArgumentType(Collections.newList(values));
    }

    public static String getString(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        var text = reader.getRemaining();
        for (var value : list) {
            if (text.startsWith(value)) {
                reader.setCursor(reader.getCursor() + value.length());
                return value;
            }
        }
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(list, builder);
    }
}
