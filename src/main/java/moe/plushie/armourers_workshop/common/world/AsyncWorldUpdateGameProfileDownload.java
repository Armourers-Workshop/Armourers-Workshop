package moe.plushie.armourers_workshop.common.world;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.GameProfileCache.IGameProfileCallback;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AsyncWorldUpdateGameProfileDownload extends AsyncWorldUpdate implements IGameProfileCallback {

    private GameProfile gameProfile;
    
    public AsyncWorldUpdateGameProfileDownload(BlockPos pos, World world) {
        this(pos, world.provider.getDimension());
    }
    
    public AsyncWorldUpdateGameProfileDownload(BlockPos pos, int dimensionId) {
        super(pos, dimensionId);
    }
    
    @Override
    public void doUpdate(World world) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof IGameProfileCallback) {
            ((IGameProfileCallback)tileEntity).profileDownloaded(gameProfile);
        }
    }

    @Override
    public void profileDownloaded(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        SyncWorldUpdater.addWorldUpdate(this);
    }
}
