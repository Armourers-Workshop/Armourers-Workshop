package riskyken.armourersWorkshop.client.gui.wardrobe.tab;

import java.util.BitSet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.wardrobe.GuiWardrobe;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.wardrobe.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.wardrobe.ExPropsPlayerSkinData;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeDisplaySettings extends GuiTabPanel {

    // private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);

    EntityPlayer entityPlayer;
    ExPropsPlayerSkinData propsPlayerSkinData;
    EquipmentWardrobeData equipmentWardrobeData;

    BitSet armourOverride;

    private GuiCheckBox[] armourOverrideCheck;

    String guiName = "equipmentWardrobe";

    public GuiTabWardrobeDisplaySettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, ExPropsPlayerSkinData propsPlayerSkinData, EquipmentWardrobeData equipmentWardrobeData) {
        super(tabId, parent, false);
        this.entityPlayer = entityPlayer;
        this.propsPlayerSkinData = propsPlayerSkinData;
        this.equipmentWardrobeData = equipmentWardrobeData;

        this.armourOverride = equipmentWardrobeData.armourOverride;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        armourOverrideCheck = new GuiCheckBox[4];
        armourOverrideCheck[0] = new GuiCheckBox(2, 83, 27, GuiHelper.getLocalizedControlName(guiName, "renderHeadArmour"), !armourOverride.get(0));
        armourOverrideCheck[1] = new GuiCheckBox(3, 83, 27 + 10, GuiHelper.getLocalizedControlName(guiName, "renderChestArmour"), !armourOverride.get(1));
        armourOverrideCheck[2] = new GuiCheckBox(4, 83, 27 + 20, GuiHelper.getLocalizedControlName(guiName, "renderLegArmour"), !armourOverride.get(2));
        armourOverrideCheck[3] = new GuiCheckBox(5, 83, 27 + 30, GuiHelper.getLocalizedControlName(guiName, "renderFootArmour"), !armourOverride.get(3));

        buttonList.add(armourOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[1]);
        buttonList.add(armourOverrideCheck[2]);
        buttonList.add(armourOverrideCheck[3]);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiCheckBox) {
            for (int i = 0; i < 4; i++) {
                armourOverride.set(i, !armourOverrideCheck[i].isChecked());
            }
        }

        if (button.id >= 1) {
            equipmentWardrobeData.armourOverride = armourOverride;
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(equipmentWardrobeData));
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);

        // Top half of GUI. (active tab)
        // this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);

        // Bottom half of GUI. (player inventory)
        // this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        // Draw player preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
