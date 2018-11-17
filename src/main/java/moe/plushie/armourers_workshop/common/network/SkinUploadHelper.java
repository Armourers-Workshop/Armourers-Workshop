package moe.plushie.armourers_workshop.common.network;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinLibrary;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientSkinPart;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

/**
 * Helps clients upload skins onto servers. Skin will
 * be split into multiple packet if needed.
 * @author RiskyKen
 *
 */
public final class SkinUploadHelper {
    
    private static final HashMap<Integer, byte[]> unfinishedSkins = new HashMap<Integer, byte[]>();
    //Forge packet limit is 32k
    private static final int MAX_PACKET_SIZE = 30000;
    
    public static void uploadSkinToServer(Skin skin) {
        if (!ConfigHandler.allowUploadingSkins) {
            return;
        }
        skin.requestId = new SkinIdentifier(skin);
        ModLogger.log("Uploading skin to server: " + skin);
        byte[] skinData = ByteBufHelper.convertSkinToByteArray(skin);
        
        ArrayList<MessageClientSkinPart> packetQueue = new ArrayList<MessageClientSkinPart>();
        
        int packetsNeeded = (int) Math.ceil((double)skinData.length / (double)MAX_PACKET_SIZE);
        int bytesLeftToSend = skinData.length;
        int bytesSent = 0;
        
        for (int i = 0; i < packetsNeeded; i++) {
            boolean lastPacket = i == packetsNeeded - 1;
            byte[] messageData;
            if (lastPacket) {
                messageData = new byte[bytesLeftToSend];
            } else {
                messageData = new byte[MAX_PACKET_SIZE];
            }
            System.arraycopy(skinData, bytesSent, messageData, 0, messageData.length);
            MessageClientSkinPart skinMessage = new MessageClientSkinPart(skin.lightHash(), (byte) i, messageData);
            packetQueue.add(skinMessage);
            bytesLeftToSend -= messageData.length;
            bytesSent += messageData.length;
        }
        
        for (int i = 0; i < packetQueue.size(); i++) {
            PacketHandler.networkWrapper.sendToServer(packetQueue.get(i));
        }
    }
    
    public static void gotSkinPartFromClient(int skinId, byte packetId, byte[] skinData, EntityPlayerMP player) {
        boolean lastPacket = skinData.length < MAX_PACKET_SIZE;
        byte[] oldSkinData = unfinishedSkins.get(skinId);
        
        byte[] newSkinData = null;
        if (oldSkinData != null) {
            newSkinData = ArrayUtils.addAll(oldSkinData, skinData);
            unfinishedSkins.remove(skinId);
        } else {
            newSkinData = skinData;
        }
        
        if (!lastPacket) {
            unfinishedSkins.put(skinId, newSkinData);
        } else {
            Skin skin = ByteBufHelper.convertByteArrayToSkin(newSkinData);
            ModLogger.log("Downloaded skin " + skin + " from client " + player);
            Container container = player.openContainer;
            
            if (!ConfigHandler.allowUploadingSkins) {
                return;
            }
            
            if (container != null && container instanceof ContainerSkinLibrary) {
                TileEntitySkinLibrary te = ((ContainerSkinLibrary) container).getTileEntity();
                te.loadClientSkin(skin, player);
            }
        }
    }
}
