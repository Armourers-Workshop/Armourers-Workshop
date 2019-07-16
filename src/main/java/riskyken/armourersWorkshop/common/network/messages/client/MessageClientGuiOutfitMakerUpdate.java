package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.inventory.ContainerOutfitMaker;
import riskyken.armourersWorkshop.common.tileentities.TileEntityOutfitMaker;

public class MessageClientGuiOutfitMakerUpdate implements IMessage {

    private String name;
    private String flavour;

    public MessageClientGuiOutfitMakerUpdate() {
    }

    public MessageClientGuiOutfitMakerUpdate(String name, String flavour) {
        this.name = name;
        this.flavour = flavour;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, flavour);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        flavour = ByteBufUtils.readUTF8String(buf);
    }

    public static class Handler implements IMessageHandler<MessageClientGuiOutfitMakerUpdate, IMessage> {

        @Override
        public IMessage onMessage(MessageClientGuiOutfitMakerUpdate message, MessageContext ctx) {
            updateTile(ctx.getServerHandler().playerEntity, message.name, message.flavour);
            return null;
        }

        private void updateTile(EntityPlayer player, String name, String flavour) {
            if (player.openContainer != null && player.openContainer instanceof ContainerOutfitMaker) {
                TileEntityOutfitMaker tileEntity = ((ContainerOutfitMaker) player.openContainer).getTileEntity();
                tileEntity.setOutfitName(name);
                tileEntity.setOutfitFlavour(flavour);
            }
        }
    }
}
