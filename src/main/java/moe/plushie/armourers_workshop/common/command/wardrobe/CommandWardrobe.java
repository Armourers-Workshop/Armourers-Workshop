package moe.plushie.armourers_workshop.common.command.wardrobe;

import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.common.command.ModSubCommands;

public class CommandWardrobe extends ModSubCommands {

    public CommandWardrobe(ModCommand parent) {
        super(parent, "wardrobe");
        addSubCommand(new CommandWardrobeClearSkin(this));
        addSubCommand(new CommandWardrobeClearSkins(this));
        addSubCommand(new CommandWardrobeResync(this));
        addSubCommand(new CommandWardrobeSetColour(this));
        addSubCommand(new CommandWardrobeSetOption(this));
        addSubCommand(new CommandWardrobeSetSkin(this));
        addSubCommand(new CommandWardrobeSetUnlockedSlots(this));
    }
}
