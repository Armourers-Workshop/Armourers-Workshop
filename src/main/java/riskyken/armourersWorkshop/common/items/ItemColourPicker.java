package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPartTextured;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.utils.PaintingNBTHelper;
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
        itemIcon = register.registerIcon(LibItemResources.COLOUR_PICKER);
        tipIcon = register.registerIcon(LibItemResources.COLOUR_PICKER_TIP);
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
                    if (skinPartHasTexture(((TileEntityBoundingBox)te).getSkinPart())) {
                        int colour = getColourFromSkin(parent, ((TileEntityBoundingBox)te).getSkinPart(), player, world, x, y, z, side);
                        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate((byte)1, colour));
                    }
                }
            }
            if (!world.isRemote) {
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        return false;
    }
    
    private boolean skinPartHasTexture(IEquipmentSkinPart skinPart) {
        return skinPart instanceof IEquipmentSkinPartTextured;
    }
    
    private int getColourFromSkin(TileEntityArmourerBrain te, IEquipmentSkinPart skinPart, EntityPlayer player, World world, int x, int y, int z, int side) {
        /*
        
        int textureX = skinPart.getTextureX();
        int textureY = skinPart.getTextureY();
        boolean holdMirror = true;
        
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        ForgeDirection xSearchAxis = ForgeDirection.UNKNOWN;
        ForgeDirection ySearchAxis = ForgeDirection.UNKNOWN;
        
        switch (dir) {
        case DOWN:
            textureX += skinPart.getTextureHeight() + skinPart.getTextureWidth();
            ySearchAxis = ForgeDirection.SOUTH;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case UP:
            textureX += skinPart.getTextureHeight();
            ySearchAxis = ForgeDirection.SOUTH;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case NORTH:
            textureX += skinPart.getTextureHeight();
            textureY += skinPart.getTextureHeight();
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.EAST;
            break;
        case SOUTH:
            textureX += skinPart.getTextureHeight() + skinPart.getTextureWidth() + skinPart.getTextureHeight();
            textureY += skinPart.getTextureHeight();
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.WEST;
            break;
        case WEST:
            textureX += skinPart.getTextureHeight() + skinPart.getTextureWidth();
            textureY += skinPart.getTextureHeight();
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.NORTH;
            holdMirror = false;
            break;
        case EAST:
            textureY += skinPart.getTextureHeight();
            ySearchAxis = ForgeDirection.UP;
            xSearchAxis = ForgeDirection.SOUTH;
            holdMirror = false;
            break;
        case UNKNOWN:
            break;
        }
        
        for (int ix = 1; ix < 13; ix++) {
            int xOffset = xSearchAxis.offsetX;
            int yOffset = xSearchAxis.offsetY;
            int zOffset = xSearchAxis.offsetZ;
            Block block = null;
            if (skinPart.isTextureMirrored() & holdMirror) {
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
            } else {
                TileEntity teTar = world.getTileEntity(x + xOffset * iy, y + yOffset * iy, z + zOffset * iy);
                if (teTar != null && teTar instanceof TileEntityBoundingBox) {
                    if (((TileEntityBoundingBox)teTar).getSkinPart() != skinPart) {
                        textureY += iy - 1;
                        break;
                    }
                }
            }
        }
        
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(te.getGameProfile());
        
        int colour = UtilColour.getMinecraftColor(0);
        if (playerSkin != null) {
            colour = playerSkin.getRGB(textureX, textureY);
        }
        return colour;
        */
        return 0;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        String cGray = EnumChatFormatting.GRAY.toString();
        String cGold = EnumChatFormatting.GOLD.toString();
        if (getToolHasColour(stack)) {
            Color c = new Color(getToolColour(stack));
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            list.add(cGold + "Colour: " + cGray + c.getRGB());
            list.add(cGold + "Hex: " + cGray + hex);
        } else {
            list.add("No paint");
        }
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
