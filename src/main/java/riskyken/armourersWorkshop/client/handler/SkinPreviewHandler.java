package riskyken.armourersWorkshop.client.handler;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinItemRenderHelper;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

@SideOnly(Side.CLIENT)
public final class SkinPreviewHandler {
    
    private final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SKIN_PREVIEW);
    
    private SkinPointer lastSkinPointer;
    private List<String> lastList;
    
    public SkinPreviewHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onDrawScreenPre(DrawScreenEvent.Post event) {
        if (!ConfigHandlerClient.skinPreEnabled) {
            return;
        }
        if (!ConfigHandlerClient.skinPreLocFollowMouse) {
            return;
        }
        
        SkinPointer skinPointer = lastSkinPointer;
        List<String> list = lastList;
        
        lastSkinPointer = null;
        lastList = null;
        
        if (skinPointer != null & list != null) {
            Minecraft mc = Minecraft.getMinecraft();
            
            float skinPreSize = ConfigHandlerClient.skinPreSize;

            int[] toolTipSize = getTooltipSize(list, mc.currentScreen.width, mc.currentScreen.height, event.mouseX + 8, event.mouseY, mc.fontRenderer);
            int x = (int) (toolTipSize[0] - skinPreSize - 28);
            int y = (int) (toolTipSize[1] - 4);
            if (tooltipOnLeft(list, mc.currentScreen.width, mc.currentScreen.height, event.mouseX + 8, event.mouseY, mc.fontRenderer)) {
                x = (int) (toolTipSize[0] + toolTipSize[2] + 15);
            }
            if (y < 0) {
                y = 0;
            }
            if (y + skinPreSize > mc.currentScreen.height) {
                y = mc.currentScreen.height - (int)skinPreSize;
            }
            
            drawSkinBox(mc, x, y, skinPreSize, skinPointer);
        }
    }
    
    @SubscribeEvent
    public void onDrawScreenPost(DrawScreenEvent.Pre event) {
        if (!ConfigHandlerClient.skinPreEnabled) {
            return;
        }
        if (ConfigHandlerClient.skinPreLocFollowMouse) {
            return;
        }
        
        SkinPointer skinPointer = lastSkinPointer;
        List<String> list = lastList;
        
        lastSkinPointer = null;
        lastList = null;
        
        if (skinPointer != null & list != null) {
            Minecraft mc = Minecraft.getMinecraft();
            
            float skinPreSize = ConfigHandlerClient.skinPreSize;
            float skinPreLocHorizontal = ConfigHandlerClient.skinPreLocHorizontal;
            float skinPreLocVertical = ConfigHandlerClient.skinPreLocVertical;
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            
            double widthClip = sr.getScaledWidth_double() - skinPreSize;
            double heightClip = sr.getScaledHeight_double() - skinPreSize;
            
            int x = MathHelper.ceiling_double_int(widthClip * skinPreLocHorizontal);
            int y = MathHelper.ceiling_double_int(heightClip * skinPreLocVertical);
            
            drawSkinBox(mc, x, y, skinPreSize, skinPointer);
            
            skinPointer = null;
        }
    }
    
    private void drawSkinBox(Minecraft mc, int x, int y, float skinPreSize, SkinPointer skinPointer) {
        boolean skinPreDrawBackground = ConfigHandlerClient.skinPreDrawBackground;
        if (skinPreDrawBackground) {
            RenderHelper.disableStandardItemLighting();
            mc.renderEngine.bindTexture(TEXTURE);
            ModRenderHelper.enableAlphaBlend();
            GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, (int) skinPreSize, (int) skinPreSize, 62, 62, 4, 400);
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_NORMALIZE);
        RenderHelper.enableGUIStandardItemLighting();
        
        GL11.glTranslatef(-10F, -5F, 600);
        
        GL11.glTranslatef(
                (skinPreSize / 2F) + x,
                (skinPreSize / 2F) + y,
                0);
        
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
        GL11.glScalef(1.0F, -1.0F, 1.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        SkinItemRenderHelper.renderSkinAsItem(skinPointer, true, false, (int)skinPreSize, (int)skinPreSize);
        
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    
    private int[] getTooltipSize(List<String> list, int width, int height, int mouseX, int mouseY, FontRenderer font) {
        if (!list.isEmpty()) {
            int strWidth = 0;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);
                if (l > strWidth) {
                    strWidth = l;
                }
            }

            int xPos = mouseX + 12;
            int yPos = mouseY - 12;
            int strHeight = 8;

            if (list.size() > 1) {
                strHeight += 2 + (list.size() - 1) * 10;
            }

            if (xPos + strWidth > width) {
                xPos -= 28 + strWidth;
            }

            if (yPos + strHeight + 6 > height) {
                yPos = height - strHeight - 6;
            }
            if (xPos < 12) {
                xPos = 12;
            }
            if (yPos < 8) {
                yPos = 8;
            }
            return new int[] {xPos, yPos, strWidth, strHeight};
        }
        return null;
    }
    
    private boolean tooltipOnLeft(List<String> list, int width, int height, int mouseX, int mouseY, FontRenderer font) {
        if (!list.isEmpty()) {
            int strWidth = 0;
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                String s = (String)iterator.next();
                int l = font.getStringWidth(s);
                if (l > strWidth) {
                    strWidth = l;
                }
            }

            int xPos = mouseX + 12;
            int yPos = mouseY - 12;
            int strHeight = 8;

            if (list.size() > 1) {
                strHeight += 2 + (list.size() - 1) * 10;
            }
            if (xPos + strWidth > width) {
                return true;
            }
        }
        return false;
    }
    
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ConfigHandlerClient.skinPreEnabled) {
            lastSkinPointer = SkinNBTHelper.getSkinPointerFromStack(event.itemStack);
            lastList = event.toolTip;
        }
    }
}
