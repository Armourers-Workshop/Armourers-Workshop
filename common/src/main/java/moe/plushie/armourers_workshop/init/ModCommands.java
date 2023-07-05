package moe.plushie.armourers_workshop.init;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.network.ExecuteAlertPacket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.exporter.SkinExportManager;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.command.ColorArgumentType;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgumentType;
import moe.plushie.armourers_workshop.init.command.ComponentArgumentType;
import moe.plushie.armourers_workshop.init.command.FileArgumentType;
import moe.plushie.armourers_workshop.init.command.ListArgumentType;
import moe.plushie.armourers_workshop.init.command.ReflectArgumentBuilder;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.init.platform.RegistryManager;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModCommands {

    private static final HashMap<String, ISkinPaintType> DYE_TYPES = Util.make(() -> {
        HashMap<String, ISkinPaintType> map = new HashMap<>();
        for (int i = 0; i < 8; ++i) {
            ISkinPaintType paintType = SkinPaintTypes.byId(i + 1);
            String name = paintType.getRegistryName().getPath();
            map.put(name.replaceAll("_", ""), paintType);
        }
        return map;
    });

    private static final DynamicCommandExceptionType ERROR_MISSING_DYE_SLOT = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingSkin", ob);
    });

    private static final DynamicCommandExceptionType ERROR_MISSING_SKIN = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingSkin", ob);
    });

    private static final DynamicCommandExceptionType ERROR_MISSING_ITEM_STACK = new DynamicCommandExceptionType(ob -> {
        return TranslateUtils.title("commands.armourers_workshop.armourers.error.missingItemSkinnable", ob);
    });

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(commands());
    }

    // :/armourers setSkin|giveSkin|clearSkin
    public static LiteralArgumentBuilder<CommandSourceStack> commands() {
        return Commands.literal("armourers")
                .then(ReflectArgumentBuilder.literal("config", ModConfig.Client.class))
                .then(ReflectArgumentBuilder.literal("debug", ModDebugger.class))
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("library").then(Commands.literal("reload").executes(Executor::reloadLibrary)))
                .then(Commands.literal("setSkin").then(entities().then(slots().then(skins().then(skinDying().executes(Executor::setSkin)).executes(Executor::setSkin))).then(skins().then(skinDying().executes(Executor::setSkin)).executes(Executor::setSkin))))
                .then(Commands.literal("giveSkin").then(players().then(skins().then(skinDying().executes(Executor::giveSkin)).executes(Executor::giveSkin))))
                .then(Commands.literal("clearSkin").then(entities().then(slotNames().then(slots().executes(Executor::clearSkin))).executes(Executor::clearSkin)))
                .then(Commands.literal("exportSkin").then(skinFormats().then(outputFileName().then(scale().executes(Executor::exportSkin)).executes(Executor::exportSkin))))
                .then(Commands.literal("setColor").then(entities().then(dyesSlotNames().then(dyeColor().executes(Executor::setColor)))))
                .then(Commands.literal("rsyncWardrobe").then(players().executes(Executor::resyncWardrobe)))
                .then(Commands.literal("openWardrobe").then(entities().executes(Executor::openWardrobe)))
                .then(Commands.literal("itemSkinnable").then(addOrRemote().then(overrideTypes().executes(Executor::setItemSkinnable))))
                .then(Commands.literal("notify").then(alertNotify()).then(toastNotify()))
                .then(Commands.literal("setUnlockedSlots").then(entities().then(resizableSlotNames().then(resizableSlotAmounts().executes(Executor::setUnlockedWardrobeSlots)))));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> alertNotify() {
        return Commands.literal("alert").then(players().then(textInput("message").then(textInput("title").then(textInput("confirm").executes(Executor::sendAlertNotify)).executes(Executor::sendAlertNotify)).executes(Executor::sendAlertNotify)));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> toastNotify() {
        return Commands.literal("toast").then(players().then(textInput("message").then(textInput("title").then(nbtInput("icon").executes(Executor::sendToastNotify)).executes(Executor::sendToastNotify)).executes(Executor::sendToastNotify)));
    }

    static ArgumentBuilder<CommandSourceStack, ?> players() {
        return Commands.argument("targets", EntityArgument.players());
    }

    static ArgumentBuilder<CommandSourceStack, ?> entities() {
        return Commands.argument("entities", EntityArgument.entities());
    }

    static ArgumentBuilder<CommandSourceStack, ?> slots() {
        return Commands.argument("slot", IntegerArgumentType.integer(1, 10));
    }

    static ArgumentBuilder<CommandSourceStack, ?> skinFormats() {
        return Commands.argument("format", ListArgumentType.list(SkinExportManager.getExporters()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> skinDying() {
        return Commands.argument("dying", new ColorSchemeArgumentType());
    }

    static ArgumentBuilder<CommandSourceStack, ?> dyesSlotNames() {
        return Commands.argument("dye_slot", new ListArgumentType(DYE_TYPES.keySet()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> dyeColor() {
        return Commands.argument("color", new ColorArgumentType());
    }

    static ArgumentBuilder<CommandSourceStack, ?> textInput(String key) {
        return Commands.argument(key, ComponentArgumentType.textComponent());
    }

    static ArgumentBuilder<CommandSourceStack, ?> nbtInput(String key) {
        return Commands.argument(key, CompoundTagArgument.compoundTag());
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
        return Commands.argument("slot_name", new ListArgumentType(slotTypes.map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> slotNames() {
        return Commands.argument("slot_name", new ListArgumentType(Arrays.stream(SkinSlotType.values()).map(SkinSlotType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> overrideTypes() {
        return Commands.argument("skin_type", new ListArgumentType(Arrays.stream(ItemOverrideType.values()).map(ItemOverrideType::getName).collect(Collectors.toList())));
    }

    static ArgumentBuilder<CommandSourceStack, ?> skins() {
        return Commands.argument("skin", new FileArgumentType(EnvironmentManager.getSkinLibraryDirectory()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> addOrRemote() {
        return Commands.argument("operator", new ListArgumentType(Lists.newArrayList("add", "remove")));
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

        static int setColor(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ISkinPaintType paintType = DYE_TYPES.get(ListArgumentType.getString(context, "dye_slot"));
            if (paintType == null) {
                throw ERROR_MISSING_DYE_SLOT.create(null);
            }
            PaintColor paintColor = ColorArgumentType.getColor(context, "color");
            for (Entity entity : EntityArgument.getEntities(context, "entities")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe == null) {
                    continue;
                }
                int slot = SkinSlotType.getDyeSlotIndex(paintType);
                ItemStack itemStack = new ItemStack(ModItems.BOTTLE.get());
                ColorUtils.setColor(itemStack, paintColor);
                Container inventory = wardrobe.getInventory();
                inventory.setItem(slot, itemStack);
                wardrobe.broadcast();
            }
            return 0;
        }

        static int giveSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            SkinDescriptor descriptor = loadSkinDescriptor(context);
            if (descriptor.isEmpty()) {
                return 0;
            }
            ItemStack itemStack = descriptor.asItemStack();
            for (Player player : EntityArgument.getPlayers(context, "targets")) {
                boolean flag = player.getInventory().add(itemStack);
                if (flag && itemStack.isEmpty()) {
                    itemStack.setCount(1);
                    ItemEntity itemEntity1 = player.drop(itemStack, false);
                    if (itemEntity1 != null) {
                        itemEntity1.makeFakeItem();
                    }
                    player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itemEntity = player.drop(itemStack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                        //itemEntity.setOwner(player.getUUID());
                        itemEntity.setThrower(player.getUUID());
                    }
                }
                context.getSource().sendSuccess(Component.translatable("commands.give.success.single", 1, itemStack.getDisplayName(), player.getDisplayName()), true);
            }
            return 1;
        }

        static int setSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            SkinDescriptor descriptor = loadSkinDescriptor(context);
            if (descriptor.isEmpty()) {
                return 0;
            }
            ItemStack itemStack = descriptor.asItemStack();
            for (Entity entity : EntityArgument.getEntities(context, "entities")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
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
            for (Entity entity : EntityArgument.getEntities(context, "entities")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe == null) {
                    continue;
                }
                if (!containsNode(context, "slot")) {
                    wardrobe.clear();
                    wardrobe.broadcast();
                    continue;
                }
                int slot = IntegerArgumentType.getInteger(context, "slot");
                SkinSlotType slotType = SkinSlotType.of(ListArgumentType.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                wardrobe.setItem(slotType, slot - 1, ItemStack.EMPTY);
                wardrobe.broadcast();
            }
            return 0;
        }

        static int exportSkin(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            String format = ListArgumentType.getString(context, "format");
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
            player.sendSystemMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.processing", filename));
            Util.backgroundExecutor().execute(() -> {
                try {
                    SkinExportManager.exportSkin(skin, format, filename, resolvedScale);
                    player.sendSystemMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.success", filename));
                } catch (Exception e) {
                    player.sendSystemMessage(TranslateUtils.title("commands.armourers_workshop.armourers.exportSkin.failure", filename));
                    e.printStackTrace();
                }
            });
            return 0;
        }

        static int setItemSkinnable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

            Player player = context.getSource().getPlayerOrException();
            String operator = ListArgumentType.getString(context, "operator");
            ItemOverrideType overrideType = ItemOverrideType.of(ListArgumentType.getString(context, "skin_type"));
            ItemStack itemStack = player.getMainHandItem();
            if (overrideType == null || itemStack.isEmpty()) {
                throw ERROR_MISSING_ITEM_STACK.create(player.getScoreboardName());
            }
            ResourceLocation identifier = RegistryManager.getKey(itemStack.getItem());
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
            player.sendSystemMessage(TranslateUtils.title(messageKey, itemStack.getDisplayName(), overrideTypeName));
            return 1;
        }

        static int resyncWardrobe(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(player);
                if (wardrobe != null) {
                    wardrobe.broadcast();
                }
            }
            return 1;
        }

        static int openWardrobe(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            Player player = context.getSource().getPlayerOrException();
            for (Entity entity : EntityArgument.getEntities(context, "entities")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe != null) {
                    MenuManager.openMenu(ModMenuTypes.WARDROBE_OP, player, wardrobe);
                    break;
                }
            }
            return 1;
        }

        static int sendToastNotify(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            return sendUserNotify(context, 0x80000000);
        }

        static int sendAlertNotify(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            return sendUserNotify(context, 0x00000000);
        }

        static int sendUserNotify(CommandContext<CommandSourceStack> context, int type) throws CommandSyntaxException {
            // read context from command
            Component message1 = ComponentArgumentType.getComponent(context, "message");
            Component title1 = TranslateUtils.title("commands.armourers_workshop.notify.title");
            Component confirm1 = TranslateUtils.title("commands.armourers_workshop.notify.confirm");
            CompoundTag icon1 = null;
            if (containsNode(context, "title")) {
                title1 = ComponentArgumentType.getComponent(context, "title");
            }
            if (containsNode(context, "confirm")) {
                confirm1 = ComponentArgumentType.getComponent(context, "confirm");
            }
            if (containsNode(context, "icon")) {
                icon1 = CompoundTagArgument.getCompoundTag(context, "icon");
            }
            for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                NetworkManager.sendTo(new ExecuteAlertPacket(title1, message1, confirm1, type, icon1), player);
            }
            return 1;
        }

        static int setUnlockedWardrobeSlots(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            for (Entity entity : EntityArgument.getEntities(context, "entities")) {
                SkinWardrobe wardrobe = SkinWardrobe.of(entity);
                if (wardrobe == null) {
                    continue;
                }
                SkinSlotType slotType = SkinSlotType.of(ListArgumentType.getString(context, "slot_name"));
                if (slotType == null) {
                    continue;
                }
                int amount = IntegerArgumentType.getInteger(context, "amount");
                wardrobe.setUnlockedSize(slotType, MathUtils.clamp(amount, 0, slotType.getMaxSize()));
                wardrobe.broadcast();
            }
            return 1;
        }

        static SkinDescriptor loadSkinDescriptor(CommandContext<CommandSourceStack> context) {
            String identifier = FileArgumentType.getString(context, "skin");
            if (identifier.isEmpty()) {
                return SkinDescriptor.EMPTY;
            }
            ColorScheme scheme = ColorScheme.EMPTY;
            if (containsNode(context, "dying")) {
                scheme = ColorSchemeArgumentType.getColorScheme(context, "dying");
            }
            boolean needCopy = false;
            if (identifier.startsWith("/")) {
                identifier = DataDomain.DEDICATED_SERVER.normalize(identifier);
                needCopy = true; // save the skin to the database
            }
            return SkinLoader.getInstance().loadSkinFromDB(identifier, scheme, needCopy);
        }
    }
}

