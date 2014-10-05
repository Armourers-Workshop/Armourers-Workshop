package riskyken.armourersWorkshop.common.items;

import java.awt.image.BufferedImage;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.BodyPart;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemColourPicker extends AbstractModItem implements IPaintingTool {
    
    public ItemColourPicker() {
        super(LibItemNames.COLOUR_PICKER);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibModInfo.ID + ":" + "colour-picker");
        tipIcon = register.registerIcon(LibModInfo.ID + ":" + "colour-picker-tip");
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        if (player.isSneaking() & block == ModBlocks.colourMixer & getToolHasColour(stack)) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof IPantable) {
                if (!world.isRemote) {
                    int colour = getToolColour(stack);
                    ((IPantable)te).setColour(colour);
                }
            }
            return true;
        }
        
        
        if (block instanceof IPantableBlock) {
            if (!world.isRemote) {
                setToolColour(stack, ((IPantableBlock)block).getColour(world, x, y, z));
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        
        if (block == ModBlocks.boundingBox) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityBoundingBox && world.isRemote) {
                TileEntityArmourerBrain parent = ((TileEntityBoundingBox)te).getParent();
                if (parent != null) {
                    int colour = getColourFromSkin(parent, ((TileEntityBoundingBox)te).getBodyPart(), player, world, x, y, z, side);
                    PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate((byte)1, colour));
                }
            }
            if (!world.isRemote) {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        return false;
    }
    
    private int getColourFromSkin(TileEntityArmourerBrain te, BodyPart bodyPart, EntityPlayer player, World world, int x, int y, int z, int side) {
        int textureX = bodyPart.textureX;
        int textureY = bodyPart.textureY;
        
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        ForgeDirection xSearchAxis = ForgeDirection.UNKNOWN;
        ForgeDirection ySearchAxis = ForgeDirection.UNKNOWN;
        
        switch (dir) {
        case DOWN:
            textureX += bodyPart.zSize + bodyPart.xSize;
            ySearchAxis = ForgeDirection.SOUTH;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case UP:
            textureX += bodyPart.zSize;
            ySearchAxis = ForgeDirection.SOUTH;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case NORTH:
            textureX += bodyPart.zSize;
            textureY += bodyPart.zSize;
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case SOUTH:
            textureX += bodyPart.zSize + bodyPart.xSize + bodyPart.zSize;
            textureY += bodyPart.zSize;
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.WEST;
            break;
        case WEST:
            textureX +=  bodyPart.zSize + bodyPart.xSize;
            textureY += bodyPart.zSize;
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.NORTH;
            break;
        case EAST:
            textureY += bodyPart.zSize;
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.SOUTH;
            break;
        case UNKNOWN:
            break;
        }
        
        for (int ix = 1; ix < 13; ix++) {
            int xOffset = xSearchAxis.offsetX;
            int yOffset = xSearchAxis.offsetY;
            int zOffset = xSearchAxis.offsetZ;
            Block block = null;
            if (bodyPart.mirrorTexture) {
                block = world.getBlock(x - xOffset * ix, y - yOffset * ix, z - zOffset * ix);
            } else {
                block = world.getBlock(x + xOffset * ix, y + yOffset * ix, z + zOffset * ix);
            }
            
            if (block != ModBlocks.boundingBox) {
                textureX += ix - 1;
                break;
            }
        }
        
        for (int iy = 1; iy < 13; iy++) {
            int xOffset = ySearchAxis.offsetX;
            int yOffset = ySearchAxis.offsetY;
            int zOffset = ySearchAxis.offsetZ;
            Block block = world.getBlock(x + xOffset * iy, y + yOffset * iy, z + zOffset * iy);
            if (block != ModBlocks.boundingBox) {
                textureY += iy - 1;
                break;
            }
        }
        
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(te.getGameProfile());
        int colour = playerSkin.getRGB(textureX, textureY);
        
        return colour;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (getToolHasColour(stack)) {
            list.add("Colour " + getToolColour(stack));
        } else {
            list.add("No colour");
        }
        super.addInformation(stack, player, list, p_77624_4_);
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }
    
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (!getToolHasColour(stack)) {
            return super.getColorFromItemStack(stack, pass);
        }
        
        if (pass == 0) {
            return super.getColorFromItemStack(stack, pass);
        }
        return getToolColour(stack);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (!getToolHasColour(stack)) {
            return itemIcon;
        }
        if (pass == 0) {
            return itemIcon;
        }
        return tipIcon;
    }
    
    @Override
    public boolean getToolHasColour(ItemStack stack) {
        return PaintingNBTHelper.getToolHasColour(stack);
    }

    @Override
    public int getToolColour(ItemStack stack) {
        return PaintingNBTHelper.getToolColour(stack);
    }

    @Override
    public void setToolColour(ItemStack stack, int colour) {
        PaintingNBTHelper.setToolColour(stack, colour);
    }
}
