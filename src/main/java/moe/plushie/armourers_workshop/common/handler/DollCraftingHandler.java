package moe.plushie.armourers_workshop.common.handler;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DollCraftingHandler implements IWorldEventListener {

    public DollCraftingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event) {
        ModLogger.log(String.format("Adding WorldEventListener to world %s", event.getWorld().toString()));
        event.getWorld().addEventListener(this);
    }
    
    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    @Override
    public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
    }

    @Override
    public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        if (ConfigHandler.disableDollRecipe) {
            return;
        }
        World world = entityIn.getEntityWorld();
        if (!world.isRemote) {
            if (entityIn instanceof EntityFallingBlock) {
                if (((EntityFallingBlock)entityIn).getBlock().getBlock() == Blocks.ANVIL) {
                    BlockPos pos = entityIn.getPosition();
                    IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
                    // if (state.getBlock() == ModBlocks.MANNEQUIN) {
                    //     ((BlockMannequin)state.getBlock()).convertToDoll(world, pos);
                    // }
                }
            }
        }
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    }
}
