package moe.plushie.armourers_workshop.client.gui.controls;

public interface IDialogCallback {
    
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result);
    
    public static enum DialogResult {
        OK, CANCEL
    }
}
