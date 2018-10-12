package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCapability;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeDyes extends GuiTabPanel {

    private EntityPlayer entityPlayer;
    private IEntitySkinCapability skinCapability;
    private IWardrobeCapability wardrobeCapability;
    
    public GuiTabWardrobeDyes(int tabId, GuiScreen parent, EntityPlayer entityPlayer, IEntitySkinCapability skinCapability, IWardrobeCapability wardrobeCapability) {
        super(tabId, parent, false);
        this.entityPlayer = entityPlayer;
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        
    }
}
