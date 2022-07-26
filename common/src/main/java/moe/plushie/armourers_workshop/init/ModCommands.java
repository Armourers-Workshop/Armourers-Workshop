package moe.plushie.armourers_workshop.init;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exporter.SkinExportManager;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.command.ReflectArgumentBuilder;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.PreferenceManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModCommands {

    private static final DynamicCommandExceptionType ERROR_MISSING_SKIN = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingSkin", ob);
    });

    private static final DynamicCommandExceptionType ERROR_MISSING_ITEM_STACK = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingItemSkinnable", ob);
    });

    /// :/armourers setSkin|giveSkin|clearSkin
    public static LiteralArgumentBuilder<CommandSourceStack> commands() {
        return Commands.literal("armourers")
                .then(ReflectArgumentBuilder.literal("config", ModConfig.Client.class))
                .then(ReflectArgumentBuilder.literal("debug", ModDebugger.class))
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("library").then(Commands.literal("reload").executes(Executor::reloadLibrary)))
                .then(Commands.literal("setSkin").then(targets().then(slots().then(skins().executes(Executor::setSkin))).then(skins().executes(Executor::setSkin))))
                .then(Commands.literal("giveSkin").then(targets().then(skins().executes(Executor::giveSkin))))
                .then(Commands.literal("clearSkin").then(targets().then(slotNames().then(slots().executes(Executor::clearSkin))).executes(Executor::clearSkin)))
                .then(Commands.literal("exportSkin").then(skinFormats().then(outputFileName().then(scale().executes(Executor::exportSkin)).executes(Executor::exportSkin))))
                .then(Commands.literal("resyncWardrobe").then(targets().executes(Executor::resyncWardrobe)))
                .then(Commands.literal("itemSkinnable").then(addOrRemote().then(overrideTypes().executes(Executor::setItemSkinnable))))
                .then(Commands.literal("setUnlockedSlots").then(targets().then(resizableSlotNames().then(resizableSlotAmounts().executes(Executor::setUnlockedWardrobeSlots)))));
    }

    static ArgumentBuilder<CommandSourceStack, ?> targets() {
        return Commands.argument("targets", EntityArgument.players());
    }

    static ArgumentBuilder<CommandSourceStack, ?> slots() {
        return Commands.argument("slot", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSourceStack, ?> skinFormats() {
        return Commands.argument("format", ListArgument.list(SkinExportManager.getExporters()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> scale() {
        return Commands.argument("scale", FloatArgumentType.floatArg());
    }

    static ArgumentBuilder<CommandSourceStack, ?> outputFileName() {
        return Commands.argument("name", StringArgumentType.string());
    }

    static ArgumentBuilder<CommandSourceStack, ?> resizableSlotAmounts() {
        return Commands.argument("amount", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSourceStack, ?> resizableSlotNames() {
        Stream<SkinSlotType> slotTypes = Arrays.stream(SkinSlotType.values()).filter(SkinSlotType::isResizable);
        return Commands.argument("slot_name", new ListArgument(slotTypes.map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> slotNames() {
        return Commands.argument("slot_name", new ListArgument(Arrays.stream(SkinSlotType.values()).map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> overrideTypes() {
        return Commands.argument("skin_type", new ListArgument(Arrays.stream(ItemOverrideType.values()).map(ItemOverrideType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> skins() {
        return Commands.argument("skin", new FileArgument(EnvironmentManager.getSkinLibraryDirectory()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> addOrRemote() {
        return Commands.argument("operator", new ListArgument(Lists.newArrayList("add", "remove")));
    }

    static ArgumentBuilder<CommandSourceStack, ?> dyes() {
        return Commands.argument("dye", StringArgumentType.string());
    }

    private static class Executor {

        static int reloadLibrary(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            SkinLibraryManager.getServer().start();
            return 0;
        }

        static boolean containsNode(CommandContext<CommandSourceStack> context, String name) {
            for (ParsedCommandNode<?> node : context.getNodes()) {
                if (name.equals(node.getNode().getName())) {
                    return true;
                }
            }
            return false;
        }

        static int giveSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            String identifier = FileArgument.getString(context, "skin");
            SkinDescriptor descriptor = loadSkinDescriptor(identifier);
            if (descriptor.isEmpty()) {
                return 0;
            }
            ItemStack itemStack = descriptor.asItemStack();
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                boolean flag = player.inventory.add(itemStack);
                if (flag && itemStack.isEmpty()) {
                    itemStack.setCount(1);
                    ItemEntity itemEntity1 = player.drop(itemStack, false);
                    if (itemEntity1 != null) {
                        itemEntity1.makeFakeItem();
                    }
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itemEntity = player.drop(itemStack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                        itemEntity.setOwner(player.getUUID());
                    }
                }
                context.getSource().sendSuccess(new TranslatableComponent("commands.give.success.single", 1, itemStack.getDisplayName(), player.getDisplayName()), true);
            }
            return 1;
        }

        static int setSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            String identifier = FileArgument.getString(context, "skin");
            SkinDescriptor descriptor = loadSkinDescriptor(identifier);
            if (descriptor.isEmpty()) {
                return 0;
            }
            ItemStack itemStack = descriptor.asItemStack();
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                SkinSlotType slotType = SkinSlotType.of(descriptor.getType());
                if (slotType == null || wardrobe == null) {
                    continue;
                }
                int slot = wardrobe.getFreeSlot(slotType);
                if (containsNode(context, "slot")) {
                    slot = IntegerArgumentType.getInteger(context, "slot") - 1;
                }
                wardrobe.setItem(slotType, slot, itemStack);
                wardrobe.broadcast();
            }
            return 0;
        }

        static int clearSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe == null) {
                    continue;
                }
                if (!containsNode(context, "slot")) {
                    wardrobe.clear();
                    wardrobe.broadcast();
                    continue;
                }
                int slot = IntegerArgumentType.getInteger(context, "slot");
                SkinSlotType slotType = SkinSlotType.of(ListArgument.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                wardrobe.setItem(slotType, slot - 1, ItemStack.EMPTY);
                wardrobe.broadcast();
            }
            return 0;
        }

        static int exportSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            String format = ListArgument.getString(context, "format");
            String filename = StringArgumentType.getString(context, "name");
            float scale = 1.0f;
            if (containsNode(context, "scale")) {
                scale = FloatArgumentType.getFloat(context, "scale");
            }
            Player player = context.getSource().getPlayerOrException();
            ItemStack itemStack = player.getMainHandItem();
            String identifier = SkinDescriptor.of(itemStack).getIdentifier();
            Skin skin = SkinLoader.getInstance().loadSkin(identifier);
            if (skin == null) {
                throw ERROR_MISSING_SKIN.create(identifier);
            }
            float resolvedScale = scale;
            player.sendMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.processing", filename), player.getUUID());
            Util.backgroundExecutor().execute(() -> {
                try {
                    SkinExportManager.exportSkin(skin, format, filename, resolvedScale);
                    player.sendMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.success", filename), player.getUUID());
                } catch (Exception e) {
                    player.sendMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.failure", filename), player.getUUID());
                    e.printStackTrace();
                }
            });
            return 0;
        }

        static int setItemSkinnable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

            Player player = context.getSource().getPlayerOrException();
            String operator = ListArgument.getString(context, "operator");
            ItemOverrideType overrideType = ItemOverrideType.of(ListArgument.getString(context, "skin_type"));
            ItemStack itemStack = player.getMainHandItem();
            if (overrideType == null || itemStack.isEmpty()) {
                throw ERROR_MISSING_ITEM_STACK.create(player.getDisplayName());
            }
            ResourceLocation identifier = Registry.ITEM.getKey(itemStack.getItem());
            String key = String.format("%s:%s", overrideType.getName(), identifier);
            // we always remove and then add again
            if (operator.equals("add")) {
                if (ModConfig.Common.overrides.contains(key)) {
                    return 1; // item already added to the overrides list, ignored.
                }
                ModConfig.Common.overrides.add(key);
            } else {
                if (!ModConfig.Common.overrides.contains(key)) {
                    return 1; // item not found in the overrides list, ignored.
                }
                ModConfig.Common.overrides.remove(key);
            }
            ModConfigSpec.COMMON.save();
            // notify the user of what happened
            String messageKey = "commands.armourers_workshop.armourers.setItemSkinnable." + operator;
            Component overrideTypeName = TranslateUtils.Name.of(overrideType);
            player.sendMessage(TranslateUtils.title(messageKey, itemStack.getDisplayName(), overrideTypeName), player.getUUID());
            return 1;
        }

        static int resyncWardrobe(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe != null) {
                    wardrobe.broadcast();
                }
            }
            return 1;
        }

        static int setUnlockedWardrobeSlots(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe == null) {
                    continue;
                }
                SkinSlotType slotType = SkinSlotType.of(ListArgument.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                int amount = IntegerArgumentType.getInteger(context, "amount");
                wardrobe.setUnlockedSize(slotType, MathUtils.clamp(amount, 0, slotType.getMaxSize()));
                wardrobe.broadcast();
            }
            return 1;
        }

        static SkinDescriptor loadSkinDescriptor(String identifier) {
            boolean needCopy = false;
            if (identifier.startsWith("/")) {
                identifier = DataDomain.DEDICATED_SERVER.normalize(identifier);
                needCopy = true; // save the skin to the database
            }
            Skin skin = SkinLoader.getInstance().loadSkin(identifier);
            if (skin != null) {
                if (needCopy) {
                    identifier = SkinLoader.getInstance().saveSkin(identifier, skin);
                }
                return new SkinDescriptor(identifier, skin.getType(), ColorScheme.EMPTY);
            }
            return SkinDescriptor.EMPTY;
        }
    }
}

