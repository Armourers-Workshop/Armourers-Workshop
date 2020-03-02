package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSkinUnlock extends AbstractModItem {

    private final ISkinType[] VALID_SKINS = {
            SkinTypeRegistry.skinHead,
            SkinTypeRegistry.skinChest,
            SkinTypeRegistry.skinLegs,
            SkinTypeRegistry.skinFeet,
            SkinTypeRegistry.skinWings,
            SkinTypeRegistry.skinOutfit
            };

    public ItemSkinUnlock() {
        super(LibItemNames.SKIN_UNLOCK);
        setHasSubtypes(true);
        setSortPriority(7);
        setMaxStackSize(8);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < VALID_SKINS.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        for (int i = 0; i < VALID_SKINS.length; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()) + "-" + VALID_SKINS[i].getName().toLowerCase(), "inventory"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        ISkinType skinType = getSkinTypeFormStack(playerIn.getHeldItem(handIn));
        TextComponentTranslation unlocalizedSkinName = new TextComponentTranslation("skinType." + skinType.getRegistryName() + ".name", new Object[0]);
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(playerIn);
        if (wardrobeCap == null) {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
        }
        int count = wardrobeCap.getUnlockedSlotsForSkinType(skinType) + 1;
        if (count <= wardrobeCap.getMaxSlotsForSkinType(skinType)) {
            if (!worldIn.isRemote) {
                wardrobeCap.setUnlockedSlotsForSkinType(skinType, count);
                wardrobeCap.syncToPlayer((EntityPlayerMP) playerIn);
                wardrobeCap.syncToAllTracking();
                itemStack.shrink(1);
                playerIn.sendMessage(new TextComponentTranslation("chat.armourers_workshop:slotUnlocked", unlocalizedSkinName, Integer.toString(count)));
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
        } else {
            if (!worldIn.isRemote) {
                playerIn.sendMessage(new TextComponentTranslation("chat.armourers_workshop:slotUnlockedFailed", unlocalizedSkinName));
            }
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
        }
    }

    private ISkinType getSkinTypeFormStack(ItemStack itemStack) {
        int damage = itemStack.getItemDamage();
        if (damage >= 0 & damage < VALID_SKINS.length) {
            return VALID_SKINS[damage];
        }
        return VALID_SKINS[0];
    }
}
