package moe.plushie.armourers_workshop.init.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.ExecuteCommandPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectArgumentBuilder<S> extends LiteralArgumentBuilder<S> {

    private static final ImmutableMap<Class<?>, Function<Pair<Object, Field>, ArgumentBuilder<CommandSource, ?>>> FIELD_BUILDERS =
            new ImmutableMap.Builder<Class<?>, Function<Pair<Object, Field>, ArgumentBuilder<CommandSource, ?>>>()
                    .put(boolean.class, pair -> argument(pair, BoolArgumentType.bool(), BoolArgumentType::getBool))
                    .put(int.class, pair -> argument(pair, IntegerArgumentType.integer(), IntegerArgumentType::getInteger))
                    .put(double.class, pair -> argument(pair, DoubleArgumentType.doubleArg(), DoubleArgumentType::getDouble))
                    .put(float.class, pair -> argument(pair, FloatArgumentType.floatArg(), FloatArgumentType::getFloat))
                    .put(String.class, pair -> argument(pair, StringArgumentType.string(), StringArgumentType::getString))
                    .build();

    private final Class<?> object;

    protected ReflectArgumentBuilder(String literal, Class<?> object) {
        super(literal);
        this.object = object;
    }

    public static <S> ReflectArgumentBuilder<S> literal(final String name, Class<?> object) {
        return new ReflectArgumentBuilder<>(name, object);
    }

    public static <R> ArgumentBuilder<CommandSource, ?> argument(Pair<Object, Field> pair, ArgumentType<R> argumentType, BiFunction<CommandContext<?>, String, R> argumentParser) {
        return Commands.literal(pair.getSecond().getName())
                .then(Commands.argument("value", argumentType).executes(context -> {
                    R value =  argumentParser.apply(context, "value");
                    String name = pair.getSecond().getName();
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    NetworkHandler.getInstance().sendTo(ExecuteCommandPacket.set(name, value), player);
                    return 0;
                }))
                .executes(context -> {
                    String name = pair.getSecond().getName();
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    NetworkHandler.getInstance().sendTo(ExecuteCommandPacket.get(name), player);
                    return 0;
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<CommandNode<S>> getArguments() {
        ArrayList<CommandNode<S>> nodes = new ArrayList<>(super.getArguments());
        for (Field field : object.getDeclaredFields()) {
            Function<Pair<Object, Field>, ArgumentBuilder<CommandSource, ?>> function = FIELD_BUILDERS.get(field.getType());
            if (function != null) {
                nodes.add((CommandNode<S>) function.apply(Pair.of(object, field)).build());
            }
        }
        return nodes;
    }
}