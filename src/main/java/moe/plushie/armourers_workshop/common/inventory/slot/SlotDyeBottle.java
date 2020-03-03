package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.inventory.ContainerDyeTable;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotDyeBottle extends SlotHidable {
    
    private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(LibModInfo.ID, "textures/items/slot/dye-bottle.png");
    private final ContainerDyeTable container;
    private boolean locked;
    
    public SlotDyeBottle(IInventory inventory, int slotIndex, int xPosition, int yPosition) {
        this(inventory, slotIndex, xPosition, yPosition, null);
    }
    
    public SlotDyeBottle(IInventory inventory, int slotIndex, int xPosition, int yPosition, ContainerDyeTable container) {
        super(inventory, slotIndex, xPosition, yPosition);
        this.container = container;
        this.locked = false;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        if (container != null) {
            ItemStack skinStack = inventory.getStackInSlot(0);
            if (!SkinNBTHelper.stackHasSkinData(skinStack)) {
                return false;
            }
        }
        if (stack.getItem() == ModItems.DYE_BOTTLE) {
            if (PaintingHelper.getToolHasPaint(stack)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean canTakeStack(EntityPlayer player) {
        if (container != null) {
            if (!ConfigHandler.lockDyesOnSkins | player.capabilities.isCreativeMode) {
                return true;
            }
            return !locked;
        } else {
            return true;
        }
    }
    
    @Override
    public void onSlotChanged() {
        if (container != null) {
            ItemStack stack = getStack();
            if (stack.isEmpty()) {
                container.dyeRemoved(getSlotIndex() - 1);
            } else {
                if (stack.getItem() == ModItems.DYE_BOTTLE) {
                    container.dyeAdded(stack, getSlotIndex() - 1);
                }
            }
        }
        super.onSlotChanged();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getBackgroundLocation() {
        return BACKGROUND_IMAGE;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getBackgroundSprite() {
        return new DummySprite("");
    }
    
    @SideOnly(Side.CLIENT)
    private class DummySprite extends TextureAtlasSprite {

        protected DummySprite(String spriteName) {
            super(spriteName);
        }
        
        @Override
        public float getMaxU() {
            return 1;
        }
        
        @Override
        public float getMaxV() {
            return 1;
        }
        
        @Override
        public float getMinU() {
            return 0;
        }
        
        @Override
        public float getMinV() {
            return 0;
        }
    }
}
