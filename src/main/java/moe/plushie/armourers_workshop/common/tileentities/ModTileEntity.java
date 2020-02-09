package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateTileProperties;
import moe.plushie.armourers_workshop.common.tileentities.property.IPropertyHolder;
import moe.plushie.armourers_workshop.common.tileentities.property.TileProperty;
import moe.plushie.armourers_workshop.common.tileentities.property.TilePropertyManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class ModTileEntity extends TileEntity implements IPropertyHolder {

    protected final ArrayList<TileProperty<?>> tileProperties;
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

    public void syncWithNearbyPlayers(TileEntity tileEntity) {
        if (tileEntity.getWorld() == null) {
            return;
        }
        if (!(tileEntity.getWorld() instanceof WorldServer)) {
            return;
        }
        WorldServer worldServer = (WorldServer) tileEntity.getWorld();
        PlayerChunkMapEntry chunk = worldServer.getPlayerChunkMap().getEntry(tileEntity.getPos().getX() >> 4, tileEntity.getPos().getZ() >> 4);
        SPacketUpdateTileEntity packet = tileEntity.getUpdatePacket();
        if (chunk != null & packet != null) {
            chunk.sendPacket(packet);
        }
    }
    
    public void updateProperty(TileProperty<?>... property) {
        MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(property);
        PacketHandler.networkWrapper.sendToServer(message);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
