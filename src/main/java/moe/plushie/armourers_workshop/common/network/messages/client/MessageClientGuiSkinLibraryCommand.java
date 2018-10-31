package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
        ArmourersWorkshop.getProxy().skinLibraryCommand(ctx.getServerHandler().player, message.command, message.file, message.publicList);
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
