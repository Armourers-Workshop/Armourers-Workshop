package moe.plushie.armourers_workshop.common.items;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.SkinInventoryContainer;
import moe.plushie.armourers_workshop.common.inventory.WardrobeInventory;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperty;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemOutfit extends AbstractModItem {

    private ISkinType[] skinTypes = new ISkinType[] {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet,
            SkinTypeRegistry.skinWings};
    
    public ItemOutfit() {
        super(LibItemNames.OUTFIT);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.getInstance(), LibGuiIds.OUTFIT, worldIn, 0, 0, 0);
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
            } else {
                if (itemStack.hasTagCompound()) {
                    SkinInventoryContainer sic = new SkinInventoryContainer(null, skinTypes, 2);
                    sic.readFromNBT(itemStack.getTagCompound());
                    ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();
                    SkinProperties skinProperties = new SkinProperties();
                    int[] paintData = null;
                    for (int skinIndex = 0; skinIndex < skinTypes.length; skinIndex++) {
                        WardrobeInventory wi = sic.getSkinTypeInv(skinTypes[skinIndex]);
                        for (int i = 0; i < wi.getSizeInventory(); i++) {
                            ItemStack stack = wi.getStackInSlot(i);
                            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
                            if (descriptor != null) {
                                Skin skin = CommonSkinCache.INSTANCE.getSkin(descriptor);
                                if (skin != null) {
                                    skinParts.addAll(skin.getParts());
                                    for (ISkinProperty prop : skin.getSkinType().getProperties()) {
                                        SkinProperty p = (SkinProperty) prop;
                                        p.setValue(skinProperties, p.getValue(skin.getProperties()));
                                    }
                                    if (skin.hasPaintData()) {
                                        paintData = skin.getPaintData();
                                    }
                                }
                            }
                        }
                    }
                    if (!skinParts.isEmpty()) {
                        Skin skin = new Skin(skinProperties, SkinTypeRegistry.skinOutfit, paintData, skinParts);
                        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile)null);
                        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skin));
                        UtilItems.spawnItemAtEntity(playerIn, skinStack, true);
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
