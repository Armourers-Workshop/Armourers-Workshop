package riskyken.armourersWorkshop.client.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;

@SideOnly(Side.CLIENT)
public class GuiToolOptions extends GuiScreen {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/toolOptions.png");
    private static final int MARGIN_TOP = 22;
    private static final int MARGIN_LEFT = 6;
    private static final int CONTROL_PADDING = 6;
    
    private final int guiWidth;
    private int guiHeight;
    protected int guiLeft;
    protected int guiTop;
    protected ItemStack stack;
    private String guiName;
    private final ArrayList<AbstractToolOption> toolOptionsList;
    
    public GuiToolOptions(ItemStack stack) {
        this.stack = stack;
        toolOptionsList = new ArrayList<AbstractToolOption>();
        ((IConfigurableTool)stack.getItem()).getToolOptions(toolOptionsList);
        guiWidth = 175;
        guiHeight = 61;
        this.guiName = stack.getDisplayName();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        guiLeft = width / 2 - guiWidth / 2;
        
        //Work out how tall the GUI needs to be.
        int controlHeight = MARGIN_TOP;
        for (int i = 0; i < toolOptionsList.size(); i++) {
            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
        }
        guiHeight = controlHeight;
        guiTop = height / 2 - guiHeight / 2;
        
        //Place the controls on the GUI.
        controlHeight = MARGIN_TOP;
        for (int i = 0; i < toolOptionsList.size(); i++) {
            GuiButton control = toolOptionsList.get(i).getGuiControl(i, guiLeft + MARGIN_LEFT, controlHeight + guiTop, stack.getTagCompound());
            buttonList.add(control);
            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, guiWidth, guiHeight, textureWidth, textureHeight, borderSize, zLevel);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        renderGuiTitle(fontRendererObj, guiName);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    private void renderGuiTitle(FontRenderer fontRenderer, String name) {
        int xPos = this.guiWidth / 2 - fontRenderer.getStringWidth(name) / 2;
        fontRenderer.drawString(name, this.guiLeft + xPos, this.guiTop + 6, 4210752);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToCompound(compound);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        for (int i = 0; i < toolOptionsList.size(); i++) {
            toolOptionsList.get(i).writeToNBT(compound, (GuiButton) buttonList.get(i));
        }
    }
}
