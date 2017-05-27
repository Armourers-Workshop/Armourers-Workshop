package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.library.LibraryFile;

public class MessageClientGuiSkinLibraryCommand implements IMessage, IMessageHandler<MessageClientGuiSkinLibraryCommand, IMessage> {

    private SkinLibraryCommand command;
    private LibraryFile file;
    private boolean publicList;
    
    public MessageClientGuiSkinLibraryCommand() {
    }
    
    public void delete(LibraryFile file, boolean publicList) {
        this.publicList = publicList;
        command = SkinLibraryCommand.DELETE;
        this.file = file;
    }
    
    public void newFolder(LibraryFile folder, boolean publicList) {
        this.publicList = publicList;
        command = SkinLibraryCommand.NEW_FOLDER;
        this.file = folder;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(command.ordinal());
        buf.writeBoolean(publicList);
        switch (command) {
        case DELETE:
            file.writeToByteBuf(buf);
            break;
        case NEW_FOLDER:
            file.writeToByteBuf(buf);
            break;
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        command = SkinLibraryCommand.values()[buf.readByte()];
        publicList = buf.readBoolean();
        switch (command) {
        case DELETE:
            file = LibraryFile.readFromByteBuf(buf); 
            break;
        case NEW_FOLDER:
            file = LibraryFile.readFromByteBuf(buf); 
            break;
        }
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSkinLibraryCommand message, MessageContext ctx) {
        ArmourersWorkshop.proxy.skinLibraryCommand(ctx.getServerHandler().playerEntity, message.command, message.file, message.publicList);
        return null;
    }
    
    public static enum SkinLibraryCommand {
        DELETE,
        NEW_FOLDER
        /*
        RELOAD_LIBRARY,
        RENAME_FOLDER,
        RENAME_SKIN*/
    }
}
