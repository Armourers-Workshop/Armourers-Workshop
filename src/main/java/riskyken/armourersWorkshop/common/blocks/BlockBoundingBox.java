package riskyken.armourersWorkshop.common.blocks;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.SkinTextureHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.utils.BitwiseUtils;

public class BlockBoundingBox extends AbstractModBlockContainer implements IPantableBlock {

    protected BlockBoundingBox() {
        super(LibBlockNames.BOUNDING_BOX, Material.CLOTH, SoundType.CLOTH, false);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setLightOpacity(0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
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
        return null;
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
                //world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
                world.markBlockRangeForRenderUpdate(pos, pos);
                return false;
            } else {
                world.setBlockToAir(pos);
                return true;
            }
        }
        world.setBlockToAir(pos);
        return world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityBoundingBox();
    }
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, int colour, EnumFacing side) {
        BlockPos sideBlock = pos.offset(side);
        if (world.getBlockState(sideBlock).getBlock() == this) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(pos);
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
        
        return false;
    }
    
    @Override
    public boolean setColour(IBlockAccess world, BlockPos pos, byte[] rgb, EnumFacing side) {
        int colour = new Color(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF).getRGB();
        return setColour(world, pos, colour, side);
    }

    @Override
    public int getColour(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos sideBlock = pos.offset(side);
        if (world.getBlockState(sideBlock).getBlock() == this) {
            return 0x00FFFFFF;
        }
        
        TileEntity te = world.getTileEntity(pos);
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
                        if (te.getWorld().isRemote) {
                            GameProfile gameProfile = parent.getGameProfile();
                            if (gameProfile != null) {
                                BufferedImage playerSkin = SkinHelper.getBufferedImageSkinNew(gameProfile);
                                if (playerSkin != null) {
                                    colour = playerSkin.getRGB(texturePoint.x, texturePoint.y);
                                    return colour;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return 0x00FFFFFF;
    }
    
    @Override
    public boolean isRemoteOnly(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos sideBlock = pos.offset(side);
        if (world.getBlockState(sideBlock).getBlock() == this) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(pos);
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
        
        return false;
    }
    
    @Override
    public void setPaintType(IBlockAccess world, BlockPos pos, PaintType paintType, EnumFacing side) {
        BlockPos sideBlock = pos.offset(side);
        if (world.getBlockState(sideBlock).getBlock() == this) {
            return;
        }
        
        TileEntity te = world.getTileEntity(pos);
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
    }
    
    @Override
    public PaintType getPaintType(IBlockAccess world, BlockPos pos, EnumFacing side) {
        BlockPos sideBlock = pos.offset(side);
        if (world.getBlockState(sideBlock).getBlock() == this) {
            return PaintType.NORMAL;
        }
        
        TileEntity te = world.getTileEntity(pos);
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
        return PaintType.NORMAL;
    }

    @Override
    public ICubeColour getColour(IBlockAccess world, BlockPos pos) {
        return null;
    }
}
