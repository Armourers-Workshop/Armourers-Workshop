package moe.plushie.armourers_workshop.common.blocks;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.items.ItemDebugTool.IDebug;
import moe.plushie.armourers_workshop.common.items.block.ItemBlockMannequin;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.HolidayHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockMannequin extends AbstractModBlockContainer implements IDebug {

    public static DamageSource victoriousDamage = new DamageSource("victorious");
    public static GameProfile vicProfile = new GameProfile(UUID.fromString("b027a4f4-d480-426c-84a3-a9cb029f4b72"), "VicNightfall");
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private final boolean isValentins;
    
    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.ROCK, SoundType.METAL, true);
        setLightOpacity(0);
        //setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
        isValentins = HolidayHelper.valentins.isHolidayActive();
        setSortPriority(199);
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntityMannequin te = getMannequinTileEntity(worldIn, pos);
            if (te != null && te.getDropItems()) {
                ItemStack dropStack = te.getDropStack();
                UtilItems.spawnItemInWorld(worldIn, pos, dropStack);
                BlockUtils.dropInventoryBlocks(worldIn, pos);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor((double)(placer.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
            if (!worldIn.isRemote) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    GameProfile gameProfile = null;
                    if (compound.hasKey(TAG_OWNER, 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
                        ((TileEntityMannequin)te).setGameProfile(gameProfile);
                    }
                    if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                        ((TileEntityMannequin)te).setImageUrl(compound.getString(TAG_IMAGE_URL));
                    }
                }
            }
        }
        //world.setBlock(x, y + 1, z, this, 1, 2);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.1F, 0, 0.1F, 0.9F, 1.96F, 0.9F);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isTopOfMannequin(worldIn, pos)) {
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
            if (isValentins) {
                if (rand.nextFloat() * 100 > 75) {
                    Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.HEART.getParticleID(),
                            pos.getX() - 0.2F + rand.nextFloat() * 0.6F,
                            pos.getY(),
                            pos.getZ() - 0.2F + rand.nextFloat() * 0.6F,
                            0, 0, 0, null);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                }
            }
            TileEntityMannequin te = getMannequinTileEntity(worldIn, pos);
            if (te != null && te.isRenderExtras()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.getGameProfile());
                if (contributor != null & te.isVisible()) {
                    for (int i = 0; i < 4; i++) {
                        Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(),
                                pos.getX() - 1 + rand.nextFloat() * 3F,
                                pos.getY() - 1D,
                                pos.getZ() - 1 + rand.nextFloat() * 3F,
                                0, 0, 0, null);
                        particle.setRBGColorF((float)(contributor.r & 0xFF) / 255F, (float)(contributor.g & 0xFF) / 255F, (float)(contributor.b & 0xFF) / 255F);
                        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                    }
                }
            }
        }
    }
    /*
    public void convertToDoll(World world, int x, int y, int z) {
        if (isTopOfMannequin(world, x, y, z)) {
            Block block = world.getBlock(x, y - 1, z);
            if (block == this) {
                ((BlockMannequin)block).convertToDoll(world, x, y - 1, z);
            }
            return;
        }
        
        if (world.getBlock(x, y + 1, z) == this) {
            TileEntityMannequin te = getMannequinTileEntity(world, x, y, z);
            if (te != null) {
                te.setDropItems(false);
                NBTTagCompound compound = new NBTTagCompound();
                te.writeCommonToNBT(compound);
                te.writeItemsToNBT(compound);
                world.setBlockToAir(x, y + 1, z);
                world.setBlock(x, y, z, ModBlocks.doll, 0, 3);
                TileEntity newTe = world.getTileEntity(x, y, z);
                if (newTe != null && newTe instanceof TileEntityMannequin) {
                    ((TileEntityMannequin)newTe).readCommonFromNBT(compound);
                    ((TileEntityMannequin)newTe).readItemsFromNBT(compound);
                    ((TileEntityMannequin)newTe).setDoll(true);
                }
            }
        }
    }
    */
    public TileEntityMannequin getMannequinTileEntity(World world, BlockPos pos) {
        if (isTopOfMannequin(world, pos)) {
            pos = pos.down();
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            return (TileEntityMannequin) te;
        }
        return null;
    }
    
    public boolean isTopOfMannequin(World world, BlockPos pos) {
        return false;
        //return isTopOfMannequin(world.getBlockMetadata(pos));
    }
    
    public boolean isTopOfMannequin(int meta) {
        return meta == 1;
    }
    
    /*@Override
    public boolean rotateBlock(World world, int x, int y, int z, EnumFacing axis) {
        if (world.isRemote) {
            return false;
        }
        
        int meta = world.getBlockMetadata(x, y, z);
        int yOffset = 0;
        if (meta == 1) {
            yOffset = -1;
        }
        TileEntity te = world.getTileEntity(x, y + yOffset, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int rotation = ((TileEntityMannequin)te).getRotation();
            rotation++;
            if (rotation > 15) {
                rotation = 0;
            }
            ((TileEntityMannequin)te).setRotation(rotation);
        }
        return true;
    }*/

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
        /*
        int meta = worldIn.getBlockMetadata(x, y, z);
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
        
        TileEntity te = worldIn.getTileEntity(x, y - 1, z);
        if (te != null && te instanceof TileEntityMannequin) {
            TileEntityMannequin teMan = (TileEntityMannequin) te;
            if (teMan.getGameProfile() != null) {
                if (teMan.getGameProfile().getId() == vicProfile.getId()) {
                    entityLiving.attackEntityFrom(victoriousDamage, 2.0F);
                }
            }
        }
        */
    }
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(ModBlocks.mannequin, 1);
        /*
        int meta = world.getBlockMetadata(x, y, z);
        int yOffset = 0;
        if (meta == 1) {
            yOffset = -1;
        }
        TileEntity te = world.getTileEntity(x, y + yOffset, z);
        if (te != null && te instanceof TileEntityMannequin) {
            TileEntityMannequin teMan = (TileEntityMannequin) te;
            if (teMan.getGameProfile() != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, teMan.getGameProfile());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
            }
            if (!StringUtils.isNullOrEmpty(teMan.getImageUrl())) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString(TAG_IMAGE_URL, teMan.getImageUrl());
            }
        }
        */
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
        } else {
            setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
        }
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!worldIn.isRemote) {
            if (player.inventory.getCurrentItem() != null) {
                if (player.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
                    return false;
                }
                if (player.inventory.getCurrentItem().getItem() == ModItems.paintbrush) {
                    return false;
                }
            }
            int meta = world.getBlockMetadata(x, y, z);
            int yOffset = 0;
            if (meta == 1) {
                yOffset = -1;
            }
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == Items.NAME_TAG) {
                TileEntity te = world.getTileEntity(x, y + yOffset, z);;
                if (te != null && te instanceof TileEntityMannequin) {
                    if (stack.getItem() == Items.NAME_TAG) {
                        ((TileEntityMannequin)te).setOwner(player.getCurrentEquippedItem());
                    }
                }
            } else {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.MANNEQUIN, world, x, y + yOffset, z);
            }
        }
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
            return false;
        }
        return true;
    }
    
    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            if (world.getBlock(x, y + 1, z) != ModBlocks.mannequin) {
                world.setBlockToAir(x, y, z);
            }
        } else {
            if (world.getBlock(x, y - 1, z) != ModBlocks.mannequin) {
                world.setBlockToAir(x, y, z);
            }
        }
    }
    
    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 0;
    }
    

    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }*/
    
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if (meta == 0) {
            return new TileEntityMannequin(false);
        } else {
            return null;
        }
    }
    
    @Override
    public void getDebugHoverText(World world, BlockPos pos, ArrayList<String> textLines) {
        textLines.add("top=" + isTopOfMannequin(world, pos));
        TileEntityMannequin te = getMannequinTileEntity(world, pos);
        if (te != null && te.getGameProfile() != null) {
            textLines.add("profile=" + te.getGameProfile().getName() + ":" + te.getGameProfile().getId());
        } else {
            textLines.add("profile=null");
        }
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockMannequin(this).setRegistryName(getRegistryName()));
    }
}
