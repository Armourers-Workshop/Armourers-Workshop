package extensions.net.minecraft.commands.CommandSourceStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

@Extension
@Available("[1.20, )")
public class ABI {

    public static void sendSuccess(@This CommandSourceStack sourceStack, Component text, boolean bl) {
        sourceStack.sendSuccess(() -> text, bl);
    }
}
