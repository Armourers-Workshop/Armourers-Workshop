package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerHologramProjector;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;

public class MessageClientGuiHologramProjector implements IMessage, IMessageHandler<MessageClientGuiHologramProjector, IMessage> {

    private boolean hasOffsets = false;
    private int offsetX = 0;
    private int offsetY = 16;
    private int offsetZ = 0;
    
    private boolean hasAngle = false;
    private int angleX = 0;
    private int angleY = 16;
    private int angleZ = 0;
    
    private boolean hasRotationOffset = false;
    private int rotationOffsetX = 0;
    private int rotationOffsetY = 0;
    private int rotationOffsetZ = 0;
    
    private boolean hasRotationSpeed = false;
    private int rotationSpeedX = 0;
    private int rotationSpeedY = 0;
    private int rotationSpeedZ = 0;
    
    private boolean hasGlowing = false;
    private boolean glowing = false;
    
    public MessageClientGuiHologramProjector() {
    }
    
    public void setOffset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        hasOffsets = true;
    }
    
    public void setAngle(int x, int y, int z) {
        this.angleX = x;
        this.angleY = y;
        this.angleZ = z;
        hasAngle = true;
    }
    
    public void setRotationOffset(int x, int y, int z) {
        this.rotationOffsetX = x;
        this.rotationOffsetY = y;
        this.rotationOffsetZ = z;
        hasRotationOffset = true;
    }
    
    public void setRotationSpeedX(int x, int y, int z) {
        this.rotationSpeedX = x;
        this.rotationSpeedY = y;
        this.rotationSpeedZ = z;
        hasRotationSpeed = true;
    }
    
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        hasGlowing = true;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(hasOffsets);
        if (hasOffsets) {
            buf.writeInt(offsetX);
            buf.writeInt(offsetY);
            buf.writeInt(offsetZ);
        }
        
        buf.writeBoolean(hasAngle);
        if (hasAngle) {
            buf.writeInt(angleX);
            buf.writeInt(angleY);
            buf.writeInt(angleZ);
        }
        
        buf.writeBoolean(hasRotationOffset);
        if (hasRotationOffset) {
            buf.writeInt(rotationOffsetX);
            buf.writeInt(rotationOffsetY);
            buf.writeInt(rotationOffsetZ);
        }
        
        buf.writeBoolean(hasRotationSpeed);
        if (hasRotationSpeed) {
            buf.writeInt(rotationSpeedX);
            buf.writeInt(rotationSpeedY);
            buf.writeInt(rotationSpeedZ);
        }
        buf.writeBoolean(hasGlowing);
        if (hasGlowing) {
            buf.writeBoolean(glowing);
        }
     }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            offsetX = buf.readInt();
            offsetY = buf.readInt();
            offsetZ = buf.readInt();
            hasOffsets = true;
        }
        
        if (buf.readBoolean()) {
            angleX = buf.readInt();
            angleY = buf.readInt();
            angleZ = buf.readInt();
            hasAngle = true;
        }
        
        if (buf.readBoolean()) {
            rotationOffsetX = buf.readInt();
            rotationOffsetY = buf.readInt();
            rotationOffsetZ = buf.readInt();
            hasRotationOffset = true;
        }
        
        if (buf.readBoolean()) {
            rotationSpeedX = buf.readInt();
            rotationSpeedY = buf.readInt();
            rotationSpeedZ = buf.readInt();
            hasRotationSpeed = true;
        }
        if (buf.readBoolean()) {
            glowing = buf.readBoolean();
            hasGlowing = true;
        }
    }


    @Override
    public IMessage onMessage(MessageClientGuiHologramProjector message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) {
            return null;
        }
        
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerHologramProjector) {
            TileEntityHologramProjector tileEntity = ((ContainerHologramProjector)container).getTileEntity();
            if (message.hasOffsets) {
                tileEntity.setOffset(message.offsetX, message.offsetY, message.offsetZ);
            }
            if (message.hasAngle) {
                tileEntity.setAngle(message.angleX, message.angleY, message.angleZ);
            }
            if (message.hasRotationOffset) {
                tileEntity.setRotationOffset(message.rotationOffsetX, message.rotationOffsetY, message.rotationOffsetZ);
            }
            if (message.hasRotationSpeed) {
                tileEntity.setRotationSpeed(message.rotationSpeedX, message.rotationSpeedY, message.rotationSpeedZ);
            }
            if (message.hasGlowing) {
                tileEntity.setGlowing(message.glowing);
            }
        }
        return null;
    }
}
