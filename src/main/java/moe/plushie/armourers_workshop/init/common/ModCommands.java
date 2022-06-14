package moe.plushie.armourers_workshop.init.common;

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
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exporter.SkinExportManager;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.command.ReflectArgumentBuilder;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModCommands {

    private static final DynamicCommandExceptionType ERROR_MISSING_SKIN = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingSkin", ob);
    });

    /// :/armourers setSkin|giveSkin|clearSkin
    public static LiteralArgumentBuilder<CommandSource> commands() {
        return Commands.literal("armourers")
                .then(ReflectArgumentBuilder.literal("config", ModConfig.Client.class))
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("library").then(Commands.literal("reload").executes(Executor::reloadLibrary)))
                .then(Commands.literal("setSkin").then(targets().then(slots().then(skins().executes(Executor::setSkin))).then(skins().executes(Executor::setSkin))))
                .then(Commands.literal("giveSkin").then(targets().then(skins().executes(Executor::giveSkin))))
                .then(Commands.literal("clearSkin").then(targets().then(slotNames().then(slots().executes(Executor::clearSkin))).executes(Executor::clearSkin)))
                .then(Commands.literal("exportSkin").then(skinFormats().then(outputFileName().then(scale().executes(Executor::exportSkin)).executes(Executor::exportSkin))))
                .then(Commands.literal("resyncWardrobe").then(targets().executes(Executor::resyncWardrobe)))
                .then(Commands.literal("setUnlockedSlots").then(targets().then(resizableSlotNames().then(resizableSlotAmounts().executes(Executor::setUnlockedWardrobeSlots)))));
    }

    static ArgumentBuilder<CommandSource, ?> targets() {
        return Commands.argument("targets", EntityArgument.players());
    }

    static ArgumentBuilder<CommandSource, ?> slots() {
        return Commands.argument("slot", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSource, ?> skinFormats() {
        return Commands.argument("format", ListArgument.list(SkinExportManager.getExporters()));
    }

    static ArgumentBuilder<CommandSource, ?> scale() {
        return Commands.argument("scale", FloatArgumentType.floatArg());
    }

    static ArgumentBuilder<CommandSource, ?> outputFileName() {
        return Commands.argument("name", StringArgumentType.string());
    }

    static ArgumentBuilder<CommandSource, ?> resizableSlotAmounts() {
        return Commands.argument("amount", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSource, ?> resizableSlotNames() {
        Stream<SkinSlotType> slotTypes = Arrays.stream(SkinSlotType.values()).filter(SkinSlotType::isResizable);
        return Commands.argument("slot_name", new ListArgument(slotTypes.map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSource, ?> slotNames() {
        return Commands.argument("slot_name", new ListArgument(Arrays.stream(SkinSlotType.values()).map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSource, ?> skins() {
        return Commands.argument("skin", new FileArgument(AWCore.getSkinLibraryDirectory()));
    }

    static ArgumentBuilder<CommandSource, ?> dyes() {
        return Commands.argument("dye", StringArgumentType.string());
    }

    private static class Executor {

        static int reloadLibrary(CommandContext<CommandSource> context) throws CommandSyntaxException {
            SkinLibraryManager.getServer().start();
            return 0;
        }

        static boolean containsNode(CommandContext<CommandSource> context, String name) {
            for (ParsedCommandNode<?> node : context.getNodes()) {
                if (name.equals(node.getNode().getName())) {
                    return true;
                }
            }
            return false;
        }

        static int giveSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            String identifier = FileArgument.getString(context, "skin");
            SkinDescriptor descriptor = loadSkinDescriptor(identifier);
            if (descriptor.isEmpty()) {
                return 0;
            }
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

        static int setSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            String identifier = FileArgument.getString(context, "skin");
            SkinDescriptor descriptor = loadSkinDescriptor(identifier);
            if (descriptor.isEmpty()) {
                return 0;
            }
            ItemStack itemStack = descriptor.asItemStack();
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                SkinSlotType slotType = SkinSlotType.of(descriptor.getType());
                if (slotType == null || wardrobe == null) {
                    continue;
                }
                int slot = wardrobe.getFreeSlot(slotType);
                if (containsNode(context, "slot")) {
                    slot = IntegerArgumentType.getInteger(context, "slot");
                }
                wardrobe.setItem(slotType, slot - 1, itemStack);
                wardrobe.sendToAll();
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
                    wardrobe.sendToAll();
                    continue;
                }
                int slot = IntegerArgumentType.getInteger(context, "slot");
                SkinSlotType slotType = SkinSlotType.of(ListArgument.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                wardrobe.setItem(slotType, slot - 1, ItemStack.EMPTY);
                wardrobe.sendToAll();
            }
            return 0;
        }

        static int exportSkin(CommandContext<CommandSource> context) throws CommandSyntaxException {
            String format = ListArgument.getString(context, "format");
            String filename = StringArgumentType.getString(context, "name");
            float scale = 1.0f;
            if (containsNode(context, "scale")) {
                scale = FloatArgumentType.getFloat(context, "scale");
            }
            PlayerEntity player = context.getSource().getPlayerOrException();
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

        static int resyncWardrobe(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe != null) {
                    wardrobe.sendToAll();
                }
            }
            return 1;
        }

        static int setUnlockedWardrobeSlots(CommandContext<CommandSource> context) throws CommandSyntaxException {
            for (PlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe == null) {
                    continue;
                }
                SkinSlotType slotType = SkinSlotType.of(ListArgument.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                int amount = IntegerArgumentType.getInteger(context, "amount");
                wardrobe.setUnlockedSize(slotType, MathHelper.clamp(amount, 0, slotType.getMaxSize()));
                wardrobe.sendToAll();
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

