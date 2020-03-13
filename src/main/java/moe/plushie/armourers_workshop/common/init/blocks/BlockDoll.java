package moe.plushie.armourers_workshop.common.init.blocks;

import java.util.Random;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.render.item.RenderItemMannequin;
import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDoll extends AbstractModBlockContainer {

    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";

    private final boolean isValentins;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.2F, 0F, 0.2F, 0.8F, 0.95F, 0.8F);

    public BlockDoll() {
        super(LibBlockNames.DOLL, Material.CIRCUITS, SoundType.METAL, !ConfigHandler.hideDollFromCreativeTabs);
        setLightOpacity(0);
        // setBlockBounds(0.2F, 0F, 0.2F, 0.8F, 0.95F, 0.8F);
        isValentins = ModHolidays.VALENTINES.isHolidayActive();
        setSortPriority(198);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
            if (te != null) {
                te.disableSync();
                BlockUtils.dropInventoryBlocks(worldIn, te, pos);
                UtilItems.spawnItemInWorld(worldIn, pos, createItemStackFromTile(te));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor(placer.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
            ((TileEntityMannequin) te).PROP_ROTATION.set(l);
            if (!worldIn.isRemote) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    GameProfile gameProfile = null;
                    if (compound.hasKey(TAG_OWNER, 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
                        ((TileEntityMannequin) te).PROP_OWNER.set(gameProfile);
                    }
                    if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                        ((TileEntityMannequin) te).PROP_IMAGE_URL.set(compound.getString(TAG_IMAGE_URL));
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
        if (isValentins) {
            if (rand.nextFloat() * 100 > 75) {
                Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.HEART.getParticleID(), pos.getX() - 0.2F + rand.nextFloat() * 0.6F, pos.getY(), pos.getZ() - 0.2F + rand.nextFloat() * 0.6F, 0, 0, 0, null);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityMannequin) {
            TileEntityMannequin te = (TileEntityMannequin) tileEntity;
            if (te != null && te.PROP_RENDER_EXTRAS.get()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.PROP_OWNER.get());
                if (contributor != null & te.PROP_VISIBLE.get()) {
                    for (int i = 0; i < 6; i++) {
                        Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), pos.getX() + rand.nextFloat() * 1F, pos.getY(), pos.getZ() + rand.nextFloat() * 1F, 0, 0, 0, null);
                        particle.setRBGColorF((contributor.r & 0xFF) / 255F, (contributor.g & 0xFF) / 255F, (contributor.b & 0xFF) / 255F);
                        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                    }
                }
            }
        }
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (world.isRemote) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityMannequin) {
            int rotation = ((TileEntityMannequin) te).PROP_ROTATION.get();
            rotation++;
            if (rotation > 15) {
                rotation = 0;
            }
            ((TileEntityMannequin) te).PROP_ROTATION.set(rotation);
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntityMannequin te = getTileEntity(world, pos, TileEntityMannequin.class);
        return createItemStackFromTile(te);
    }

    private ItemStack createItemStackFromTile(TileEntityMannequin te) {
        ItemStack stack = new ItemStack(this, 1);
        if (te != null) {
            if (te.PROP_OWNER.get() != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, te.PROP_OWNER.get());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
            }
            if (!StringUtils.isNullOrEmpty(te.PROP_IMAGE_URL.get())) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString(TAG_IMAGE_URL, te.PROP_IMAGE_URL.get());
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos, facing, playerIn.getHeldItem(hand))) {
            return false;
        }
        openGui(playerIn, EnumGuiId.MANNEQUIN, worldIn, pos, state, facing);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityMannequin(true);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        super.registerModels();
        Item.getItemFromBlock(this).setTileEntityItemStackRenderer(new RenderItemMannequin());
    }
    
    public void convertToEntity(World world, BlockPos pos) {
        TileEntityMannequin tileEntity = getTileEntity(world, pos, TileEntityMannequin.class);
        if (tileEntity != null) {
            TextureData textureData = new TextureData();
            if (tileEntity.PROP_OWNER.get() != null && tileEntity.PROP_TEXTURE_TYPE.get() == TextureType.USER) {
                textureData = new TextureData(tileEntity.PROP_OWNER.get());
            }
            if (!StringUtils.isNullOrEmpty(tileEntity.PROP_IMAGE_URL.get()) && tileEntity.PROP_TEXTURE_TYPE.get() == TextureType.URL) {
                textureData = new TextureData(tileEntity.PROP_IMAGE_URL.get());
            }

            float offsetX = tileEntity.PROP_OFFSET_X.get();
            float offsetY = tileEntity.PROP_OFFSET_Y.get();
            float offsetZ = tileEntity.PROP_OFFSET_Z.get();

            EntityMannequin entityMannequin = new EntityMannequin(world);
            entityMannequin.setPosition(pos.getX() + 0.5F + offsetX, pos.getY() + offsetY, pos.getZ() + 0.5F + offsetZ);
            entityMannequin.setRotation(tileEntity.PROP_ROTATION.get() * 22.5F + 180);
            entityMannequin.setScale(0.5F);
            world.spawnEntity(entityMannequin);
            entityMannequin.setTextureData(textureData, true);
            
            world.removeTileEntity(pos);
            
            world.setBlockToAir(pos);
            if (world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == this) {
                world.setBlockToAir(pos.offset(EnumFacing.UP));
            }
        }
    }
}
