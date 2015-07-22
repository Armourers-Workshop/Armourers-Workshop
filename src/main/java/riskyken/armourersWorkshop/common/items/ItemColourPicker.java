package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.painting.IPaintingTool;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.painting.PaintingNBTHelper;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class ItemColourPicker extends AbstractModItem implements IPaintingTool, IConfigurableTool {
    
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
                setToolColour(stack, ((IPantableBlock)block).getColour(world, x, y, z, side));
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PICKER, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        
        if (block == ModBlocks.boundingBox) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityBoundingBox && world.isRemote) {
                TileEntityArmourerBrain parent = ((TileEntityBoundingBox)te).getParent();
                if (parent != null) {
                    ISkinType skinType = parent.getSkinType();
                    if (skinPartHasTexture(((TileEntityBoundingBox)te).getSkinPart())) {
                        int colour = getColourFromSkin((TileEntityBoundingBox)te, player, world, x, y, z, side);
                        NBTTagCompound compound = new NBTTagCompound();
                        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
                        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
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
    
    private boolean skinPartHasTexture(ISkinPartType skinPart) {
        return skinPart instanceof ISkinPartTypeTextured;
    }
    
    private int getColourFromSkin(TileEntityBoundingBox te, EntityPlayer player, World world, int x, int y, int z, int side) {
        ISkinPartTypeTextured skinPart = (ISkinPartTypeTextured) te.getSkinPart();
        Point textureLocation = skinPart.getTextureLocation();
        IPoint3D textureModelSize = skinPart.getTextureModelSize();
        ForgeDirection blockFace = ForgeDirection.getOrientation(side);
        GameProfile gameProfile = te.getParent().getGameProfile();
        
        byte blockX = te.getGuideX();
        byte blockY = te.getGuideY();
        byte blockZ = te.getGuideZ();
        
        int textureX = textureLocation.x;
        int textureY = textureLocation.y;
        
        int shiftX = 0;
        int shiftY = 0;
        
        switch (blockFace) {
        case EAST:
            textureY += textureModelSize.getZ();
            shiftX = (byte) (-blockZ + textureModelSize.getZ() - 1);
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case NORTH:
            textureX += textureModelSize.getZ();
            textureY += textureModelSize.getZ();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case WEST:
            textureX += textureModelSize.getZ() + textureModelSize.getX();
            textureY += textureModelSize.getZ();
            shiftX = blockZ;
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case SOUTH:
            textureX += textureModelSize.getZ() + textureModelSize.getX() + textureModelSize.getZ();
            textureY += textureModelSize.getZ();
            shiftX = blockX;
            shiftY = (byte) (-blockY + textureModelSize.getY() - 1);
            break;
        case DOWN:
            textureX += textureModelSize.getZ() + textureModelSize.getX();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            shiftY = (byte) (-blockZ + textureModelSize.getX() - 1);
            break;
        case UP:
            textureX += textureModelSize.getZ();
            shiftX = (byte) (-blockX + textureModelSize.getX() - 1);
            shiftY = (byte) (-blockZ + textureModelSize.getZ() - 1);
            break;
        default:
            break;
        }
        
        textureX += shiftX;
        textureY += shiftY;
        
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(gameProfile);
        int colour = UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT);
        if (playerSkin != null) {
            colour = playerSkin.getRGB(textureX, textureY);
        }
        return colour;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        if (getToolHasColour(stack)) {
            Color c = new Color(getToolColour(stack));
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
            String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
            list.add(colourText);
            list.add(hexText);
        } else {
            String noPaint = TranslateUtils.translate("item.armourersworkshop:rollover.nopaint");
            list.add(noPaint);
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

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        //Only here to allow colour updates
    }
}
