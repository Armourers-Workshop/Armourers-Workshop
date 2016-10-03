package riskyken.armourersWorkshop.client.handler;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourersWorkshop.common.items.ModItems;

@SideOnly(Side.CLIENT)
public class BlockHighlightRenderHandler {

    public BlockHighlightRenderHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player  = mc.thePlayer;
        World world = player.worldObj;
        if (player.getHeldItem(EnumHand.MAIN_HAND) == null || player.getHeldItem(EnumHand.MAIN_HAND).getItem() != ModItems.debugTool) {
            return;
        }
        
        if (event.getType() != ElementType.TEXT) {
            return;
        }
        
        RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
        
        if (target != null && target.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        
        BlockPos pos = target.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        
        FontRenderer fontRenderer = mc.fontRendererObj;
        
        ArrayList<String> textLines = new ArrayList<String>();
        textLines.add("name: " + block.getLocalizedName());
        textLines.add("state: " + world.getBlockState(pos).toString());
        
        if (block instanceof IDebug) {
            IDebug debug = (IDebug) block;
            debug.getDebugHoverText(world, pos, textLines);
        }
        int centerX = event.getResolution().getScaledWidth() / 2;
        int centerY = event.getResolution().getScaledHeight() / 2;
        
        int longestLine = 0;
        
        for (int i = 0; i < textLines.size(); i++) {
            int sWidth = fontRenderer.getStringWidth(textLines.get(i));
            longestLine = Math.max(longestLine, sWidth);
        }
        
        for (int i = 0; i < textLines.size(); i++) {
            fontRenderer.drawStringWithShadow(textLines.get(i), centerX - longestLine / 2, 5 + fontRenderer.FONT_HEIGHT * i, 0xFFFFFFFF);
        }
    }
}
