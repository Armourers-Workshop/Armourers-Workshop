package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

// /path/name.armour
public class FileArgument implements ArgumentType<String> {

    public static final SimpleCommandExceptionType ERROR_START = new SimpleCommandExceptionType(new TextComponent("File must start with '/'"));
    public static final SimpleCommandExceptionType ERROR_NOT_FOUND = new SimpleCommandExceptionType(new TextComponent("Not found any file"));
    private static final Collection<String> EXAMPLES = Arrays.asList("/", "/file.armour", "\"<scheme>:<identifier>\"");
    private final File rootFile;
    private final ArrayList<String> fileList;
    private final StringArgumentType stringType = StringArgumentType.string();

    private String filteredName;
    private ArrayList<String> filteredFileList;

    public FileArgument(File rootPath) {
        super();
        this.rootFile = rootPath;
        this.fileList = null;
        ModLog.debug("setup root path: " + rootPath);
    }

    FileArgument(ArrayList<String> fileList) {
        super();
        this.rootFile = null;
        this.fileList = fileList;
    }


    public static String getString(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        String inputPath = reader.getRemaining();
        if (inputPath.startsWith("\"")) {
            return stringType.parse(reader);
        }
        if (inputPath.startsWith("/")) { // file
            ArrayList<String> fileList = getFileList(inputPath);
            if (fileList.isEmpty()) {
                throw ERROR_NOT_FOUND.createWithContext(reader);
            }
            if (fileList.size() == 1 && inputPath.startsWith(fileList.get(0))) {
                inputPath = fileList.get(0);
            }
            reader.setCursor(reader.getCursor() + inputPath.length());
            return inputPath;
        }
        throw ERROR_START.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String inputPath = builder.getRemaining();
        if (inputPath.startsWith("/")) {
            ArrayList<String> fileList = getFileList(inputPath);
            if (!fileList.isEmpty()) {
                return SharedSuggestionProvider.suggest(fileList, builder);
            }
        }
        return SharedSuggestionProvider.suggest(Arrays.asList("/", "\""), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private ArrayList<String> getFileList(String name) {
        if (Objects.equals(name, filteredName)) {
            return filteredFileList;
        }
        if (rootFile != null) {
            filteredName = name;
            filteredFileList = getFileList(rootFile, name);
        } else {
            filteredName = name;
            filteredFileList = getFileList(fileList, name);
        }
        return filteredFileList;
    }

    public ArrayList<String> getFileList(File root, String name) {
        ArrayList<String> results = new ArrayList<>();
        File[] files = root.listFiles();
        if (files == null) {
            ModLog.error("load file error at {}", root);
            return results;
        }
        for (File file : files) {
            String rv = SkinFileUtils.normalize(file.getAbsolutePath().replace(rootFile.getAbsolutePath(), ""), true);
            if (file.isDirectory()) {
                if (name.startsWith(rv)) {
                    results.addAll(getFileList(file, name));
                } else if (rv.startsWith(name)) {
                    results.addAll(getFileList(file, name));
                }
                continue;
            }
            if (rv.toLowerCase().endsWith(Constants.EXT)) {
                if (rv.startsWith(name)) {
                    results.add(rv);
                } else if (name.startsWith(rv)) {
                    results.add(rv);
                }
            }
        }
        return results;
    }

    public ArrayList<String> getFileList(ArrayList<String> fileList, String name) {
        ArrayList<String> results = new ArrayList<>();
        for (String file : fileList) {
            if (file.startsWith(name)) {
                results.add(file);
            }
        }
        return results;
    }

    public static class Serializer implements ArgumentSerializer<FileArgument> {

        public void serializeToNetwork(FileArgument argument, FriendlyByteBuf buffer) {
            ArrayList<String> lists = argument.getFileList("/");
            buffer.writeInt(lists.size());
            lists.forEach(buffer::writeUtf);
        }

        public FileArgument deserializeFromNetwork(FriendlyByteBuf buffer) {
            int size = buffer.readInt();
            ArrayList<String> lists = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                lists.add(buffer.readUtf(Short.MAX_VALUE));
            }
            return new FileArgument(lists);
        }

        public void serializeToJson(FileArgument argument, JsonObject json) {
            JsonArray array = new JsonArray();
            ArrayList<String> lists = argument.getFileList("/");
            lists.forEach(array::add);
            json.add("files", array);
        }
    }
}
