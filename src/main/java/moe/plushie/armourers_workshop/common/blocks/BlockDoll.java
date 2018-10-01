package moe.plushie.armourers_workshop.common.blocks;

import java.util.Random;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.HolidayHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDoll extends AbstractModBlockContainer {

    private static final String TAG_OWNER = "owner";
    private final boolean isValentins;
    
    public BlockDoll() {
        super(LibBlockNames.DOLL, Material.ROCK, SoundType.METAL, !ConfigHandler.hideDollFromCreativeTabs);
        setLightOpacity(0);
        //setBlockBounds(0.2F, 0F, 0.2F, 0.8F, 0.95F, 0.8F);
        isValentins = HolidayHelper.valentins.isHolidayActive();
        setSortPriority(198);
    }
    
    /*
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityMannequin) {
                ItemStack dropStack = ((TileEntityMannequin)te).getDropStack();
                UtilItems.spawnItemInWorld(world, x, y, z, dropStack);
            }
            BlockUtils.dropInventoryBlocks(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }
    
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor_double((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
            
            if (stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();
                GameProfile gameProfile = null;
                if (compound.hasKey(TAG_OWNER, 10)) {
                    gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
                    ((TileEntityMannequin)te).setGameProfile(gameProfile);
                }
            }
            
        }
    }
    */
    /*
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isValentins) {
            if (random.nextFloat() * 100 > 80) {
                world.spawnParticle("heart", x + 0.2D + random.nextFloat() * 0.6F, y + 1D, z + 0.2D + random.nextFloat() * 0.6F, 0, 0, 0);
            }
        }
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityMannequin) {
            TileEntityMannequin te = (TileEntityMannequin) tileEntity;
            if (te.isRenderExtras() & te.isVisible()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.getGameProfile());
                if (contributor != null) {
                    Particle entityfx = new ParticleSpell(world,  x + random.nextFloat() * 1F, y, z + random.nextFloat() * 1F, 0, 0, 0);
                    ((ParticleSpell)entityfx).setBaseSpellTextureIndex(144);
                    entityfx.setRBGColorF((float)(contributor.r & 0xFF) / 255F, (float)(contributor.g & 0xFF) / 255F, (float)(contributor.b & 0xFF) / 255F);
                    Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
                }
            }
        }
    }
    */
    /*
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, EnumFacing axis) {
        if (world.isRemote) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int rotation = ((TileEntityMannequin)te).getRotation();
            rotation++;
            if (rotation > 15) {
                rotation = 0;
            }
            ((TileEntityMannequin)te).setRotation(rotation);
        }
        return true;
    }
    */
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(ModBlocks.doll, 1);
        int meta = 0;// world.getBlockMetadata(x, y, z);
        int yOffset = 0;
        if (meta == 1) {
            yOffset = -1;
        }
        TileEntity te = world.getTileEntity(pos);;
        if (te != null && te instanceof TileEntityMannequin) {
            TileEntityMannequin teMan = (TileEntityMannequin) te;
            if (teMan.getGameProfile() != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, teMan.getGameProfile());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
            }
        }
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }
    /*
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == ModItems.mannequinTool) {
                return false;
            }
            if (stack != null && stack.getItem() == Items.NAME_TAG) {
                TileEntity te = world.getTileEntity(x, y, z);;
                if (te != null && te instanceof TileEntityMannequin) {
                    if (stack.getItem() == Items.NAME_TAG) {
                        ((TileEntityMannequin)te).setOwner(player.getCurrentEquippedItem());
                    }
                }
            } else {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.MANNEQUIN, world, x, y, z);
            }
        }
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
            return false;
        }
        return true;
    }
    */
    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 0;
    }
    
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityMannequin(true);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
