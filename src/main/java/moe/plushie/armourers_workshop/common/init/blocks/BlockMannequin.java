package moe.plushie.armourers_workshop.common.init.blocks;

import java.util.Random;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.render.item.RenderItemMannequin;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.init.items.block.ItemBlockMannequin;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.statemap.StateMap;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockMannequin extends AbstractModBlockContainer {

    public static final PropertyEnum<EnumPartType> STATE_PART = PropertyEnum.<EnumPartType>create("part", EnumPartType.class);
    public static final PropertyInteger STATE_ROTATION = PropertyInteger.create("rotation", 0, 15);

    private static final AxisAlignedBB MANNEQUIN_AABB = new AxisAlignedBB(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
    private static final DamageSource VIC_DAMAGE = new DamageSource("victorious");
    private static final GameProfile VIC_PROFILE = new GameProfile(UUID.fromString("b027a4f4-d480-426c-84a3-a9cb029f4b72"), "VicNightfall");
    private static final GameProfile GAROAM_PROFILE = new GameProfile(UUID.fromString("31873a23-125e-4752-8607-0f1c3cb22c84"), "Garoam");

    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private final boolean isValentins;

    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.CIRCUITS, SoundType.METAL, true);
        setLightOpacity(0);
        isValentins = ModHolidays.VALENTINES.isHolidayActive();
        setSortPriority(199);
        setDefaultState(this.blockState.getBaseState().withProperty(STATE_PART, EnumPartType.BOTTOM));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE_PART, STATE_ROTATION });
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(STATE_PART) == EnumPartType.TOP) {
            return 1;
        }
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
        case 0:
            return this.blockState.getBaseState().withProperty(STATE_PART, EnumPartType.BOTTOM);
        case 1:
            return this.blockState.getBaseState().withProperty(STATE_PART, EnumPartType.TOP);
        default:
            return getDefaultState();
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
        if (te != null) {
            state = state.withProperty(STATE_ROTATION, te.PROP_ROTATION.get());
        }
        return state;
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

    public ItemStack getStackWithTexture(PlayerTexture playerTexture) {
        ItemStack result = new ItemStack(this);
        result.setTagCompound(new NBTTagCompound());
        playerTexture.writeToNBT(result.getTagCompound());
        return result;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(STATE_PART) == EnumPartType.BOTTOM) {
            return new AxisAlignedBB(0.1F, 0, 0.1F, 0.9F, 1.9F, 0.9F);
        }
        if (state.getValue(STATE_PART) == EnumPartType.TOP) {
            return new AxisAlignedBB(0.1F, -1, 0.1F, 0.9F, 0.9F, 0.9F);
        }
        return MANNEQUIN_AABB;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
        if (te != null) {
            int l = MathHelper.floor(placer.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
            te.PROP_ROTATION.set(l);
            if (!worldIn.isRemote) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    GameProfile gameProfile = null;
                    if (compound.hasKey(TAG_OWNER, 10)) {
                        gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
                        te.PROP_OWNER.set(gameProfile);
                    }
                    if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                        te.PROP_IMAGE_URL.set(compound.getString(TAG_IMAGE_URL));
                    }
                }
            }
        }
        worldIn.setBlockState(pos.offset(EnumFacing.UP), state.withProperty(STATE_PART, EnumPartType.TOP));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (!isTopOfMannequin(worldIn, pos)) {
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
            if (isValentins) {
                if (rand.nextFloat() * 100 > 75) {
                    Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.HEART.getParticleID(), pos.getX() - 0.2F + rand.nextFloat() * 0.6F, pos.getY(), pos.getZ() - 0.2F + rand.nextFloat() * 0.6F, 0, 0, 0, null);
                    Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                }
            }
            TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
            if (te != null && te.PROP_RENDER_EXTRAS.get()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.PROP_OWNER.get());
                if (contributor != null & te.PROP_VISIBLE.get()) {
                    if (contributor.uuid.equals(GAROAM_PROFILE.getId())) {
                        for (int i = 0; i < 10; i++) {
                            Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.HEART.getParticleID(), pos.getX() - 1 + rand.nextFloat() * 3F, pos.getY(), pos.getZ() - 1 + rand.nextFloat() * 3F, 0, 0, 0, null);
                            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                        }
                    } else {
                        for (int i = 0; i < 6; i++) {
                            Particle particle = particleManager.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), pos.getX() - 1 + rand.nextFloat() * 3F, pos.getY(), pos.getZ() - 1 + rand.nextFloat() * 3F, 0, 0, 0, null);
                            particle.setRBGColorF((contributor.r & 0xFF) / 255F, (contributor.g & 0xFF) / 255F, (contributor.b & 0xFF) / 255F);
                            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                        }
                    }
                }
            }
        }
    }

    public void convertToEntity(World world, BlockPos pos) {
        
        if (isTopOfMannequin(world, pos)) {
            pos = pos.offset(EnumFacing.DOWN);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == this) {
                ((BlockMannequin) state.getBlock()).convertToEntity(world, pos);
            }
            return;
        }

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
            world.spawnEntity(entityMannequin);
            entityMannequin.setTextureData(textureData, true);
            
            world.removeTileEntity(pos);
            
            world.setBlockToAir(pos);
            if (world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == this) {
                world.setBlockToAir(pos.offset(EnumFacing.UP));
            }
        }
    }

