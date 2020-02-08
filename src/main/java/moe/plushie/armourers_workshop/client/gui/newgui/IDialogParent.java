package moe.plushie.armourers_workshop.client.gui.newgui;

public interface IDialogParent {
    
    public void openDialog(AbstractGuiDialog dialog);

    public boolean isDialogOpen();
    
    public void closeDialog();
}
