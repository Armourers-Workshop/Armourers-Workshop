package moe.plushie.armourers_workshop.compat.core;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.command.HasSkinArgumentType;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

@Available("[1.16, )")
public abstract class AbstractEntitySelectorParser {

    private static boolean IS_INITIALED = false;

    private final String name;
    private final Component description;

    protected AbstractEntitySelectorParser(String name, Component description) {
        this.name = name;
        this.description = description;
    }

    public static void register(Consumer<AbstractEntitySelectorParser> consumer) {
        if (!IS_INITIALED) {
            consumer.accept(new HasSkinParser());
            IS_INITIALED = true;
        }
    }

    public abstract void parse(EntitySelectorParser parser) throws CommandSyntaxException;

    public boolean canUse(EntitySelectorParser parser) {
        return true;
    }

    public String getName() {
        return name;
    }

    public Component getDescription() {
        return description;
    }

    // @p[skin={type=outfit,slot=0,id=""}]
    private static class HasSkinParser extends AbstractEntitySelectorParser {

        protected HasSkinParser() {
            super("skin", Component.literal("commands.armourers_workshop.hasskin.description"));
        }

        @Override
        public void parse(EntitySelectorParser parser) throws CommandSyntaxException {
            var bl = parser.shouldInvertValue();
            var type = new HasSkinArgumentType();
            var predicate = type.parse(parser.getReader());
            parser.addPredicate(entity -> predicate.test(entity) != bl);
        }
    }
}
