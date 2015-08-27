package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary.LibraryFile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerLibraryFileList implements IMessage, IMessageHandler<MessageServerLibraryFileList, IMessage> {

    ArrayList<LibraryFile> fileList;
    
    public MessageServerLibraryFileList() {}
    
    public MessageServerLibraryFileList(ArrayList<LibraryFile> fileList) {
        this.fileList = fileList;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fileList.size());
        for (int i = 0; i < fileList.size(); i++) {
            fileList.get(i).writeToByteBuf(buf);
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        fileList = new ArrayList<LibraryFile>();
        for (int i = 0; i < size; i++) {
            fileList.add(LibraryFile.readFromByteBuf(buf));
        }
    }
    
    @Override
    public IMessage onMessage(MessageServerLibraryFileList message, MessageContext ctx) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Container container = player.openContainer;
        
        if (container != null & container instanceof ContainerArmourLibrary) {
            TileEntityArmourLibrary te = ((ContainerArmourLibrary)container).getTileEntity();
            te.setSkinFileList(message.fileList);
        }
        return null;
    }
}
