package moe.plushie.armourers_workshop.client.handler;

import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class SkinPreviewHandler {
    
    private final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SKIN_PREVIEW);
    
    private SkinDescriptor lastSkinDescriptor;
    private List<String> lastList;
    
    public SkinPreviewHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onDrawScreenPost(DrawScreenEvent.Post event) {
        if (!ConfigHandlerClient.skinPreEnabled) {
            return;
        }
        if (!ConfigHandlerClient.skinPreLocFollowMouse) {
            return;
        }
        
        SkinDescriptor skinDescriptor = lastSkinDescriptor;
        List<String> list = lastList;
        
        lastSkinDescriptor = null;
        lastList = null;
        
        if (skinDescriptor != null & list != null) {
            Minecraft mc = Minecraft.getMinecraft();
            
            float skinPreSize = ConfigHandlerClient.skinPreSize;

            int[] toolTipSize = getTooltipSize(list, mc.currentScreen.width, mc.currentScreen.height, event.getMouseX() + 8, event.getMouseY(), mc.fontRenderer);
            int x = (int) (toolTipSize[0] - skinPreSize - 28);
            int y = toolTipSize[1] - 4;
            if (tooltipOnLeft(list, mc.currentScreen.width, mc.currentScreen.height, event.getMouseX() + 8, event.getMouseY(), mc.fontRenderer)) {
                x = toolTipSize[0] + toolTipSize[2] + 15;
            }
            if (y < 0) {
                y = 0;
            }
            if (y + skinPreSize > mc.currentScreen.height) {
                y = mc.currentScreen.height - (int)skinPreSize;
            }
            
            drawSkinBox(mc, x, y, skinPreSize, skinDescriptor);
        }
    }
    
    @SubscribeEvent
    public void onDrawScreenPre(DrawScreenEvent.Pre event) {
        if (!ConfigHandlerClient.skinPreEnabled) {
            return;
        }
        if (ConfigHandlerClient.skinPreLocFollowMouse) {
            return;
        }
        
        SkinDescriptor skinDescriptor = lastSkinDescriptor;
        List<String> list = lastList;
        
        lastSkinDescriptor = null;
        lastList = null;
        
        if (skinDescriptor != null & list != null) {
            Minecraft mc = Minecraft.getMinecraft();
            
            float skinPreSize = ConfigHandlerClient.skinPreSize;
            float skinPreLocHorizontal = ConfigHandlerClient.skinPreLocHorizontal;
            float skinPreLocVertical = ConfigHandlerClient.skinPreLocVertical;
            ScaledResolution sr = new ScaledResolution(mc);
            
            double widthClip = sr.getScaledWidth_double() - skinPreSize;
            double heightClip = sr.getScaledHeight_double() - skinPreSize;
            
            int x = MathHelper.ceil(widthClip * skinPreLocHorizontal);
            int y = MathHelper.ceil(heightClip * skinPreLocVertical);
            
            drawSkinBox(mc, x, y, skinPreSize, skinDescriptor);
            
            skinDescriptor = null;
        }
    }
    
    private void drawSkinBox(Minecraft mc, int x, int y, float skinPreSize, SkinDescriptor skinPointer) {
        boolean skinPreDrawBackground = ConfigHandlerClient.skinPreDrawBackground;
        if (skinPreDrawBackground & ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
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
            lastSkinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(event.getItemStack());
            lastList = event.getToolTip();
        }
    }
}
