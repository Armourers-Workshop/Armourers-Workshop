package moe.plushie.armourers_workshop.core.utils.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ListArgument implements ArgumentType<String> {

    private final Collection<String> list;

    ListArgument(Collection<String> list) {
        super();
        this.list = list;
    }

    public static String getString(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(list, builder);
    }

    public static class Serializer implements IArgumentSerializer<ListArgument> {

        public void serializeToNetwork(ListArgument argument, PacketBuffer buffer) {
            ArrayList<String> lists = new ArrayList<>(argument.list);
            buffer.writeInt(lists.size());
            lists.forEach(buffer::writeUtf);
        }

        public ListArgument deserializeFromNetwork(PacketBuffer buffer) {
            int size = buffer.readInt();
            ArrayList<String> lists = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                lists.add(buffer.readUtf());
            }
            return new ListArgument(lists);
        }

        public void serializeToJson(ListArgument argument, JsonObject json) {
            JsonArray array = new JsonArray();
            argument.list.forEach(array::add);
            json.add("items", array);
        }
    }
}
