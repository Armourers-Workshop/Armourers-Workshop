package moe.plushie.armourers_workshop.common;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SkinCommands {

    /// :/armourers setSkin|giveSkin|clearSkin
    public static LiteralArgumentBuilder<CommandSource> commands() {
        return Commands.literal("armourers")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setSkin").then(players().then(slots().then(skins().executes(Executor::setSkin)))))
                .then(Commands.literal("giveSkin").then(players().then(skins().executes(Executor::giveSkin))))
                .then(Commands.literal("clearSkin").then(players().executes(Executor::clearSkin)))
                .then(Commands.literal("test")
                        .then(DebugCommand.disablePart())
                        .then(DebugCommand.clearCache())
                        .then(DebugCommand.rote()));
    }


//    public static void register(CommandDispatcher<CommandSource> source) {
//        p_198510_0_.register(Commands.literal("help").executes((p_198511_1_) -> {
//            Map<CommandNode<CommandSource>, String> map = p_198510_0_.getSmartUsage(p_198510_0_.getRoot(), p_198511_1_.getSource());
//
//            for(String s : map.values()) {
//                p_198511_1_.getSource().sendSuccess(new StringTextComponent("/" + s), false);
//            }
//
//            return map.size();
//        }).then(Commands.argument("command", StringArgumentType.greedyString()).executes((p_198512_1_) -> {
//            ParseResults<CommandSource> parseresults = p_198510_0_.parse(StringArgumentType.getString(p_198512_1_, "command"), p_198512_1_.getSource());
//            if (parseresults.getContext().getNodes().isEmpty()) {
//                throw ERROR_FAILED.create();
//            } else {
//                Map<CommandNode<CommandSource>, String> map = p_198510_0_.getSmartUsage(Iterables.getLast(parseresults.getContext().getNodes()).getNode(), p_198512_1_.getSource());
//
//                for(String s : map.values()) {
//                    p_198512_1_.getSource().sendSuccess(new StringTextComponent("/" + parseresults.getReader().getString() + " " + s), false);
//                }
//
//                return map.size();
//            }
//        })));
//    }

    static ArgumentBuilder<CommandSource, ?> players() {
        return Commands.argument("targets", EntityArgument.players());
    }

    static ArgumentBuilder<CommandSource, ?> slots() {
        return Commands.argument("slot", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSource, ?> skins() {
        return Commands.argument("skin", StringArgumentType.string());
    }

    static ArgumentBuilder<CommandSource, ?> dyes() {
        return Commands.argument("dye", StringArgumentType.string());
    }

    private static class Executor {
        static int setSkin(CommandContext<CommandSource> source) throws CommandSyntaxException {
            int slot = IntegerArgumentType.getInteger(source, "slot");
            String skin = StringArgumentType.getString(source, "skin");
            Collection<ServerPlayerEntity> players = EntityArgument.getPlayers(source, "targets");
            return 0;
        }

        static int clearSkin(CommandContext<CommandSource> source) {
            return 0;
        }

        static int giveSkin(CommandContext<CommandSource> source) {
            return 0;
        }
    }

    private static class DebugCommand {

        static ArgumentBuilder<CommandSource, ?> clearCache() {
            return Commands.literal("clearCache").executes(ctx -> {
                SkinCache.INSTANCE.clear();
                return 0;
            });
        }

        static ArgumentBuilder<CommandSource, ?> rote() {
            return Commands.literal("type").then(
                    Commands.argument("cro", IntegerArgumentType.integer()).executes(ctx -> {
                        int key = IntegerArgumentType.getInteger(ctx, "cro");
                        SkinItemRenderer.mp1 = key;
                        return 0;
                    }));
        }

        static ArgumentBuilder<CommandSource, ?> disablePart() {
            return Commands.literal("disablePart").then(
                    Commands.argument("part_name", new ListArgument(SkinPartTypes.registeredNames())).executes(ctx -> {
                        String key = ListArgument.getString(ctx, "part_name");
                        if (SkinConfig.disabledSkinParts.contains(key)) {
                            SkinConfig.disabledSkinParts.remove(key);
                        } else {
                            SkinConfig.disabledSkinParts.add(key);
                        }
                        return 0;
                    }));
        }

        static class ListArgument implements ArgumentType<String> {

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
        }
    }
}
