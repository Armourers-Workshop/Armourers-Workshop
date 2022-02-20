package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWTags;
import moe.plushie.armourers_workshop.core.utils.ContainerOpener;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MannequinEntity extends ArmorStandEntity {

//    private static final DataParameter<BipedRotations> DATA_BIPED_ROTATIONS = EntityDataManager.<BipedRotations>createKey(EntityMannequin.class, BIPED_ROTATIONS_SERIALIZER);
//    private static final DataParameter<TextureData> DATA_TEXTURE_DATA = EntityDataManager.<TextureData>createKey(EntityMannequin.class, TEXTURE_DATA_SERIALIZER);
//    private static final DataParameter<Float> DATA_ROTATION = EntityDataManager.<Float>createKey(EntityMannequin.class, DataSerializers.FLOAT);
//    private static final DataParameter<Boolean> DATA_RENDER_EXTRAS = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
//    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
//    private static final DataParameter<Boolean> DATA_VISIBLE = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
//    private static final DataParameter<Boolean> DATA_NO_CLIP = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
//    private static final DataParameter<Float> DATA_SCALE = EntityDataManager.<Float>createKey(EntityMannequin.class, DataSerializers.FLOAT);
//    private static final DataParameter<ItemStack> DATA_HAND_LEFT = EntityDataManager.<ItemStack>createKey(EntityMannequin.class, DataSerializers.ITEM_STACK);
//    private static final DataParameter<ItemStack> DATA_HAND_RIGHT = EntityDataManager.<ItemStack>createKey(EntityMannequin.class, DataSerializers.ITEM_STACK);


    public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d pos, Hand hand) {
        if (player.level.isClientSide) {
            return ActionResultType.CONSUME;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(this);
        if (wardrobe != null) {
            ContainerOpener.openContainer(SkinWardrobeContainer.TYPE, player, wardrobe);
        }
        return ActionResultType.SUCCESS;
    }


    public IInventory getInventory() {
        return new Inventory(getMainHandItem(), getOffhandItem()) {
            @Override
            public void setItem(int index, ItemStack itemStack) {
                super.setItem(index, itemStack);
                setItemSlot(EquipmentSlotType.values()[index], itemStack);
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack itemStack) {
                if (itemStack.isEmpty()) {
                    return true;
                }
                SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
                if (descriptor.getType() instanceof ISkinToolType) {
                    return true;
                }
                Item item = itemStack.getItem();
                return AWTags.isWeaponItem(item) || AWTags.isToolItem(item);
            }
        };
    }
}