//    public void convertToDoll(World world, BlockPos pos) {
//        if (isTopOfMannequin(world, pos)) {
//            pos = pos.offset(EnumFacing.DOWN);
//            IBlockState state = world.getBlockState(pos);
//            if (state.getBlock() == this) {
//                ((BlockMannequin) state.getBlock()).convertToDoll(world, pos);
//            }
//            return;
//        }
//
//        if (world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == this) {
//            TileEntityMannequin te = getTileEntity(world, pos, TileEntityMannequin.class);
//            if (te != null) {
//                // te.setDropItems(false);
//                NBTTagCompound compound = new NBTTagCompound();
//                te.writeCommonToNBT(compound);
//                te.writeItemsToNBT(compound);
//                world.setBlockToAir(pos.offset(EnumFacing.UP));
//                world.setBlockState(pos, ModBlocks.DOLL.getDefaultState(), 3);
//                TileEntity newTe = world.getTileEntity(pos);
//                if (newTe != null && newTe instanceof TileEntityMannequin) {
//                    ((TileEntityMannequin) newTe).readCommonFromNBT(compound);
//                    ((TileEntityMannequin) newTe).readItemsFromNBT(compound);
//                    ((TileEntityMannequin) newTe).PROP_DOLL.set(true);
//                }
//            }
//        }
//    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
        if (te != null && te instanceof TileEntityMannequin) {
            if (te.PROP_NOCLIP.get()) {
                return NULL_AABB;
            }
        }
        return blockState.getBoundingBox(worldIn, pos);
    }

    public boolean isTopOfMannequin(IBlockAccess blockAccess, BlockPos pos) {
        IBlockState state = blockAccess.getBlockState(pos);
        if ((state.getBlock() != this)) {
            return false;
        }
        return state.getValue(STATE_PART) == EnumPartType.TOP;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (world.isRemote) {
            return false;
        }
        TileEntityMannequin te = getTileEntity(world, pos, TileEntityMannequin.class);
        if (te != null) {
            int rotation = te.PROP_ROTATION.get();
            rotation++;
            if (rotation > 15) {
                rotation = 0;
            }
            te.PROP_ROTATION.set(rotation);
        }
        return true;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (worldIn.isRemote) {
            return;
        }
        if (!(entityIn instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
        if (!isTopOfMannequin(worldIn, pos)) {
            return;
        }

        if (entityLiving.posY != pos.getY() + (double) 0.9F) {
            return;
        }

        if (entityLiving.posX < pos.getX() + 0.2F | entityLiving.posX > pos.getX() + 0.8F) {
            return;
        }

        if (entityLiving.posZ < pos.getZ() + 0.2F | entityLiving.posZ > pos.getZ() + 0.8F) {
            return;
        }
        TileEntityMannequin te = getTileEntity(worldIn, pos, TileEntityMannequin.class);
        if (te != null) {
            if (te.PROP_OWNER.get() != null && te.PROP_OWNER.get().getId() != null) {
                // ModLogger.log(te.PROP_OWNER.get());
                if (te.PROP_OWNER.get().getId().equals(VIC_PROFILE.getId())) {
                    entityLiving.attackEntityFrom(VIC_DAMAGE, 2.0F);
                }
            }
        }
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
        if (isTopOfMannequin(worldIn, pos)) {
            pos = pos.offset(EnumFacing.DOWN);
        }
        if (!playerIn.canPlayerEdit(pos, facing, playerIn.getHeldItem(hand))) {
            return false;
        }
        openGui(playerIn, EnumGuiId.MANNEQUIN, worldIn, pos, state, facing);
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (isTopOfMannequin(worldIn, pos)) {
            if (worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
        } else {
            if (worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (state.getValue(STATE_PART) == EnumPartType.BOTTOM) {
            return new TileEntityMannequin();
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
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

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockMannequin(this).setRegistryName(getRegistryName()));
    }

    @Override
    public <T extends TileEntity> T getTileEntity(IBlockAccess blockAccess, BlockPos pos, Class<T> type) {
        if (isTopOfMannequin(blockAccess, pos)) {
            pos = pos.offset(EnumFacing.DOWN);
        }
        return super.getTileEntity(blockAccess, pos, type);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        super.registerModels();
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(STATE_ROTATION, STATE_PART).build());
        Item.getItemFromBlock(this).setTileEntityItemStackRenderer(new RenderItemMannequin());
    }

    public static enum EnumPartType implements IStringSerializable {
        TOP("top"), BOTTOM("bottom");

        private final String name;

        private EnumPartType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
