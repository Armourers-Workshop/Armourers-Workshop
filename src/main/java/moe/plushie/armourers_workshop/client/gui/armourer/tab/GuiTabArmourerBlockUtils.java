package moe.plushie.armourers_workshop.client.gui.armourer.tab;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.gui.armourer.dialog.GuiDialogClear;
import moe.plushie.armourers_workshop.client.gui.armourer.dialog.GuiDialogCopy;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiArmourerBlockUtil;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerBlockUtils extends GuiTabPanel implements IDialogCallback {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_ARMOURER);
    
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
        String guiName = tileEntity.getName();
        buttonClear = new GuiButtonExt(10, 10, 20, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear"));
        buttonCopy = new GuiButtonExt(11, 10, 40, 70, 16, GuiHelper.getLocalizedControlName(guiName, "copy"));
        
        buttonList.add(buttonClear);
        buttonList.add(buttonCopy);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        String guiName = tileEntity.getName();
        if (button == buttonClear) {
            ((GuiArmourer)parent).openDialog(new GuiDialogClear(parent, guiName + ".dialog.clear", (IDialogCallback) parent, 190, 140, tileEntity.getSkinType(), tileEntity.getSkinProps()));
        }
        if (button == buttonCopy) {
            ((GuiArmourer)parent).openDialog(new GuiDialogCopy(parent, guiName + ".dialog.copy", (IDialogCallback) parent, 190, 140, tileEntity.getSkinType(), tileEntity.getSkinProps()));
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        drawTexturedModalRect(this.x + 7, this.y + 141, 7, 3, 162, 76);
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK & dialog != null) {
            if (dialog instanceof GuiDialogClear) {
                String tag = ((GuiDialogClear)dialog).getClearTag();
                if (!StringUtils.isNullOrEmpty(tag)) {
                    ISkinPartType partType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(tag);
                    boolean clearBlocks = ((GuiDialogClear)dialog).isClearBlocks();
                    boolean clearPaint = ((GuiDialogClear)dialog).isClearPaint();
                    boolean clearMarkers = ((GuiDialogClear)dialog).isClearMarkers();
                    MessageClientGuiArmourerBlockUtil message;
                    message = new MessageClientGuiArmourerBlockUtil("clear", partType, null, clearBlocks, clearPaint, clearMarkers);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
            }
            if (dialog instanceof GuiDialogCopy) {
                ISkinPartType srcPart  = ((GuiDialogCopy)dialog).getSrcPart();
                ISkinPartType desPart  = ((GuiDialogCopy)dialog).getDesPart();
                boolean mirror  = ((GuiDialogCopy)dialog).isMirror();
                MessageClientGuiArmourerBlockUtil message;
                message = new MessageClientGuiArmourerBlockUtil("copy", srcPart, desPart, mirror, false, false);
                PacketHandler.networkWrapper.sendToServer(message);
            }
        }
    }
}
