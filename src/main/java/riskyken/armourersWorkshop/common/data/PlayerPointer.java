package riskyken.armourersWorkshop.common.data;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.ByteBufUtils;

public class PlayerPointer {
    
    private static final boolean USE_UUID_TO_SYNC = false;
    private UUID uuid = null;
    private String name = null;
    
    public PlayerPointer(EntityPlayer entityPlayer) {
        if (USE_UUID_TO_SYNC) {
            this.uuid = entityPlayer.getGameProfile().getId();
        } else {
            this.name = entityPlayer.getGameProfile().getName();
        }
    }
    
    public PlayerPointer(GameProfile gameProfile) {
        if (USE_UUID_TO_SYNC) {
            this.uuid = gameProfile.getId();
        } else {
            this.name = gameProfile.getName();
        }
    }
    
    public PlayerPointer(ByteBuf buf) {
        readFromByteBuffer(buf);
    }
    
    private void readFromByteBuffer(ByteBuf buf) {
        if (USE_UUID_TO_SYNC) {
            this.uuid = ByteBufHelper.readUUID(buf);
        } else {
            this.name = ByteBufUtils.readUTF8String(buf);
        }
    }
    
    public void writeToByteBuffer(ByteBuf buf) {
        if (USE_UUID_TO_SYNC) {
            ByteBufHelper.writeUUID(buf, this.uuid);
        } else {
            ByteBufUtils.writeUTF8String(buf, this.name);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerPointer other = (PlayerPointer) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }
}
