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
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class GuiDialogClear extends AbstractGuiDialog {

    private GuiButtonExt buttonClose;
    private GuiButtonExt buttonClear;
    private GuiDropDownList dropDownParts;
    private GuiCheckBox checkClearBlocks;
    private GuiCheckBox checkClearPaint;
    private final ISkinType skinType;
    
    public GuiDialogClear(GuiScreen parent, String name, IDialogCallback callback, int width, int height, ISkinType skinType) {
        super(parent, name, callback, width, height);
        this.skinType = skinType;
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
            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                ISkinPartType partType = skinType.getSkinParts().get(i);
                String regName = partType.getRegistryName();
                String disName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(partType);
                dropDownParts.addListItem(disName, regName, true);
            }
        }
        dropDownParts.setListSelectedIndex(0);
        checkClearBlocks = new GuiCheckBox(0, x + 10, y  + height - 60, GuiHelper.getLocalizedControlName(name, "clearBlocks"), true);
        checkClearPaint = new GuiCheckBox(0, x + 10, y  + height - 50, GuiHelper.getLocalizedControlName(name, "clearPaint"), true);
        
        buttonList.add(buttonClose);
        buttonList.add(buttonClear);
        buttonList.add(dropDownParts);
        buttonList.add(checkClearBlocks);
        buttonList.add(checkClearPaint);
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
