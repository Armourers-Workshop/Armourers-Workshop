package riskyken.armourersWorkshop.client.handler;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.render.SkinItemRenderHelper;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

@SideOnly(Side.CLIENT)
public final class SkinPreviewHandler {
    
    private final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SKIN_PREVIEW);
    
    public SkinPreviewHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onRender(DrawScreenEvent.Pre event) {
        if (!ConfigHandlerClient.skinPreEnabled) {
            return;
        }
        if (skinPointer != null) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.renderEngine.bindTexture(TEXTURE);

            boolean skinPreDrawBackground = ConfigHandlerClient.skinPreDrawBackground;
            float skinPreSize = ConfigHandlerClient.skinPreSize;
            float skinPreLocHorizontal = ConfigHandlerClient.skinPreLocHorizontal;
            float skinPreLocVertical = ConfigHandlerClient.skinPreLocVertical;
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            
            double widthClip = sr.getScaledWidth_double() - skinPreSize;
            double heightClip = sr.getScaledHeight_double() - skinPreSize;
            
            int x = MathHelper.ceiling_double_int(widthClip * skinPreLocHorizontal);
            int y = MathHelper.ceiling_double_int(heightClip * skinPreLocVertical);
            
            
            GL11.glColor4f(1F, 1F, 1F, 1F);
            if (skinPreDrawBackground) {
                RenderHelper.disableStandardItemLighting();
                GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, (int) skinPreSize, (int) skinPreSize, 62, 62, 4, 200);
            }
            
            
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_NORMALIZE);
            RenderHelper.enableGUIStandardItemLighting();
            
            GL11.glTranslatef(-10F, -5F, 500);
            
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
            skinPointer = null;
        }
    }
    
    private SkinPointer skinPointer;
    
    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        if (ConfigHandlerClient.skinPreEnabled) {
            skinPointer = SkinNBTHelper.getSkinPointerFromStack(event.itemStack);
        }
    }
}
