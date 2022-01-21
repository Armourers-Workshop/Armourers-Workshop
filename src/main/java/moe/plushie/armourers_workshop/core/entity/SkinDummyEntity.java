package moe.plushie.armourers_workshop.core.entity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@SuppressWarnings("all")
@OnlyIn(Dist.CLIENT)
public class SkinDummyEntity extends LivingEntity {

    private static SkinDummyEntity INSTANCE;

    private final BipedModel<SkinDummyEntity> model = new BipedModel<>(0);

    public SkinDummyEntity() {
        super(EntityType.ARMOR_STAND, null);
        this.setupDefaultModel();
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slotType) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotType, ItemStack itemStack) {
    }

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }

    public BipedModel<SkinDummyEntity> getModel() {
        return model;
    }

    private void setupDefaultModel() {
        this.model.young = false;
        this.model.crouching = false;
        this.model.riding = false;
        this.model.prepareMobModel(this, 0, 0, 0);
        this.model.setupAnim(this, 0, 0, 0, 0, 0);
    }

    public static SkinDummyEntity shared() {
        if (INSTANCE == null) {
            INSTANCE = new SkinDummyEntity();
        }
        return INSTANCE;
    }
}
