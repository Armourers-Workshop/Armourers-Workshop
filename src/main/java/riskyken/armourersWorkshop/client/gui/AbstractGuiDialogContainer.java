package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog.DialogResult;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog.IDialogCallback;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiDialogContainer extends GuiContainer implements IDialogCallback {

    protected AbstractGuiDialog dialog;
    
    public AbstractGuiDialogContainer(Container container) {
        super(container);
    }
    
    public void openDialog(AbstractGuiDialog dialog) {
        this.dialog = dialog;
    }
    
    protected boolean isDialogOpen() {
        return dialog != null;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (isDialogOpen()) {
            dialog.initGui();
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (isDialogOpen()) {
            dialog.mouseClicked(mouseX, mouseY, button);
        } else {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (isDialogOpen()) {
            dialog.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        } else {
            super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (isDialogOpen()) {
            dialog.mouseMovedOrUp(mouseX, mouseY, button);
        } else {
            super.mouseMovedOrUp(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) {
        if (isDialogOpen()) {
            dialog.keyTyped(c, keycode);
        } else {
            super.keyTyped(c, keycode);
        }
    }
    
    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        this.dialog = null;
    }
}
