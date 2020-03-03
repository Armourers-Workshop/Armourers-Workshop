package moe.plushie.armourers_workshop.common.init.blocks;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.model.ICustomModel;
import moe.plushie.armourers_workshop.common.creativetab.ISortOrder;
import moe.plushie.armourers_workshop.common.init.items.block.ModItemBlock;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.permission.IPermissionHolder;
import moe.plushie.armourers_workshop.common.permission.Permission;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

public abstract class AbstractModBlockContainer extends BlockContainer implements ISortOrder, ICustomItemBlock, ICustomModel, IPermissionHolder {

    private int sortPriority = 100;

    public AbstractModBlockContainer(String name) {
        super(Material.IRON);
        setCreativeTab(ArmourersWorkshop.TAB_MAIN);
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    public AbstractModBlockContainer(String name, Material material, SoundType soundType, boolean addCreativeTab) {
        super(material);
        if (addCreativeTab) {
            setCreativeTab(ArmourersWorkshop.TAB_MAIN);
        }
        setHardness(3.0F);
        setSoundType(soundType);
        setTranslationKey(name);
        ModBlocks.BLOCK_LIST.add(this);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    protected static boolean getBitBool(int value, int index) {
        return getBit(value, index) == 1;
    }

    protected static int getBit(int value, int index) {
        return (value >> index) & 1;
    }

    protected static int setBit(int value, int index, boolean on) {
        if (on) {
            return value | (1 << index);
        } else {
            return value & ~(1 << index);
        }
    }

    @Override
    public Block setTranslationKey(String name) {
        super.setTranslationKey(name);
        setRegistryName(new ResourceLocation(LibModInfo.ID, "tile." + name));
        return this;
    }

    public AbstractModBlockContainer setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
        return this;
    }

    public <T extends TileEntity> T getTileEntity(IBlockAccess blockAccess, BlockPos pos, Class<T> type) {
        TileEntity te = blockAccess.getTileEntity(pos);
        if (te != null && type.isAssignableFrom(te.getClass())) {
            return (T) te;
        }
        return null;
    }

    @Override
    public int getSortPriority() {
        return sortPriority;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ModItemBlock(this).setRegistryName(getRegistryName()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"));
    }

    protected void openGui(EntityPlayer playerIn, EnumGuiId guiId, World worldIn, BlockPos pos, IBlockState state, EnumFacing facing) {
        openGui(playerIn, guiId.ordinal(), worldIn, pos, state, facing);
    }

    protected void openGui(EntityPlayer playerIn, int guiId, World worldIn, BlockPos pos, IBlockState state, EnumFacing facing) {
        if (!worldIn.isRemote) {
            if (PermissionAPI.hasPermission(playerIn.getGameProfile(), LibModInfo.ID + "." + getPermissionName() + ".open-gui", new BlockPosContext(playerIn, pos, state, facing))) {
                FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.getInstance(), guiId, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Override
    public void getPermissions(ArrayList<Permission> permissions) {
        permissions.add(new Permission(getPermissionName() + ".open-gui", DefaultPermissionLevel.ALL));
    }

    @Override
    public String getPermissionName() {
        return getTranslationKey();
    }
}
