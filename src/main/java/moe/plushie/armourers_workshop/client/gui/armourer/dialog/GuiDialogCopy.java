package moe.plushie.armourers_workshop.client.gui.armourer.dialog;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.block.SkinBlock;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDialogCopy extends AbstractGuiDialog {

    private final ISkinType skinType;
    
    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonCopy;
    private GuiDropDownList dropDownSrcPart;
    private GuiDropDownList dropDownDesPart;
    private GuiCheckBox checkMirror;
    private final SkinProperties skinProperties;
    
    public GuiDialogCopy(GuiScreen parent, String name, IDialogCallback callback, int width, int height, ISkinType skinType, SkinProperties skinProperties) {
        super(parent, name, callback, width, height);
        this.skinType = skinType;
        this.skinProperties = skinProperties;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "close"));
        buttonCopy = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "copy"));
        checkMirror = new GuiCheckBox(-1, x + 10, y  + height - 50, GuiHelper.getLocalizedControlName(name, "mirror"), false);
        
        dropDownSrcPart = new GuiDropDownList(0, x + 10, y + 35, 80, "", null);
        dropDownDesPart = new GuiDropDownList(0, x + 100, y + 35, 80, "", null);
        if (skinType != null) {
            if (skinType != SkinTypeRegistry.skinBlock) {
                for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                    addPartToDropDown(dropDownSrcPart, skinType.getSkinParts().get(i));
                    addPartToDropDown(dropDownDesPart, skinType.getSkinParts().get(i));
                }
            } else {
                boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProperties);
                ISkinPartType partType;
                if (multiblock) {
                    partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock;
                } else {
                    partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partBase;
                }
                addPartToDropDown(dropDownSrcPart, partType);
                addPartToDropDown(dropDownDesPart, partType);
            }
        }
        dropDownSrcPart.setListSelectedIndex(0);
        dropDownDesPart.setListSelectedIndex(0);
        
        buttonList.add(buttonClose);
        buttonList.add(buttonCopy);
        buttonList.add(checkMirror);
        buttonList.add(dropDownSrcPart);
        buttonList.add(dropDownDesPart);
    }
    
    private void addPartToDropDown(GuiDropDownList dropDown, ISkinPartType partType) {
        String regName = partType.getRegistryName();
        String disName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(partType);
        dropDown.addListItem(disName, regName, true);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonCopy) {
            returnDialogResult(DialogResult.OK);
        }
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(name, "srcPart"), x + 10, y + 25, 4210752);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(name, "desPart"), x + 100, y + 25, 4210752);
        drawTitle();
        dropDownSrcPart.drawForeground(mc, mouseX, mouseY, partialTickTime);
        dropDownDesPart.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }
    
    public ISkinPartType getSrcPart() {
        return SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(dropDownSrcPart.getListSelectedItem().tag);
    }
    
    public ISkinPartType getDesPart() {
        return SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(dropDownDesPart.getListSelectedItem().tag);
    }
    
    public boolean isMirror() {
        return checkMirror.isChecked();
    }
}
