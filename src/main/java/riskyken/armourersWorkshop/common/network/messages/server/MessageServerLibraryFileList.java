package riskyken.armourersWorkshop.common.network.messages.server;

import java.util.ArrayList;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;

public class MessageServerLibraryFileList implements IMessage, IMessageHandler<MessageServerLibraryFileList, IMessage> {

    ArrayList<LibraryFile> publicFileList;
    ArrayList<LibraryFile> privateFileList;
    
    public MessageServerLibraryFileList() {}
    
    public MessageServerLibraryFileList(ArrayList<LibraryFile> publicList, ArrayList<LibraryFile> privateList) {
        this.publicFileList = publicList;
        this.privateFileList = privateList;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(publicFileList.size());
        for (int i = 0; i < publicFileList.size(); i++) {
            publicFileList.get(i).writeToByteBuf(buf);
        }
        buf.writeInt(privateFileList.size());
        for (int i = 0; i < privateFileList.size(); i++) {
            privateFileList.get(i).writeToByteBuf(buf);
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        publicFileList = new ArrayList<LibraryFile>();
        for (int i = 0; i < size; i++) {
            publicFileList.add(LibraryFile.readFromByteBuf(buf));
        }
        size = buf.readInt();
        privateFileList = new ArrayList<LibraryFile>();
        for (int i = 0; i < size; i++) {
            privateFileList.add(LibraryFile.readFromByteBuf(buf));
        }
    }
    
    @Override
    public IMessage onMessage(MessageServerLibraryFileList message, MessageContext ctx) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Container container = player.openContainer;
        
        if (container != null & container instanceof ContainerArmourLibrary) {
            TileEntityArmourLibrary te = ((ContainerArmourLibrary)container).getTileEntity();
            te.setSkinFileList(message.publicFileList, message.privateFileList);
        }
        return null;
    }
}
