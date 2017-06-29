package riskyken.armourersWorkshop.client.gui.armourer.dialog;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.skin.type.block.SkinBlock;

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
                boolean multiblock = skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false);
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
