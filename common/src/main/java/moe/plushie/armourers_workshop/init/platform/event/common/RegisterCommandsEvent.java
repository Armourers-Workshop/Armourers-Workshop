package moe.plushie.armourers_workshop.init.platform.event.common;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface RegisterCommandsEvent {

    void register(final LiteralArgumentBuilder<CommandSourceStack> command);
}
