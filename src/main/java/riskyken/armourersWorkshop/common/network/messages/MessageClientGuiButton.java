package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiButton implements IMessage, IMessageHandler<MessageClientGuiButton, IMessage> {

    byte buttonId;
    
    public MessageClientGuiButton() {}
    
    public MessageClientGuiButton(byte buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(buttonId);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiButton message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;

        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourerBrain armourerBrain = ((ContainerArmourer) container).getTileEntity();
            
            if (message.buttonId >= 0 & message.buttonId < SkinTypeRegistry.INSTANCE.getNumberOfSkinRegistered()) {
                ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(message.buttonId);
                armourerBrain.setSkinType(skinType);
            }
            if (message.buttonId == 14) {
                armourerBrain.loadArmourItem(player);
            }
            if (message.buttonId == 7) {
                armourerBrain.toggleGuides();
            }
            if (message.buttonId == 9) {
                armourerBrain.toggleOverlay();
            }
            if (message.buttonId == 10) {
                armourerBrain.clearArmourCubes();
            }
            if (message.buttonId == 11) {
                //armourerBrain.cloneToSide(ForgeDirection.WEST);
            }
            if (message.buttonId == 12) {
                //armourerBrain.cloneToSide(ForgeDirection.EAST);
            }
        }
        
        if (container != null && container instanceof ContainerMiniArmourerBuilding) {
            TileEntityMiniArmourer miniArmourer = ((ContainerMiniArmourerBuilding) container).getTileEntity();
            ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(message.buttonId);
            miniArmourer.setSkinType(skinType);
        }
        
        if (container != null && container instanceof ContainerMiniArmourer) {
            TileEntityMiniArmourer miniArmourer = ((ContainerMiniArmourer) container).getTileEntity();
        }
        
        return null;
    }
}
