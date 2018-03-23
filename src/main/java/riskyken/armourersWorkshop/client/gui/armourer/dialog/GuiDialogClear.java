package riskyken.armourersWorkshop.client.gui.armourer.dialog;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.skin.type.block.SkinBlock;

@SideOnly(Side.CLIENT)
public class GuiDialogClear extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonClear;
    private GuiDropDownList dropDownParts;
    private GuiCheckBox checkClearBlocks;
    private GuiCheckBox checkClearPaint;
    private GuiCheckBox checkClearMarkers;
    private final ISkinType skinType;
    private final SkinProperties skinProperties;
    
    public GuiDialogClear(GuiScreen parent, String name, IDialogCallback callback, int width, int height, ISkinType skinType, SkinProperties skinProperties) {
        super(parent, name, callback, width, height);
        this.skinType = skinType;
        this.skinProperties = skinProperties;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonClose = new GuiButtonExt(-1, x + width - 80 - 10, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "close"));
        buttonClear = new GuiButtonExt(-1, x + width - 160 - 20, y + height - 30, 80, 20, GuiHelper.getLocalizedControlName(name, "clear"));
        dropDownParts = new GuiDropDownList(0, x + 10, y + 20, 60, "", null);
        dropDownParts.addListItem("*", "*", true);
        if (skinType != null) {
            if (skinType != SkinTypeRegistry.skinBlock) {
                for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                    ISkinPartType partType = skinType.getSkinParts().get(i);
                    addPartToDropDown(dropDownParts, partType);
                }
            } else {
                boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProperties);
                ISkinPartType partType;
                if (multiblock) {
                    partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partMultiblock;
                } else {
                    partType = ((SkinBlock)SkinTypeRegistry.skinBlock).partBase;
                }
                addPartToDropDown(dropDownParts, partType);
            }
        }
        dropDownParts.setListSelectedIndex(0);
        checkClearBlocks = new GuiCheckBox(0, x + 10, y  + height - 60, GuiHelper.getLocalizedControlName(name, "clearBlocks"), true);
        checkClearPaint = new GuiCheckBox(0, x + 10, y  + height - 50, GuiHelper.getLocalizedControlName(name, "clearPaint"), true);
        checkClearMarkers = new GuiCheckBox(0, x + 10, y  + height - 70, GuiHelper.getLocalizedControlName(name, "clearMarkers"), true);
        
        buttonList.add(buttonClose);
        buttonList.add(buttonClear);
        buttonList.add(dropDownParts);
        buttonList.add(checkClearBlocks);
        buttonList.add(checkClearPaint);
        buttonList.add(checkClearMarkers);
    }
    
    private void addPartToDropDown(GuiDropDownList dropDown, ISkinPartType partType) {
        String regName = partType.getRegistryName();
        String disName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(partType);
        dropDown.addListItem(disName, regName, true);
    }
    
    public String getClearTag() {
        return dropDownParts.getListSelectedItem().tag;
    }
    
    public boolean isClearBlocks() {
        return checkClearBlocks.isChecked();
    }
    
    public boolean isClearPaint() {
        return checkClearPaint.isChecked();
    }
    
    public boolean isClearMarkers() {
        return checkClearMarkers.isChecked();
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
        if (button == buttonClear) {
            returnDialogResult(DialogResult.OK);
        }
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
        drawTitle();
    }
}
