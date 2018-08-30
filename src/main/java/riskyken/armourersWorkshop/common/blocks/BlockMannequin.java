package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.Contributors;
import riskyken.armourersWorkshop.common.Contributors.Contributor;
import riskyken.armourersWorkshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ItemBlockMannequin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.armourersWorkshop.utils.UtilItems;

public class BlockMannequin extends AbstractModBlockContainer implements IDebug {

    public static DamageSource victoriousDamage = new DamageSource("victorious");
    public static GameProfile vicProfile = new GameProfile(UUID.fromString("b027a4f4-d480-426c-84a3-a9cb029f4b72"), "VicNightfall");
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private final boolean isValentins;
    
    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.rock, soundTypeMetal, true);
        setLightOpacity(0);
        setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
        isValentins = HolidayHelper.valentins.isHolidayActive();
        setSortPriority(199);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockMannequin.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!world.isRemote) {
            TileEntityMannequin te = getMannequinTileEntity(world, x, y, z);
            if (te != null && te.getDropItems()) {
                ItemStack dropStack = te.getDropStack();
                UtilItems.spawnItemInWorld(world, x, y, z, dropStack);
                BlockUtils.dropInventoryBlocks(world, x, y, z);
            }
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor_double((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
            if (!world.isRemote) {
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    GameProfile gameProfile = null;
                    if (compound.hasKey(TAG_OWNER, 10)) {
                        gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
                        ((TileEntityMannequin)te).setGameProfile(gameProfile);
                    }
                    if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                        ((TileEntityMannequin)te).setImageUrl(compound.getString(TAG_IMAGE_URL));
                    }
                }
            }
        }
        world.setBlock(x, y + 1, z, this, 1, 2);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isTopOfMannequin(world, x, y, z)) {
            if (isValentins) {
                if (random.nextFloat() * 100 > 75) {
                    world.spawnParticle("heart", x + 0.2D + random.nextFloat() * 0.6F, y + 1D, z + 0.2D + random.nextFloat() * 0.6F, 0, 0, 0);
                }
            }
            TileEntityMannequin te = getMannequinTileEntity(world, x, y, z);
            if (te != null && te.isRenderExtras()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.getGameProfile());
                if (contributor != null & te.isVisible()) {
                    for (int i = 0; i < 4; i++) {
                        EntityFX entityfx = new EntitySpellParticleFX(world,  x - 1 + random.nextFloat() * 3F, y - 1D, z - 1 + random.nextFloat() * 3F, 0, 0, 0);
                        ((EntitySpellParticleFX)entityfx).setBaseSpellTextureIndex(144);
                        entityfx.setRBGColorF((float)(contributor.r & 0xFF) / 255F, (float)(contributor.g & 0xFF) / 255F, (float)(contributor.b & 0xFF) / 255F);
                        Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
                    }
                }
            }
        }
    }
    
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
    
    public TileEntityMannequin getMannequinTileEntity(World world, int x, int y, int z) {
        int offset = 0;
        if (isTopOfMannequin(world, x, y, z)) {
            offset = -1;
        }
        TileEntity te = world.getTileEntity(x, y + offset, z);
        if (te != null && te instanceof TileEntityMannequin) {
            return (TileEntityMannequin) te;
        }
        return null;
    }
    
    public boolean isTopOfMannequin(World world, int x, int y, int z) {
        return isTopOfMannequin(world.getBlockMetadata(x, y, z));
    }
    
    public boolean isTopOfMannequin(int meta) {
        return meta == 1;
    }
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
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
    }
    
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
                if (teMan.getGameProfile().getId() == vicProfile.getId()) {
                    entityLiving.attackEntityFrom(victoriousDamage, 2.0F);
                }
            }
        }
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ItemStack stack = new ItemStack(ModBlocks.mannequin, 1);
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
                NBTUtil.func_152460_a(profileTag, teMan.getGameProfile());
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
            }
            if (!StringUtils.isNullOrEmpty(teMan.getImageUrl())) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString(TAG_IMAGE_URL, teMan.getImageUrl());
            }
        }
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return true;
    }
    
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
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
            if (stack != null && stack.getItem() == Items.name_tag) {
                TileEntity te = world.getTileEntity(x, y + yOffset, z);;
                if (te != null && te instanceof TileEntityMannequin) {
                    if (stack.getItem() == Items.name_tag) {
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
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
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
    public int quantityDropped(int meta, int fortune, Random random) {
        return 0;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return null;
    }
    
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityMannequin(false);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isNormalCube() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public void getDebugHoverText(World world, int x, int y, int z, ArrayList<String> textLines) {
        textLines.add("top=" + isTopOfMannequin(world, x, y, z));
        TileEntityMannequin te = getMannequinTileEntity(world, x, y, z);
        if (te != null && te.getGameProfile() != null) {
            textLines.add("profile=" + te.getGameProfile().getName() + ":" + te.getGameProfile().getId());
        } else {
            textLines.add("profile=null");
        }
    }
}
