package riskyken.armourersWorkshop.common.network.messages.server;

import java.util.ArrayList;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.utils.ModLogger;

/**
 * Sent from the server to a client when they have the library GUI open
 * and file list needs updated.
 * 
 * @author RiskyKen
 *
 */
public class MessageServerLibraryFileList implements IMessage, IMessageHandler<MessageServerLibraryFileList, IMessage> {

    ArrayList<LibraryFile> fileList;
    LibraryFileType listType;
    
    public MessageServerLibraryFileList() {}
    
    public MessageServerLibraryFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType) {
        this.fileList = fileList;
        this.listType = listType;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fileList.size());
        for (int i = 0; i < fileList.size(); i++) {
            fileList.get(i).writeToByteBuf(buf);
        }
        buf.writeByte(listType.ordinal());
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        fileList = new ArrayList<LibraryFile>();
        for (int i = 0; i < size; i++) {
            fileList.add(LibraryFile.readFromByteBuf(buf));
        }
        listType = LibraryFileType.values()[buf.readByte()];
    }
    
    @Override
    public IMessage onMessage(MessageServerLibraryFileList message, MessageContext ctx) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Container container = player.openContainer;
        ModLogger.log("got file list type " + message.listType);
        ArmourersWorkshop.proxy.libraryManager.setFileList(message.fileList, message.listType);
        return null;
    }
}
