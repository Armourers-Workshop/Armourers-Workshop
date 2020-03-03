package moe.plushie.armourers_workshop.client.handler;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
        
        if (itemStack.getItem() == ModItems.SKIN) {
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
        
        ItemOverrideType overrideType = null;
        ISkinType skinType = null;
        
        for (int i = 0; i < ItemOverrideType.values().length; i++) {
            if (ModAddonManager.isOverrideItem(ItemOverrideType.values()[i], itemStack.getItem())) {
                overrideType = ItemOverrideType.values()[i];
                skinType = skinTypes[i];
            }
        }
        
        if (overrideType == null | skinType == null) {
            return;
        }
        
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        
        if (descriptor == null) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
            if (skinCapability != null) {
                if (ModAddonManager.isOverrideItem(overrideType, itemStack.getItem())) {
                    ISkinDescriptor descriptorItem = skinCapability.getSkinDescriptor(skinType, 0);
                    if (descriptorItem != null) {
                        descriptor = descriptorItem;
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
        GlStateManager.pushAttrib();
        renderItemInFirstPerson((AbstractClientPlayer) player, event.getPartialTicks(), event.getInterpolatedPitch(), event.getHand(), event.getSwingProgress(), itemStack, event.getEquipProgress());
        
        
        //int i = event.getHand() == EnumHand.MAIN_HAND ? 1 : -1;
        //GlStateManager.translate((float)i * 0.56F, -0.52F + event.getEquipProgress() * -0.6F, -0.72F);
        
        
        GlStateManager.enableCull();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        
        GlStateManager.scale(-1, -1, 1);
        GlStateManager.translate(0, 0.0625F * 1, 0.0625F * 1);
        if (flag) {
            GlStateManager.scale(-1, 1, 1);
            GlStateManager.cullFace(CullFace.FRONT);
        }
        if (overrideType != ItemOverrideType.BOW) {
            SkinItemRenderHelper.renderSkinWithoutHelper(descriptor, false);
        } else {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
            if (skin != null) {
                int useCount = player.getItemInUseMaxCount();
                ModelSkinBow model = SkinModelRenderHelper.INSTANCE.modelBow;
                model.frame = getAnimationFrame(useCount);
                model.render(player, skin, false, descriptor.getSkinDye(), null, false, 0, false);
            }
        }
        if (flag) {
            GlStateManager.cullFace(CullFace.BACK);
        }
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
    
    private int getAnimationFrame(int useCount) {
        if (useCount >= 18) {
            return 2;
        }
        if (useCount > 13) {
            return 1;
        }
        return 0;
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
                if (SkinNBTHelper.stackHasSkinData(armour[i])) {
                	player.inventory.armorInventory.set(i, ItemStack.EMPTY);
                } else if (wardrobeCapability.getArmourOverride(slot)) {
                    player.inventory.armorInventory.set(i, ItemStack.EMPTY);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRender(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        // Restore the players armour stacks.
        for (int i = 0; i < armour.length; i++) {
            player.inventory.armorInventory.set(i, armour[i]);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityPlayer> event) {
        EntitySkinCapability skinCapability = (EntitySkinCapability) EntitySkinCapability.get(event.getEntity());
        if (skinCapability == null) {
            return;
        }
        // Hide parts of the player model.
        for (RenderPlayer playerRender : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            ModelPlayer modelPlayer = playerRender.getMainModel();
            // Head
            if (skinCapability.hideHead) {
                modelPlayer.bipedHead.isHidden = true;
            }
            if (skinCapability.hideHead | skinCapability.hideHeadOverlay) {
                modelPlayer.bipedHeadwear.isHidden = true;
            }

            // Chest
            if (skinCapability.hideChest) {
                modelPlayer.bipedBody.isHidden = true;

            }
            if (skinCapability.hideChest | skinCapability.hideChestOverlay) {
                modelPlayer.bipedBodyWear.isHidden = true;
            }

            // Left arm
            if (skinCapability.hideArmLeft) {
                modelPlayer.bipedLeftArm.isHidden = true;
            }
            if (skinCapability.hideArmLeft | skinCapability.hideArmLeftOverlay) {
                modelPlayer.bipedLeftArmwear.isHidden = true;
            }

            // Right arm
            if (skinCapability.hideArmRight) {
                modelPlayer.bipedRightArm.isHidden = true;
            }
            if (skinCapability.hideArmRight | skinCapability.hideArmRightOverlay) {
                modelPlayer.bipedRightArmwear.isHidden = true;
            }

            // Left leg
            if (skinCapability.hideLegLeft) {
                modelPlayer.bipedLeftLeg.isHidden = true;
            }
            if (skinCapability.hideLegLeft | skinCapability.hideLegLeftOverlay) {
                modelPlayer.bipedLeftLegwear.isHidden = true;
            }

            // Right leg
            if (skinCapability.hideLegRight) {
                modelPlayer.bipedRightLeg.isHidden = true;
            }
            if (skinCapability.hideLegRight | skinCapability.hideLegRightOverlay) {
                modelPlayer.bipedRightLegwear.isHidden = true;
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderLivingPost(RenderLivingEvent.Post<EntityPlayer> event) {
        // Restore the player model.
        for (RenderPlayer playerRender : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            ModelPlayer modelPlayer = playerRender.getMainModel();
            modelPlayer.bipedHead.isHidden = false;
            modelPlayer.bipedHeadwear.isHidden = false;
            
            modelPlayer.bipedBody.isHidden = false;
            modelPlayer.bipedBodyWear.isHidden = false;
            
            modelPlayer.bipedLeftArm.isHidden = false;
            modelPlayer.bipedLeftArmwear.isHidden = false;
            
            modelPlayer.bipedRightArm.isHidden = false;
            modelPlayer.bipedRightArmwear.isHidden = false;

            modelPlayer.bipedLeftLeg.isHidden = false;
            modelPlayer.bipedLeftLegwear.isHidden = false;
            
            modelPlayer.bipedRightLeg.isHidden = false;
            modelPlayer.bipedRightLegwear.isHidden = false;
        }
    }
}
