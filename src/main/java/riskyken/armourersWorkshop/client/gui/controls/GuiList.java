package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiList extends Gui {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    
    /** Local copy of Minecraft */
    protected final Minecraft mc;
    /** Local copy of the font renderer */
    protected final FontRenderer fontRenderer;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected final int slotHeight;
    public final boolean enabled;
    public final boolean visible;
    
    protected List<IGuiListItem> listItems;
    
    public GuiList(int x, int y, int width, int height, int slotHeight) {
        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.slotHeight = slotHeight;
        listItems = new ArrayList<IGuiListItem>();
        this.enabled = true;
        this.visible = true;
    }
    
    public void clearList() {
        listItems.clear();
    }
    
    public void addListItem(IGuiListItem item) {
        listItems.add(item);
    }
    
    public void drawList(int mouseX, int mouseY, float tickTime) {
        if (!this.visible) { return; }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //mc.renderEngine.bindTexture(icons);
        //this.drawTexturedModalRect(x, y, 0, 0, width, height);
        
        for (int i = 0; i < listItems.size(); i++) {
            listItems.get(i).drawListItem(fontRenderer, x, y + i * slotHeight, 0, 0);
        }
    }

    public IGuiListItem getListEntry(int index) {
        return this.listItems.get(index);
    }

    protected int getSize() {
        return this.listItems.size();
    }
}
