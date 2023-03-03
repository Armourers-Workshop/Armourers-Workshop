package moe.plushie.armourers_workshop.init.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class ComponentArgumentType implements IArgumentType<Component> {

    private final ComponentArgument argument;

    public ComponentArgumentType(ComponentArgument argument) {
        this.argument = argument;
    }

    public static Component getComponent(CommandContext<CommandSourceStack> commandContext, String string) {
        return commandContext.getArgument(string, Component.class);
    }

    public static ComponentArgumentType textComponent() {
        return new ComponentArgumentType(ComponentArgument.textComponent());
    }

    @Override
    public Component parse(StringReader reader) throws CommandSyntaxException {
        Component component = argument.parse(reader);
        // fix bug for vanilla, because it incorrectly positions.
        reader.setCursor(reader.getCursor() - 1);
        return component;
    }

    @Override
    public Collection<String> getExamples() {
        return argument.getExamples();
    }

    public static class Serializer implements IArgumentSerializer<ComponentArgumentType> {

        @Override
        public void serializeToNetwork(ComponentArgumentType argument, FriendlyByteBuf buffer) {
        }

        @Override
        public ComponentArgumentType deserializeFromNetwork(FriendlyByteBuf buffer) {
            return ComponentArgumentType.textComponent();
        }

        @Override
        public void serializeToJson(ComponentArgumentType argument, JsonObject json) {
        }
    }
}
