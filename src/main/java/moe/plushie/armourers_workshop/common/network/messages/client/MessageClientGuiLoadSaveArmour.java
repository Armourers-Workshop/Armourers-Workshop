package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiLoadSaveArmour implements IMessage, IMessageHandler<MessageClientGuiLoadSaveArmour, IMessage> {

    private LibraryPacketType packetType;
    private String filename;
    private String filePath;
    private boolean publicList;
    private boolean trackFile;

    public MessageClientGuiLoadSaveArmour() {
    }

    public MessageClientGuiLoadSaveArmour(String filename, String filePath, LibraryPacketType packetType, boolean publicList, boolean trackFile) {
        this.packetType = packetType;
        this.filename = filename;
        this.filePath = filePath;
        this.publicList = publicList;
        this.trackFile = trackFile;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.packetType.ordinal());
        buf.writeBoolean(this.publicList);
        buf.writeBoolean(this.trackFile);
        switch (this.packetType) {
        case CLIENT_SAVE:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        case SERVER_LOAD:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        case SERVER_SAVE:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        default:
            break;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.packetType = LibraryPacketType.values()[buf.readByte()];
        this.publicList = buf.readBoolean();
        this.trackFile = buf.readBoolean();
        switch (this.packetType) {
        case CLIENT_SAVE:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        case SERVER_LOAD:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        case SERVER_SAVE:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        default:
            break;
        }
    }

    @Override
    public IMessage onMessage(MessageClientGuiLoadSaveArmour message, MessageContext ctx) {

        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (container instanceof ContainerSkinLibrary) {
                        TileEntitySkinLibrary te = ((ContainerSkinLibrary) container).getTileEntity();
                        switch (message.packetType) {
                        case CLIENT_SAVE:
                            te.sendSkinToClient(message.filename, message.filePath, player);
                            break;
                        case SERVER_LOAD:
                            te.loadSkin(message.filename, message.filePath, player, message.trackFile);
                            break;
                        case SERVER_SAVE:
                            te.saveSkin(message.filename, message.filePath, player, message.publicList);
                            break;
                        default:
                            break;
                        }
                    }
                }
            });
        }

        return null;
    }

    public enum LibraryPacketType {
        SERVER_LOAD, SERVER_SAVE, CLIENT_SAVE;
    }
}
