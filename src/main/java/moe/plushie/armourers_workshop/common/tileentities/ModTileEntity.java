package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.common.property.IPropertyHolder;
import moe.plushie.armourers_workshop.common.property.TileProperty;
import moe.plushie.armourers_workshop.common.property.TilePropertyManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ModTileEntity extends TileEntity implements IPropertyHolder {

    private final ArrayList<TileProperty<?>> tileProperties;
    private boolean sync = true;

    public ModTileEntity() {
        tileProperties = new ArrayList<TileProperty<?>>();
    }

    @Override
    public void registerProperty(TileProperty<?> property) {
        tileProperties.add(property);
    }
    
    @Override
    public void onPropertyChanged(TileProperty<?> property) {
        markDirty();
        if (property.isSync()) {
            syncWithClients();
        }
    }
    
    public void readPropsFromCompound(NBTTagCompound compound) {
        for (TileProperty<?> property : tileProperties) {
            TilePropertyManager.INSTANCE.readPropFromCompound(property, compound);
        }
    }
    
    public NBTTagCompound writePropsToCompound(NBTTagCompound compound) {
        for (TileProperty<?> property : tileProperties) {
            TilePropertyManager.INSTANCE.writePropToCompound(property, compound);
        }
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        readPropsFromCompound(compound);
        super.readFromNBT(compound);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        writePropsToCompound(compound);
        return super.writeToNBT(compound);
    }
    
    public void enableSync() {
        sync = true;
    }

    public void disableSync() {
        sync = false;
    }

    /**
     * Sync the tile entity with the clients.
     */
    public void syncWithClients() {
        if (!sync) {
            return;
        }
        if (getWorld() == null) {
            return;
        }
        if (!getWorld().isRemote) {
            syncWithNearbyPlayers(this);
        } else {
            getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
        }
    }

    /**
     * Marks the tile entity as dirty and sync it with the clients.
     */
    public void dirtySync() {
        markDirty();
        syncWithClients();
    }

    public static void syncWithNearbyPlayers(TileEntity tileEntity) {
        if (tileEntity.getWorld() == null) {
            return;
        }
        World world = tileEntity.getWorld();
        List<EntityPlayer> players = world.playerEntities;
        for (EntityPlayer player : players) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                if (tileEntity.getDistanceSq(mp.posX, mp.posY, mp.posZ) < 64) {
                    mp.connection.sendPacket(tileEntity.getUpdatePacket());
                }
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
