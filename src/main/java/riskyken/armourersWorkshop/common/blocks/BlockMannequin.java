package riskyken.armourersWorkshop.common.blocks;

import java.util.Random;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.HolidayHelper;

public class BlockMannequin extends AbstractModBlockContainer {
    
    public static final PropertyEnum<EnumPartType> PART = PropertyEnum.<EnumPartType>create("part", EnumPartType.class);
    private static final AxisAlignedBB MANNEQUIN_AABB = new AxisAlignedBB(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
    private static DamageSource victoriousDamage = new DamageSource("victorious");
    private static final String TAG_OWNER = "owner";
    private final boolean isValentins;
    
    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.ROCK, SoundType.METAL, true);
        setLightOpacity(0);
        isValentins = HolidayHelper.valentins.isHolidayActive();
        setDefaultState(this.blockState.getBaseState().withProperty(PART, EnumPartType.BOTTOM));
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(PART) == EnumPartType.BOTTOM) {
            return new AxisAlignedBB(0.1F, 0, 0.1F, 0.9F, 1.9F, 0.9F);
        }
        if (state.getValue(PART) == EnumPartType.TOP) {
            return new AxisAlignedBB(0.1F, -1, 0.1F, 0.9F, 0.9F, 0.9F);
        }
        return MANNEQUIN_AABB;
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor_double((double)(placer.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
            if (!worldIn.isRemote) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    GameProfile gameProfile = null;
                    if (compound.hasKey(TAG_OWNER, 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
                        ((TileEntityMannequin)te).setGameProfile(gameProfile);
                    }
                }
            }
        }
        worldIn.setBlockState(pos.offset(EnumFacing.UP), blockState.getBaseState().withProperty(PART, EnumPartType.TOP), 2);
        //world.setBlock(x, y + 1, z, this, 1, 2);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isTopOfMannequin(worldIn, pos)) {
            if (isValentins) {
                if (rand.nextFloat() * 100 > 75) {
                    //worldIn.spawnParticle(EnumParticleTypes.HEART, pos.getX() - 1 + rand.nextFloat() * 3F, pos.getY() - 1D, pos.getZ() - 1 + rand.nextFloat() * 3F, 0, 0, 0, new int[0]);
                    //worldIn.spawnParticle("heart", x + 0.2D + rand.nextFloat() * 0.6F, y + 1D, z + 0.2D + rand.nextFloat() * 0.6F, 0, 0, 0);
                }
            }
            TileEntityMannequin te = getMannequinTileEntity(worldIn, pos);
            if (te != null && te.isRenderExtras()) {
                if (te.hasSpecialRender()) {
                    for (int i = 0; i < 4; i++) {
                        /*
                        //ParticleSpell.AmbientMobFactory.
                        Particle entityfx = new ParticleSpell(worldIn, pos.getX() - 1 + rand.nextFloat() * 3F, pos.getY() - 1D, pos.getZ() - 1 + rand.nextFloat() * 3F, 0, 0, 0);
                        ((ParticleSpell)entityfx).setBaseSpellTextureIndex(144);
                        float[] colour = te.getSpecialRenderColour();
                        entityfx.setRBGColorF(colour[0], colour[1], colour[2]);
                        Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
                        */
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public void convertToDoll(World world, BlockPos pos) {
        if (isTopOfMannequin(world, pos)) {
            convertToDoll(world, pos.add(0, -1, 0));
            return;
        }
        
        if (world.getBlockState(pos).getBlock() == this) {
            TileEntityMannequin te = getMannequinTileEntity(world, pos);
            if (te != null) {
                te.setDropItems(false);
                NBTTagCompound compound = new NBTTagCompound();
                te.writeCommonToNBT(compound);
                te.writeItemsToNBT(compound);
                world.setBlockToAir(pos.add(0, 1, 0));
                world.setBlockState(pos, ModBlocks.doll.getDefaultState(), 3);
                TileEntity newTe = world.getTileEntity(pos);
                if (newTe != null && newTe instanceof TileEntityMannequin) {
                    ((TileEntityMannequin)newTe).readCommonFromNBT(compound);
                    ((TileEntityMannequin)newTe).readItemsFromNBT(compound);
                    ((TileEntityMannequin)newTe).setDoll(true);
                }
            }
        }
    }
    
    public TileEntityMannequin getMannequinTileEntity(World world, BlockPos pos) {
        if (isTopOfMannequin(world, pos)) {
            pos = pos.add(0, -1, 0);
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            return (TileEntityMannequin) te;
        }
        return null;
    }
    
    public boolean isTopOfMannequin(World world, BlockPos pos) {
        return isTopOfMannequin(world.getBlockState(pos));
    }
    
    public boolean isTopOfMannequin(IBlockState blockState) {
        return blockState.getValue(PART) == EnumPartType.TOP;
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (world.isRemote) {
            return false;
        }
        
        TileEntityMannequin te = getMannequinTileEntity(world, pos);
        if (te != null) {
            int rotation = ((TileEntityMannequin)te).getRotation();
            rotation++;
            if (rotation > 15) {
                rotation = 0;
            }
            ((TileEntityMannequin)te).setRotation(rotation);
        }
        return true;
    }
    /*
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        super.onEntityCollidedWithBlock(world, x, y, z, entity);
        if (world.isRemote) {
            return;
        }
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase entityLiving = (EntityLivingBase) entity;
        
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != 1) {
            return;
        }
        
        if (entityLiving.posY != y + (double)0.9F) {
            return;
        }
        
        if (entityLiving.posX < x + 0.2F | entityLiving.posX > x + 0.8F) {
            return;
        }
        
        if (entityLiving.posZ < z + 0.2F | entityLiving.posZ > z + 0.8F) {
            return;
        }
        
        TileEntity te = world.getTileEntity(x, y - 1, z);
        if (te != null && te instanceof TileEntityMannequin) {
            TileEntityMannequin teMan = (TileEntityMannequin) te;
            if (teMan.getGameProfile() != null) {
                if (teMan.getGameProfile().getName().equals("victorious3")) {
                    entityLiving.attackEntityFrom(victoriousDamage, 2.0F);
                }
            }
        }
    }
    */
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(ModBlocks.mannequin, 1);
        TileEntityMannequin te = getMannequinTileEntity(world, pos);
        if (te != null) {
            if (te.getGameProfile() != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, te.getGameProfile());
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
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos, side, heldItem)) {
            return false;
        }
        if (!worldIn.isRemote) {
            if (playerIn.inventory.getCurrentItem() != null) {
                if (playerIn.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
                    return false;
                }
                if (playerIn.inventory.getCurrentItem().getItem() == ModItems.paintbrush) {
                    return false;
                }
            }
            
            if (heldItem != null && heldItem.getItem() == Items.NAME_TAG) {
                TileEntity te = getMannequinTileEntity(worldIn, pos);
                if (te != null && te instanceof TileEntityMannequin) {
                    if (heldItem.getItem() == Items.NAME_TAG) {
                        ((TileEntityMannequin)te).setOwner(heldItem);
                    }
                }
            } else {
                FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.MANNEQUIN, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        if (playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
            return false;
        }
        return true;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        if (blockIn == this) {
            switch (state.getValue(PART)) {
            case TOP:
                if (worldIn.getBlockState(pos.up()).getBlock() != this) {
                    worldIn.setBlockToAir(pos);
                }
                break;
            case BOTTOM:
                if (worldIn.getBlockState(pos.down()).getBlock() != this) {
                    worldIn.setBlockToAir(pos);
                }
                break;
            }
        }
    }
    
    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 0;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(PART) == EnumPartType.BOTTOM) {
            return new TileEntityMannequin(false);
        }
        return null;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if (meta == 0) {
            
        }
        return null;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {PART});
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(PART) == EnumPartType.TOP) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
        case 0:
            return this.blockState.getBaseState().withProperty(PART, EnumPartType.BOTTOM);
        case 1:
            return this.blockState.getBaseState().withProperty(PART, EnumPartType.TOP);
        default:
            return getDefaultState();
        }
    }
    
    public static enum EnumPartType implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;
        
        private EnumPartType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
