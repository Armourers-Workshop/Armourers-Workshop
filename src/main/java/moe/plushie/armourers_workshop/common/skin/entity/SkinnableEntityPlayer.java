package moe.plushie.armourers_workshop.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.client.render.entity.ISkinnableEntityRenderer;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinnableEntityPlayer implements ISkinnableEntity {

    @Override
    public ArrayList<Class<? extends EntityLivingBase>> getEntityClass() {
        ArrayList<Class<? extends EntityLivingBase>> classes = new ArrayList<Class<? extends EntityLivingBase>>();
        classes.add(EntityPlayerSP.class);
        classes.add(EntityPlayerMP.class);
        return classes;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Class<? extends ISkinnableEntityRenderer> getRendererClass() {
        return null;
    }

    @Override
    public boolean canUseWandOfStyle() {
        return true;
    }

    @Override
    public boolean canUseSkinsOnEntity() {
        return false;
    }

    @Override
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
        skinTypes.add(SkinTypeRegistry.skinHead);
        skinTypes.add(SkinTypeRegistry.skinChest);
        skinTypes.add(SkinTypeRegistry.skinLegs);
        skinTypes.add(SkinTypeRegistry.skinFeet);
        skinTypes.add(SkinTypeRegistry.skinSword);
        skinTypes.add(SkinTypeRegistry.skinBow);
        skinTypes.add(SkinTypeRegistry.skinWings);
    }
    
    @Override
    public int getSlotsForSkinType(ISkinType skinType) {
        if (skinType.getVanillaArmourSlotId() != -1) {
            return 8;
        }
        if (skinType == SkinTypeRegistry.skinWings) {
            return 8;
        }
        return 1;
    }
}
