package moe.plushie.armourers_workshop.common.command;

public class CommandWardrobe extends ModSubCommands {

    public CommandWardrobe(ModCommand parent) {
        super(parent, "wardrobe");
        addSubCommand(new CommandWardrobeSetColour(this));
    }
}
