package moe.plushie.armourers_workshop.common.network.messages.server;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.library.LibraryFileType;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Container container = player.openContainer;
        ModLogger.log("got file list type " + message.listType);
        ArmourersWorkshop.getProxy().libraryManager.setFileList(message.fileList, message.listType);
        return null;
    }
}
