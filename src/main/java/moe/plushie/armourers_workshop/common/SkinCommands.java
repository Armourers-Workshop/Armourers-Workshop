package moe.plushie.armourers_workshop.common;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.Palette;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.ReflectArgumentBuilder;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


// /give @p armourers_workshop:dye-bottle{color:0x3ff0000}
// /give @p armourers_workshop:skin 1 0

public class SkinCommands {

    /// :/armourers setSkin|giveSkin|clearSkin
    public static LiteralArgumentBuilder<CommandSource> commands() {
        return Commands.literal("armourers")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("setSkin").then(players().then(slots().then(skins().executes(Executor::setSkin))).then(skins().executes(Executor::setSkin))))
                .then(Commands.literal("giveSkin").then(players().then(skins().executes(Executor::giveSkin))))
                .then(Commands.literal("clearSkin").then(players().then(slotNames().then(slots().executes(Executor::clearSkin))).executes(Executor::clearSkin)))
                .then(ReflectArgumentBuilder.literal("config", SkinConfig.class))
                .then(Commands.literal("test")
                        .then(DebugCommand.disablePart())
                        .then(DebugCommand.clearCache())
                        .then(DebugCommand.rote()));
    }

    static ArgumentBuilder<CommandSource, ?> players() {
        return Commands.argument("targets", EntityArgument.players());
    }

    static ArgumentBuilder<CommandSource, ?> slots() {
        return Commands.argument("slot", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSource, ?> slotNames() {
        return Commands.argument("slot_name", new DebugCommand.ListArgument(Arrays.stream(SkinSlotType.values()).map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSource, ?> skins() {
        return Commands.argument("skin", StringArgumentType.string());
    }

    static ArgumentBuilder<CommandSource, ?> dyes() {
        return Commands.argument("dye", StringArgumentType.string());
    }

    private static class Executor {

        static boolean containsNode(CommandContext<CommandSource> context, String name) {
            for (ParsedCommandNode<?> node : context.getNodes()) {
                if (name.equals(node.getNode().getName())) {
                    return true;
                }
            }
            return false;
        }

        static int setSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            String identifier = StringArgumentType.getString(context, "skin");
            Skin skin = SkinCore.loader.loadSkin(new SkinDescriptor(identifier));
            if (skin == null) {
                return 0;
            }
            SkinDescriptor descriptor = new SkinDescriptor(identifier, skin.getType(), Palette.EMPTY);
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                SkinSlotType slotType = SkinSlotType.of(skin.getType());
                if (slotType == null || wardrobe == null) {
                    continue;
                }
                int slot = wardrobe.getFreeSlot(slotType);
                if (containsNode(context, "slot")) {
                    slot = IntegerArgumentType.getInteger(context, "slot");
                }
                wardrobe.setItem(slotType, slot - 1, descriptor.asItemStack());
            }
            return 0;
        }

        static int clearSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe == null) {
                    continue;
                }
                if (!containsNode(context, "slot")) {
                    wardrobe.clear();
                    continue;
                }
                int slot = IntegerArgumentType.getInteger(context, "slot");
                SkinSlotType slotType = SkinSlotType.of(DebugCommand.ListArgument.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                wardrobe.setItem(slotType, slot - 1, ItemStack.EMPTY);
            }
            return 0;
        }

        static int giveSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            String identifier = StringArgumentType.getString(context, "skin");
            Skin skin = SkinCore.loader.loadSkin(new SkinDescriptor(identifier));
            if (skin == null) {
                return 0;
            }
            SkinDescriptor descriptor = new SkinDescriptor(identifier, skin.getType(), Palette.EMPTY);
            ItemStack itemStack = descriptor.asItemStack();
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                boolean flag = player.inventory.add(itemStack);
                if (flag && itemStack.isEmpty()) {
                    itemStack.setCount(1);
                    ItemEntity itemEntity1 = player.drop(itemStack, false);
                    if (itemEntity1 != null) {
                        itemEntity1.makeFakeItem();
                    }
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itemEntity = player.drop(itemStack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                        itemEntity.setOwner(player.getUUID());
                    }
                }
                context.getSource().sendSuccess(new TranslationTextComponent("commands.give.success.single", 1, itemStack.getDisplayName(), player.getDisplayName()), true);
            }
            return 1;
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

