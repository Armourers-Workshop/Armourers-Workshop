package riskyken.armourersWorkshop.common.handler;

import net.minecraftforge.common.MinecraftForge;

public class DollCraftingHandler /*implements IWorldAccess*/ {

    public DollCraftingHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    /*
    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event) {
        ModLogger.log(String.format("Adding world access to world %s", event.world.toString()));
        event.world.addWorldAccess(this);
    }

    @Override
    public void markBlockForUpdate(int x, int y, int z) {
    }

    @Override
    public void markBlockForRenderUpdate(int x, int y, int z) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    }

    @Override
    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playSoundToNearExcept(EntityPlayer player, String soundName, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void spawnParticle(String particleType, double x, double y, double z, double velX, double velY, double velZ) {
    }

    @Override
    public void onEntityCreate(Entity entity) {
    }

    @Override
    public void onEntityDestroy(Entity entity) {
        World world = entity.worldObj;
        if (!world.isRemote) {
            if (entity instanceof EntityFallingBlock) {
                if (((EntityFallingBlock)entity).func_145805_f() == Blocks.anvil) {
                    int x = MathHelper.floor_double(entity.posX);
                    int y = MathHelper.floor_double(entity.posY) - 1;
                    int z = MathHelper.floor_double(entity.posZ);
                    Block block = world.getBlock(x, y, z);
                    if (block == ModBlocks.mannequin) {
                        ((BlockMannequin)block).convertToDoll(world, x, y, z);
                    }
                }
            }
        }
    }

    @Override
    public void playRecord(String recordName, int x, int y, int z) {
    }

    @Override
    public void broadcastSound(int p_82746_1_, int p_82746_2_, int p_82746_3_, int p_82746_4_, int p_82746_5_) {
    }

    @Override
    public void playAuxSFX(EntityPlayer p_72706_1_, int p_72706_2_, int p_72706_3_, int p_72706_4_, int p_72706_5_, int p_72706_6_) {
    }

    @Override
    public void destroyBlockPartially(int p_147587_1_, int p_147587_2_, int p_147587_3_, int p_147587_4_, int p_147587_5_) {
    }

    @Override
    public void onStaticEntitiesChanged() {
    }*/
}
