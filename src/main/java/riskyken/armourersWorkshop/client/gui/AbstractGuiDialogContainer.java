package riskyken.armourersWorkshop.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        this.dialog.initGui();
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
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
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
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDialogOpen()) {
            dialog.mouseMovedOrUp(mouseX, mouseY, state);
        } else {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
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
