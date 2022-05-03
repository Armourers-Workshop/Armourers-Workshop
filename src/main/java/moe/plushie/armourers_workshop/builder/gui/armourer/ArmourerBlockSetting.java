package moe.plushie.armourers_workshop.builder.gui.armourer;

import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class ArmourerBlockSetting extends ArmourerBaseSetting {

    private ExtendedButton buttonClear;
    private ExtendedButton buttonCopy;

    protected ArmourerBlockSetting(ArmourerContainer container) {
        super("inventory.armourers_workshop.armourer.blockUtils");
    }

    @Override
    protected void init() {
        super.init();

        this.buttonClear = new ExtendedButton(leftPos + 10, topPos + 20, 70, 20, getDisplayText("clear"), this::clearAction);
        this.buttonCopy = new ExtendedButton(leftPos + 10, topPos + 45, 70, 20, getDisplayText("copy"), this::copyAction);

        this.addButton(buttonClear);
        this.addButton(buttonCopy);
    }

    private void clearAction(Button sender) {

    }

    private void copyAction(Button sender) {

    }
}
