package moe.plushie.armourers_workshop.client.gui.newgui;

public interface IDialogCallback {
    
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result);
    
    public static enum DialogResult {
        OK, CANCEL
    }
}
