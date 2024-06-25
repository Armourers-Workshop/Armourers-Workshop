package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.commands.CommandSourceStack;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class ABI {

    public static void sendSuccess(@This CommandSourceStack sourceStack, Component text, boolean bl) {
        sourceStack.sendSuccess(() -> text, bl);
    }
}
