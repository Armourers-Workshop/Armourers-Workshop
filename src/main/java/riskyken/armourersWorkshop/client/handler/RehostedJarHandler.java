package riskyken.armourersWorkshop.client.handler;

import java.io.File;
import java.nio.charset.CharsetEncoder;

import org.apache.commons.io.Charsets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public final class RehostedJarHandler {
    
    private static final String STOP_MOD_REPOSTS_URL = "http://stopmodreposts.org/";
    
    private boolean validJar = false;
    
    private long lastMessagePost = 0;
    private long messagePostRate = 1000 * 60;
    
    public RehostedJarHandler(File jarFile, String originalName) {
        checkIfJarIsValid(jarFile, originalName);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    private void checkIfJarIsValid(File jarFile, String originalName) {
        if (jarFile == null) {
            return;
        }
        
        if (jarFile.getName().equals(originalName)) {
            // Names match.
            validJar = true;
            return;
        }
        
        CharsetEncoder asciiEncoder = Charsets.US_ASCII.newEncoder();
        if (!asciiEncoder.canEncode(jarFile.getName())) {
            // Most likely posted on a local language site.
            validJar = true;
            return;
        }
    }
    
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (validJar) {
            return;
        }
        if (LibModInfo.DEVELOPMENT_VERSION) {
            return;
        }
        if (event.side != Side.CLIENT) {
            return;
        }
        if (event.type != Type.PLAYER) {
            return;
        }
        if (event.phase != Phase.END) {
            return;
        }
        if (lastMessagePost + messagePostRate > System.currentTimeMillis()) {
            return;
        }
        lastMessagePost = System.currentTimeMillis();
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        
        ChatComponentTranslation downloadLink = new ChatComponentTranslation("chat.armourersworkshop:invalidJarDownload", (Object)null);
        downloadLink.getChatStyle().setUnderlined(true);
        downloadLink.getChatStyle().setColor(EnumChatFormatting.BLUE);
        downloadLink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation("chat.armourersworkshop:invalidJarDownloadTooltip", (Object)null)));
        downloadLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, LibModInfo.DOWNLOAD_URL));
        
        ChatComponentTranslation stopModRepostsLink = new ChatComponentTranslation("chat.armourersworkshop:invalidJarStopModReposts", (Object)null);
        stopModRepostsLink.getChatStyle().setUnderlined(true);
        stopModRepostsLink.getChatStyle().setColor(EnumChatFormatting.BLUE);
        stopModRepostsLink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation("chat.armourersworkshop:invalidJarStopModRepostsTooltip", (Object)null)));
        stopModRepostsLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, STOP_MOD_REPOSTS_URL));
        
        ChatComponentTranslation updateMessage = new ChatComponentTranslation("chat.armourersworkshop:invalidJar", downloadLink, stopModRepostsLink);
        updateMessage.getChatStyle().setColor(EnumChatFormatting.RED);
        player.addChatMessage(updateMessage);
    }
}
