package riskyken.armourersWorkshop.client.gui.wardrobe.tab;

import java.util.BitSet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.wardrobe.GuiWardrobe;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeDisplaySettings extends GuiTabPanel {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    
    EntityPlayer entityPlayer;
    ExPropsPlayerSkinData propsPlayerSkinData;
    EquipmentWardrobeData equipmentWardrobeData;
    
    BitSet armourOverride;
    boolean headOverlay;
    boolean limitLimbs;
    
    private GuiCheckBox[] armourOverrideCheck;
    private GuiCheckBox[] overlayOverrideCheck;
    private GuiCheckBox limitLimbsCheck;
    
    String guiName = "equipmentWardrobe";
    
    public GuiTabWardrobeDisplaySettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, ExPropsPlayerSkinData propsPlayerSkinData, EquipmentWardrobeData equipmentWardrobeData) {
        super(tabId, parent, false);
        this.entityPlayer = entityPlayer;
        this.propsPlayerSkinData = propsPlayerSkinData;
        this.equipmentWardrobeData = equipmentWardrobeData;
        
        this.armourOverride = equipmentWardrobeData.armourOverride;
        this.headOverlay = equipmentWardrobeData.headOverlay;
        this.limitLimbs = equipmentWardrobeData.limitLimbs;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        armourOverrideCheck = new GuiCheckBox[4];
        armourOverrideCheck[0] = new GuiCheckBox(2, 68, 17, GuiHelper.getLocalizedControlName(guiName, "renderHeadArmour"), !armourOverride.get(0));
        armourOverrideCheck[1] = new GuiCheckBox(3, 68, 37, GuiHelper.getLocalizedControlName(guiName, "renderChestArmour"), !armourOverride.get(1));
        armourOverrideCheck[2] = new GuiCheckBox(4, 68, 75, GuiHelper.getLocalizedControlName(guiName, "renderLegArmour"), !armourOverride.get(2));
        armourOverrideCheck[3] = new GuiCheckBox(5, 68, 94, GuiHelper.getLocalizedControlName(guiName, "renderFootArmour"), !armourOverride.get(3));
        
        overlayOverrideCheck = new GuiCheckBox[1];
        overlayOverrideCheck[0] = new GuiCheckBox(6, 68, 26, GuiHelper.getLocalizedControlName(guiName, "renderHeadOverlay"), !headOverlay);
        
        limitLimbsCheck = new GuiCheckBox(7, 68, 56, GuiHelper.getLocalizedControlName(guiName, "limitLimbMovement"), limitLimbs);
        
        buttonList.add(overlayOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[1]);
        buttonList.add(armourOverrideCheck[2]);
        buttonList.add(armourOverrideCheck[3]);
        buttonList.add(limitLimbsCheck);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiCheckBox) {
            headOverlay = !overlayOverrideCheck[0].isChecked();
            for (int i = 0; i < 4; i++) {
                armourOverride.set(i, !armourOverrideCheck[i].isChecked());
            }
        }
        
        if (button.id >= 1) {
            equipmentWardrobeData.headOverlay = headOverlay;
            equipmentWardrobeData.armourOverride = armourOverride;
            equipmentWardrobeData.limitLimbs = limitLimbsCheck.isChecked();
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(equipmentWardrobeData));
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        
        //Top half of GUI. (active tab)
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);
        
        // Draw player preview.
        ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY);
    }
}
