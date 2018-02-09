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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.Contributors;
import riskyken.armourersWorkshop.common.Contributors.Contributor;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.armourersWorkshop.utils.HolidayHelper;
import riskyken.armourersWorkshop.utils.UtilItems;

public class BlockDoll extends AbstractModBlockContainer {

    private static final String TAG_OWNER = "owner";
    private final boolean isValentins;
    
    public BlockDoll() {
        super(LibBlockNames.DOLL, Material.rock, soundTypeMetal, !ConfigHandler.hideDollFromCreativeTabs);
        setLightOpacity(0);
        setBlockBounds(0.2F, 0F, 0.2F, 0.8F, 0.95F, 0.8F);
        isValentins = HolidayHelper.valentins.isHolidayActive();
        setSortPriority(198);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityMannequin) {
                ItemStack dropStack = ((TileEntityMannequin)te).getDropStack();
                UtilItems.spawnItemInWorld(world, x, y, z, dropStack);
            }
            BlockUtils.dropInventoryBlocks(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, metadata);
    }
    
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor_double((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
            
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        if (isValentins) {
            if (random.nextFloat() * 100 > 80) {
                world.spawnParticle("heart", x + 0.2D + random.nextFloat() * 0.6F, y + 1D, z + 0.2D + random.nextFloat() * 0.6F, 0, 0, 0);
            }
        }
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityMannequin) {
            TileEntityMannequin te = (TileEntityMannequin) tileEntity;
            if (te.isRenderExtras() & te.isVisible()) {
                Contributor contributor = Contributors.INSTANCE.getContributor(te.getGameProfile());
                if (contributor != null) {
                    EntityFX entityfx = new EntitySpellParticleFX(world,  x + random.nextFloat() * 1F, y, z + random.nextFloat() * 1F, 0, 0, 0);
                    ((EntitySpellParticleFX)entityfx).setBaseSpellTextureIndex(144);
                    entityfx.setRBGColorF((float)(contributor.r & 0xFF) / 255F, (float)(contributor.g & 0xFF) / 255F, (float)(contributor.b & 0xFF) / 255F);
                    Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
                }
            }
        }
    }
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        if (world.isRemote) {
            return false;
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
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
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ItemStack stack = new ItemStack(ModBlocks.doll, 1);
        int meta = world.getBlockMetadata(x, y, z);
        int yOffset = 0;
        if (meta == 1) {
            yOffset = -1;
        }
        TileEntity te = world.getTileEntity(x, y + yOffset, z);;
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
        blockIcon = register.registerIcon(LibBlockResources.COLOURABLE);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null && stack.getItem() == ModItems.mannequinTool) {
                return false;
            }
            if (stack != null && stack.getItem() == Items.name_tag) {
                TileEntity te = world.getTileEntity(x, y, z);;
                if (te != null && te instanceof TileEntityMannequin) {
                    if (stack.getItem() == Items.name_tag) {
                        ((TileEntityMannequin)te).setOwner(player.getCurrentEquippedItem());
                    }
                }
            } else {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.MANNEQUIN, world, x, y, z);
            }
        }
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == ModItems.mannequinTool) {
            return false;
        }
        return true;
    }
    
    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityMannequin(true);
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
