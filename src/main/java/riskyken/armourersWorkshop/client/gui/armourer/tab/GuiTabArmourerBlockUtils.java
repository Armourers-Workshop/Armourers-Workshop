package riskyken.armourersWorkshop.client.gui.armourer.tab;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.armourer.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.armourer.dialog.AbstractGuiDialog;
import riskyken.armourersWorkshop.client.gui.armourer.dialog.AbstractGuiDialog.DialogResult;
import riskyken.armourersWorkshop.client.gui.armourer.dialog.AbstractGuiDialog.IDialogCallback;
import riskyken.armourersWorkshop.client.gui.armourer.dialog.GuiDialogClear;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiArmourerBlockUtil;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerBlockUtils extends GuiTabPanel implements IDialogCallback {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.ARMOURER);
    
    private final TileEntityArmourer tileEntity;
    
    private GuiButtonExt buttonClear;
    private GuiButtonExt buttonCopy;
    
    public GuiTabArmourerBlockUtils(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        buttonClear = new GuiButtonExt(10, 10, 20, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear"));
        buttonCopy = new GuiButtonExt(11, 10, 40, 70, 16, GuiHelper.getLocalizedControlName(guiName, "copy"));
        
        buttonCopy.enabled = false;
        
        buttonList.add(buttonClear);
        buttonList.add(buttonCopy);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        String guiName = tileEntity.getInventoryName();
        if (button == buttonClear) {
            ((GuiArmourer)parent).openDialog(new GuiDialogClear(parent, guiName + ".dialog.clear", (IDialogCallback) parent, 190, 140, tileEntity.getSkinType()));
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog != null && dialog instanceof GuiDialogClear) {
                String tag = ((GuiDialogClear)dialog).getClearTag();
                if (!StringUtils.isNullOrEmpty(tag)) {
                    ISkinPartType partType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(tag);
                    MessageClientGuiArmourerBlockUtil message;
                    message = new MessageClientGuiArmourerBlockUtil("clear", partType, null);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
            }
        }
    }
}
