package moe.plushie.armourers_workshop.common.blocks;

import java.awt.Color;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityBoundingBox;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBoundingBox extends AbstractModBlockContainer implements IPantableBlock {

    protected BlockBoundingBox() {
        super(LibBlockNames.BOUNDING_BOX, Material.CLOTH, SoundType.CLOTH, false);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setLightOpacity(0);
    }
    
    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
    }
    
    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockToAir(pos);
    }
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityBoundingBox) {
            if (((TileEntityBoundingBox)tileEntity).isParentValid()) {
                tileEntity.markDirty();
                //world.markBlockForUpdate(pos);
                return false;
            } else {
                world.setBlockToAir(pos);
                return true;
            }
        }
        world.setBlockToAir(pos);
        return true;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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
    public String getTranslationKey() {
        return getModdedUnlocalizedName(super.getTranslationKey());
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityBoundingBox();
    }
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing facing) {
        //EnumFacing sideBlock = EnumFacing.byIndex(side);
        /*
        if (world.getBlock(x + sideBlock.offsetX, y + sideBlock.offsetY, z + sideBlock.offsetZ) == this) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBoundingBox) {
            TileEntityArmourer parent = ((TileEntityBoundingBox)te).getParent();
            if (((TileEntityBoundingBox)te).getSkinPart() instanceof ISkinPartTypeTextured) {
                if (parent != null) {
                    ISkinType skinType = parent.getSkinType();
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    int oldColour = parent.getPaintData(texturePoint.x, texturePoint.y);
                    int paintType = BitwiseUtils.getUByteFromInt(oldColour, 0);
                    int newColour = BitwiseUtils.setUByteToInt(colour, 0, paintType);
                    parent.updatePaintData(texturePoint.x, texturePoint.y, newColour);
                    return true;
                }
            }
        }
        */
        return false;
    }
    
    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {}
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing facing) {
        int colour = new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF).getRGB();
        return setColour(world, pos, colour, facing);
    }

    @Override
    public int getColour(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        //EnumFacing sideBlock = EnumFacing.byIndex(facing);
        /*
        if (world.getBlock(x + sideBlock.offsetX, y + sideBlock.offsetY, z + sideBlock.offsetZ) == this) {
            return 0x00FFFFFF;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBoundingBox) {
            TileEntityArmourer parent = ((TileEntityBoundingBox)te).getParent();
            if (parent != null) {
                if (((TileEntityBoundingBox)te).getSkinPart() instanceof ISkinPartTypeTextured) {
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    int colour = parent.getPaintData(texturePoint.x, texturePoint.y);
                    int paintType = BitwiseUtils.getUByteFromInt(colour, 0);
                    if (paintType != 0) {
                        return colour;
                    } else {
                        if (te.getWorldObj().isRemote) {
                            PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(parent.getTexture());
                            BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(playerTexture.getResourceLocation());
                            if (playerSkin != null) {
                                colour = playerSkin.getRGB(texturePoint.x, texturePoint.y);
                                return colour;
                            }
                        }
                    }
                }
            }
        }
        */
        return 0x00FFFFFF;
    }
    
    @Override
    public boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        //EnumFacing sideBlock = EnumFacing.byIndex(side);
        /*
        if (world.getBlock(x + sideBlock.offsetX, y + sideBlock.offsetY, z + sideBlock.offsetZ) == this) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBoundingBox) {
            TileEntityArmourer parent = ((TileEntityBoundingBox)te).getParent();
            if (parent != null) {
                if (((TileEntityBoundingBox)te).getSkinPart() instanceof ISkinPartTypeTextured) {
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    int colour = parent.getPaintData(texturePoint.x, texturePoint.y);
                    int paintType = BitwiseUtils.getUByteFromInt(colour, 0);
                    return paintType == 0;
                }
            }
        }
        */
        return false;
    }
    
    @Override
    public void setPaintType(IBlockAccess world, BlockPos pos, PaintType paintType, EnumFacing facing) {
        //EnumFacing sideBlock = EnumFacing.byIndex(side);
        /*
        if (world.getBlock(x + sideBlock.offsetX, y + sideBlock.offsetY, z + sideBlock.offsetZ) == this) {
            return;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBoundingBox) {
            TileEntityArmourer parent = ((TileEntityBoundingBox)te).getParent();
            if (((TileEntityBoundingBox)te).getSkinPart() instanceof ISkinPartTypeTextured) {
                if (parent != null) {
                    ISkinType skinType = parent.getSkinType();
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    
                    int oldColour = parent.getPaintData(texturePoint.x, texturePoint.y);
                    
                    int newColour = PaintType.setPaintTypeOnColour(paintType, oldColour);
                    parent.updatePaintData(texturePoint.x, texturePoint.y, newColour);
                }
            }
        }
        */
    }
    
    @Override
    public PaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        //EnumFacing sideBlock = EnumFacing.byIndex(side);
        /*
        if (world.getBlock(x + sideBlock.offsetX, y + sideBlock.offsetY, z + sideBlock.offsetZ) == this) {
            return PaintType.NORMAL;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBoundingBox) {
            TileEntityArmourer parent = ((TileEntityBoundingBox)te).getParent();
            if (parent != null) {
                if (((TileEntityBoundingBox)te).getSkinPart() instanceof ISkinPartTypeTextured) {
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    int colour = parent.getPaintData(texturePoint.x, texturePoint.y);
                    return PaintType.getPaintTypeFromColour(colour);
                }
            }
        }
        */
        return PaintType.NORMAL;
    }

    @Override
    public ICubeColour getColour(IBlockAccess world, BlockPos pos) {
        return null;
    }
}
