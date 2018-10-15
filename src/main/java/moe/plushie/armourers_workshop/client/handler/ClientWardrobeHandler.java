package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClientWardrobeHandler {
    
    public ClientWardrobeHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private static ItemStack[] armour = new ItemStack[4];
    
    public static ItemStack getArmourInSlot(int slotId) {
        return armour[slotId];
    }
    
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        
    }
    
    @SubscribeEvent
    public void onRenderSpecificHand(RenderSpecificHandEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        ItemStack itemStack = event.getItemStack();
        
        if (itemStack.getItem() == ModItems.skin) {
            return;
        }
        
        ISkinType[] skinTypes = new ISkinType[] {
                SkinTypeRegistry.skinSword,
                SkinTypeRegistry.skinShield,
                SkinTypeRegistry.skinBow,
                
                SkinTypeRegistry.skinPickaxe,
                SkinTypeRegistry.skinAxe,
                SkinTypeRegistry.skinShovel,
                SkinTypeRegistry.skinHoe,
                
                SkinTypeRegistry.skinItem
        };
        
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        if (descriptor == null) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
            if (skinCapability != null) {
                for (int i = 0; i < ItemOverrideType.values().length; i++) {
                    if (ModAddonManager.isOverrideItem(ItemOverrideType.values()[i], itemStack.getItem())) {
                        
                        ISkinDescriptor descriptorItem = skinCapability.getSkinDescriptor(skinTypes[i], 0);
                        if (descriptorItem != null) {
                            descriptor = descriptorItem;
                            break;
                        }
                    }
                }
            }
        }
        
        if (descriptor == null) {
            return;
        }
        
        event.setCanceled(true);
        
        boolean flag = event.getHand() == EnumHand.OFF_HAND;
        
        GlStateManager.pushMatrix();
        renderItemInFirstPerson((AbstractClientPlayer) player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), itemStack, event.getEquipProgress());
        
        
        int i = event.getHand() == EnumHand.MAIN_HAND ? 1 : -1;
        GlStateManager.translate((float)i * 0.56F, -0.52F + event.getEquipProgress() * -0.6F, -0.72F);
        
        
        GlStateManager.enableCull();
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
        if (flag) {
            GlStateManager.scale(-1, 1, 1);
            GlStateManager.cullFace(CullFace.FRONT);
        }
        SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
        if (flag) {
            GlStateManager.cullFace(CullFace.BACK);
        }
        GlStateManager.disableCull();
        
        GlStateManager.popMatrix();
    }
    
    public void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float equipProgress) {
        boolean flag = hand == EnumHand.MAIN_HAND;
        EnumHandSide enumhandside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        
        boolean flag1 = enumhandside == EnumHandSide.RIGHT;
        
        if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == hand) {
            int j = flag1 ? 1 : -1;

            switch (stack.getItemUseAction()) {
                case NONE:
                    this.transformSideFirstPerson(enumhandside, equipProgress);
                    break;
                case EAT:
                case DRINK:
                    //this.transformEatFirstPerson(p_187457_2_, enumhandside, stack);
                    this.transformSideFirstPerson(enumhandside, equipProgress);
                    break;
                case BLOCK:
                    this.transformSideFirstPerson(enumhandside, equipProgress);
                    break;
                case BOW:
                    this.transformSideFirstPerson(enumhandside, equipProgress);
                    GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
                    GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
                    float f5 = (float)stack.getMaxItemUseDuration() - ((float)player.getItemInUseCount() - p_187457_2_ + 1.0F);
                    float f6 = f5 / 20.0F;
                    f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;

                    if (f6 > 1.0F)
                    {
                        f6 = 1.0F;
                    }

                    if (f6 > 0.1F)
                    {
                        float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
                        float f3 = f6 - 0.1F;
                        float f4 = f7 * f3;
                        GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
                    }

                    GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
                    GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
                    GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
            }
        } else {
            float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * (float)Math.PI);
            float f1 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * ((float)Math.PI * 2F));
            float f2 = -0.2F * MathHelper.sin(p_187457_5_ * (float)Math.PI);
            int i = flag1 ? 1 : -1;
            GlStateManager.translate((float)i * f, f1, f2);
            this.transformSideFirstPerson(enumhandside, equipProgress);
            this.transformFirstPerson(enumhandside, p_187457_5_);
        }
    }
    
    private void transformSideFirstPerson(EnumHandSide hand, float equipProgress) {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float)i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
    }
    
    private void transformFirstPerson(EnumHandSide hand, float equipProgress) {
        int i = hand == EnumHandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(equipProgress * equipProgress * (float)Math.PI);
        GlStateManager.rotate((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(equipProgress) * (float)Math.PI);
        GlStateManager.rotate((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        /*
        if (player instanceof MannequinFakePlayer) {
            return;
        }
        */
        if (player instanceof FakePlayer) {
            return;
        }
        
        for (int i = 0; i < armour.length; i++) {
            armour[i] = ItemStack.EMPTY;
        }
        
        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
        if (wardrobeCapability != null) {
            for (int i = 0; i < armour.length; i++) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.values()[i + 2];
                armour[i] = player.inventory.armorInventory.get(i);
                if (wardrobeCapability.getArmourOverride(slot)) {
                    player.inventory.armorInventory.set(i, ItemStack.EMPTY);
                }
            }
        }

        
        // Hide the head overlay if the player has turned it off.
        RenderPlayer renderer = event.getRenderer();
        if (SkinModelRenderer.INSTANCE.playerHasCustomHead(player)) {
            renderer.getMainModel().bipedHeadwear.isHidden = true;
        }
        
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        ISkinType[] skinTypes = new ISkinType[] {SkinTypeRegistry.skinHead, SkinTypeRegistry.skinChest, SkinTypeRegistry.skinLegs, SkinTypeRegistry.skinFeet};
        
        if (skinCapability != null) {
            for (int i = 0; i < skinTypes.length; i++) {
                ISkinType skinType = skinTypes[i];
                
                ISkinDescriptor skinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(armour[3 - i]);
                
                if (skinDescriptor == null) {
                    skinDescriptor = skinCapability.getSkinDescriptor(skinType, 0);
                }
                
                if (skinDescriptor == null) {
                    continue;
                }
                
                Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor, false);
                if (skin == null) {
                    continue;
                }
                if (SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skin.getProperties())) {
                    if (i == 0) {
                        modelPlayer.bipedHead.isHidden = true;
                        modelPlayer.bipedHeadwear.isHidden = true;
                    } else if(i == 1) {
                        modelPlayer.bipedBody.isHidden = true;
                        modelPlayer.bipedBodyWear.isHidden = true;
                        modelPlayer.bipedLeftArm.isHidden = true;
                        modelPlayer.bipedLeftArmwear.isHidden = true;
                        modelPlayer.bipedRightArm.isHidden = true;
                        modelPlayer.bipedLeftArmwear.isHidden = true;
                    } else if(i == 2) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedLeftLegwear.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
                        modelPlayer.bipedRightLegwear.isHidden = true;
                    } else if(i == 3) {
                        modelPlayer.bipedLeftLeg.isHidden = true;
                        modelPlayer.bipedLeftLegwear.isHidden = true;
                        modelPlayer.bipedRightLeg.isHidden = true;
                        modelPlayer.bipedRightLegwear.isHidden = true;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        /*if (player instanceof MannequinFakePlayer) {
            return;
        }*/
        if (player instanceof FakePlayer) {
            return;
        }
        for (int i = 0; i < armour.length; i++) {
            player.inventory.armorInventory.set(i, armour[i]);
        }
        
        //Restore the head overlay.
        ModelPlayer modelPlayer = event.getRenderer().getMainModel();
        modelPlayer.bipedHead.isHidden = false;
        modelPlayer.bipedHeadwear.isHidden = false;
        
        modelPlayer.bipedBody.isHidden = false;
        modelPlayer.bipedBodyWear.isHidden = false;
        modelPlayer.bipedLeftArm.isHidden = false;
        modelPlayer.bipedLeftArmwear.isHidden = false;
        modelPlayer.bipedRightArm.isHidden = false;
        modelPlayer.bipedLeftArmwear.isHidden = false;

        modelPlayer.bipedLeftLeg.isHidden = false;
        modelPlayer.bipedLeftLegwear.isHidden = false;
        modelPlayer.bipedRightLeg.isHidden = false;
        modelPlayer.bipedRightLegwear.isHidden = false;
    }
}
