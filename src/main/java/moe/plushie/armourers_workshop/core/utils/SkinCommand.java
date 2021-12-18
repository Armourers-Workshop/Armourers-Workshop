package moe.plushie.armourers_workshop.core.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.type.SkinPartTypes;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SkinCommand {

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


    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("armourers")
                .then(DebugCommand.rote())
                .then(DebugCommand.register());
    }
//        super(null, "armourers");
//    addSubCommand(new CommandAdminPanel(this));
//    addSubCommand(new CommandClearCache(this));
//    addSubCommand(new CommandExportSkin(this));
//    addSubCommand(new CommandGiveSkin(this));
//    addSubCommand(new CommandExecute(this, "open_folder", new ICommandExecute() {
//
//        @Override
//        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//            PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.OPEN_MOD_FOLDER), player);
//        }
//    }));
//    addSubCommand(new CommandSetItemAsSkinnable(this));
//
//    addSubCommand(new CommandWardrobe(this));

    private static class DebugCommand {

        static class ListArgument implements ArgumentType<String> {

            private final Collection<String> list;

            ListArgument(Collection<String> list) {
                super();
                this.list = list;
            }

            public static String getString(CommandContext<CommandSource> contet, String name) {
                return contet.getArgument(name, String.class);
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
        static ArgumentBuilder<CommandSource, ?> rote() {
            return Commands.literal("test").then(Commands.literal("type").then(
                    Commands.argument("cro", IntegerArgumentType.integer()).executes(ctx -> {
                        int key = IntegerArgumentType.getInteger(ctx, "cro");
                        SkinItemRenderer.mp1 = key;
                        return 0;
                    })));        }

        static ArgumentBuilder<CommandSource, ?> register() {
            return Commands.literal("test").then(Commands.literal("part").then(
                    Commands.argument("part_name", new ListArgument(SkinPartTypes.skinPartTypes.keySet())).executes(ctx -> {
                        String key = ListArgument.getString(ctx, "part_name");
                        if (SkinConfig.disabledSkinParts.contains(key)) {
                            SkinConfig.disabledSkinParts.remove(key);
                        } else {
                            SkinConfig.disabledSkinParts.add(key);
                        }
                        return 0;
                    })));
        }
    }
}
