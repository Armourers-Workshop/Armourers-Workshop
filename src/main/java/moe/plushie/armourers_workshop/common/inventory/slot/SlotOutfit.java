package moe.plushie.armourers_workshop.common.inventory.slot;

import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotOutfit extends SlotHidable {
    
    private static final ResourceLocation BACKGROUND_IMAGE = new ResourceLocation(LibModInfo.ID, "textures/items/slot/outfit.png");
    
    public SlotOutfit(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == ModItems.outfit;
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
