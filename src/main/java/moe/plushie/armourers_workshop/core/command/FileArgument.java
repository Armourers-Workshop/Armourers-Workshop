package moe.plushie.armourers_workshop.core.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

// /path/name.armour
public class FileArgument implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("/", "/name.armour", "/path/name.armour");

    public static final SimpleCommandExceptionType ERROR_START = new SimpleCommandExceptionType(new StringTextComponent("File must start with '/'"));
    public static final SimpleCommandExceptionType ERROR_NOT_FOUND = new SimpleCommandExceptionType(new StringTextComponent("Not found any file"));

    private final String rootPath;
    private final String ext;

    private String filteredName;
    private ArrayList<String> filteredFileList;

    FileArgument(String rootPath) {
        super();
        this.rootPath = rootPath;
        this.ext = ".armour";
    }

    public static String getString(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        String inputPath = reader.getRemaining();
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
        } else {
            throw ERROR_START.createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String inputPath = builder.getRemaining();
        if (inputPath.startsWith("/")) {
            ArrayList<String> fileList = getFileList(inputPath);
            if (!fileList.isEmpty()) {
                return ISuggestionProvider.suggest(fileList, builder);
            }
        } else {
            return ISuggestionProvider.suggest(Collections.singletonList("/"), builder);
        }
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private ArrayList<String> getFileList(String name) {
        if (Objects.equals(name, filteredName)) {
            return filteredFileList;
        }
        filteredName = name;
        filteredFileList = getFileList(new File(rootPath), name);
        return filteredFileList;
    }

    public ArrayList<String> getFileList(File root, String name) {
        ArrayList<String> results = new ArrayList<>();
        File[] files = root.listFiles();
        if (files == null) {
            return results;
        }
        for (File file : files) {
            String rv = file.toString().substring(rootPath.length());
            if (file.isDirectory()) {
                if (name.startsWith(rv)) {
                    results.addAll(getFileList(file, name));
                } else if (rv.startsWith(name)) {
                    results.addAll(getFileList(file, name));
                }
                continue;
            }
            if (rv.toLowerCase().endsWith(ext)) {
                if (rv.startsWith(name)) {
                    results.add(rv);
                } else if (name.startsWith(rv)) {
                    results.add(rv);
                }
            }
        }
        return results;
    }
}
