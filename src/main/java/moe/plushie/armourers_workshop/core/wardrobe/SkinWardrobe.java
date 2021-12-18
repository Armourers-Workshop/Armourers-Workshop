package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.action.IHasTag;
import moe.plushie.armourers_workshop.core.api.capabilities.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.render.cache.FastCache;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public class SkinWardrobe implements ISkinWardrobe, INBTSerializable<CompoundNBT> {

    private final static ArrayList<SkinWardrobe> WARDROBES = new ArrayList<>();

    public final WeakReference<LivingEntity> entityRef;
    //    public final NonNullList<ItemStack> items = NonNullList.withSize(64, ItemStack.EMPTY);

    public final List<BakedSkin> armorSkins = new ArrayList<>();
    public final List<BakedSkin> itemSkins = new ArrayList<>();


    public SkinWardrobe(LivingEntity entity) {
        this.entityRef = new WeakReference<>(entity);
    }


    @Nullable
    public static SkinWardrobe of(@Nullable LivingEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getCapability(SkinWardrobeProvider.WARDROBE_KEY).resolve().orElse(null);
    }

//    @Nonnull
//    @OnlyIn(Dist.CLIENT)
//    public Collection<BakedSkin> getSkins() {
//        return bakedSkins;
//    }

    @OnlyIn(Dist.CLIENT)
    public Collection<BakedSkin> getArmorSkins() {
        return armorSkins;
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<BakedSkin> getItemSkins(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        }
        List<BakedSkin> bakedSkins = new ArrayList<>();
        for (BakedSkin bakedSkin : itemSkins) {
            if (bakedSkin.isOverride(itemStack)) {
                bakedSkins.add(bakedSkin);
            }
        }
        return bakedSkins;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasPart(ISkinPartType partType) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEquipment(EquipmentSlotType slotType) {
        LivingEntity entity = entityRef.get();
        if (entity == null) {
            return false;
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasOverriddenPart(ISkinPartType partType) {
        return true;
    }


    //    public SkinWardrobe(CompoundNBT nbt) {
////        super(ItemStack.class);
////        this.capNBT = p_i47263_1_.contains("ForgeCaps") ? p_i47263_1_.getCompound("ForgeCaps") : null;
////        Item rawItem =
////                this.item = Registry.ITEM.get(new ResourceLocation(p_i47263_1_.getString("id")));
////        this.delegate = rawItem.delegate;
////        this.count = p_i47263_1_.getByte("Count");
////        if (p_i47263_1_.contains("tag", 10)) {
////            this.tag = p_i47263_1_.getCompound("tag");
////            this.getItem().verifyTagAfterLoad(p_i47263_1_);
////        }
////
////        if (this.getItem().isDamageable(this)) {
////            this.setDamageValue(this.getDamageValue());
////        }
////
////        this.updateEmptyCacheFlag();
////        this.forgeInit();
//    }

//    public CompoundNBT save(CompoundNBT nbt) {
////        ResourceLocation resourcelocation = Registry.ITEM.getKey(this.getItem());
////        p_77955_1_.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
////        p_77955_1_.putByte("Count", (byte)this.count);
////        if (this.tag != null) {
////            p_77955_1_.put("tag", this.tag.copy());
////        }
////        CompoundNBT cnbt = this.serializeCaps();
////        if (cnbt != null && !cnbt.isEmpty()) {
////            p_77955_1_.put("ForgeCaps", cnbt);
////        }
////        return p_77955_1_;
//        return nbt;
//    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
    }
}
