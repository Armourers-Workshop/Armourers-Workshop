package moe.plushie.armourers_workshop.client.gui.mannequin;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerMannequin;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateTileProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.common.tileentities.property.TileProperty;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMannequin extends ModGuiContainer<ContainerMannequin> {

    private static final ResourceLocation TEXTURE_COMMON = new ResourceLocation(LibGuiResources.COMMON);

    public final TileEntityMannequin tileEntity;
    private final String inventoryName;

    public GuiMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(new ContainerMannequin(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getName();
        this.xSize = 256;
        this.ySize = 128;
    }

    public void updateProperty(TileProperty<?>... property) {
        MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(property);
        PacketHandler.networkWrapper.sendToServer(message);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        GuiButtonExt buttonExt = new GuiButtonExt(0, guiLeft + xSize / 2 - 50, guiTop + ySize - 28, 100, 18, GuiHelper.getLocalizedControlName(inventoryName, "button.update"));
        buttonList.add(buttonExt);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            MessageClientGuiButton message = new MessageClientGuiButton((byte) 0);
            PacketHandler.networkWrapper.sendToServer(message);
            this.mc.player.closeScreen();
        }
    }
    
    @Override
    public void drawDefaultBackground() {
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String append = null;
        if (tileEntity.PROP_OWNER.get() != null) {
            append = tileEntity.PROP_OWNER.get().getName();
        }
        if (tileEntity.PROP_DOLL.get()) {
            GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, "doll", append, 4210752);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntity.getName(), append, 4210752);
        }
        String updateText = GuiHelper.getLocalizedControlName(inventoryName, "label.update");
        fontRenderer.drawSplitString(updateText, 10, 30, xSize - 20, 0xFF0000);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_COMMON);
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, xSize, ySize, 128, 128, 4, zLevel);
    }

    @Override
    public String getName() {
        return tileEntity.getName();
    }
}
