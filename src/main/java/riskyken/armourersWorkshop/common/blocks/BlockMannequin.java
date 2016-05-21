package riskyken.armourersWorkshop.common.blocks;

import java.util.Random;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.tileentities.TileEntityMannequinClient;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ItemBlockMannequin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.HolidayHelper;

public class BlockMannequin extends AbstractModBlockContainer {

    public static DamageSource victoriousDamage = new DamageSource("victorious");
    private static final String TAG_OWNER = "owner";
    private final boolean isValentins;
    
    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.rock, soundTypeMetal, true);
        setLightOpacity(0);
        setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
        isValentins = HolidayHelper.valentins.isHolidayActive();
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockMannequin.class, "block." + name);
        return super.setBlockName(name);
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
            TileEntityMannequin tileEntity = getMannequinTileEntity(world, x, y, z);
            if (tileEntity != null && tileEntity instanceof TileEntityMannequinClient) {
                TileEntityMannequinClient te = (TileEntityMannequinClient)tileEntity;
                if (te.isRenderExtras() && te.hasSpecialRender()) {
                    for (int i = 0; i < 4; i++) {
                        EntityFX entityfx = new EntitySpellParticleFX(world,  x - 1 + random.nextFloat() * 3F, y - 1D, z - 1 + random.nextFloat() * 3F, 0, 0, 0);
                        ((EntitySpellParticleFX)entityfx).setBaseSpellTextureIndex(144);
                        float[] colour = te.getSpecialRenderColour();
                        entityfx.setRBGColorF(colour[0], colour[1], colour[2]);
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
                if (teMan.getGameProfile().getName().equals("victorious3")) {
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibModInfo.ID + ":" + "colourable");
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
    public TileEntity getTileEntityCommon(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityMannequin();
        }
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public TileEntity getTileEntityClient(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityMannequinClient();
        }
        return null;
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
}
