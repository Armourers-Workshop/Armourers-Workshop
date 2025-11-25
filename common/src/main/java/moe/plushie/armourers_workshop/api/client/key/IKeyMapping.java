package moe.plushie.armourers_workshop.api.client.key;

import net.minecraft.network.chat.Component;

import java.util.Collection;

public interface IKeyMapping {

    boolean consumeClick();

    Component name();

    IKeyCategory category();

    Collection<? extends IKeyModifier> modifiers();
}
