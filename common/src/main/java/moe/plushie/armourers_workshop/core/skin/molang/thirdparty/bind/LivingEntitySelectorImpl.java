package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.EffectSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.ItemSelector;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LivingEntitySelectorImpl<T extends LivingEntity> extends EntitySelectorImpl<T> implements LivingEntitySelector {

    private static final Map<String, Optional<OpenEquipmentSlot>> NAMED_SLOTS = new ConcurrentHashMap<>();

    private static final Map<String, OpenEquipmentSlot> FIXED_SLOTS = Collections.immutableMap(it -> {
        it.put("chest", OpenEquipmentSlot.CHEST);
        it.put("feet", OpenEquipmentSlot.FEET);
        it.put("head", OpenEquipmentSlot.HEAD);
        it.put("legs", OpenEquipmentSlot.LEGS);
        it.put("mainhand", OpenEquipmentSlot.MAINHAND);
        it.put("offhand", OpenEquipmentSlot.OFFHAND);
        it.put("body", OpenEquipmentSlot.BODY);
    });

    private final ItemSelectorImpl itemSelector = new ItemSelectorImpl();
    private final EffectSelectorImpl effectSelector = new EffectSelectorImpl();

    @Override
    public LivingEntitySelectorImpl<T> apply(T entity, ContextSelectorImpl context) {
        super.apply(entity, context);
        return this;
    }

    @Override
    public double bodyYaw() {
        return MathHelper.lerp(partialTick(), entity.xRotO, entity.getXRot());
    }

    @Override
    public double bodyPitch() {
        return MathHelper.wrapDegrees(MathHelper.lerp(partialTick(), entity.yRotO, entity.getYRot()));
    }

    @Override
    public double health() {
        return entity.getHealth();
    }

    @Override
    public double maxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public double armorValue() {
        return entity.getArmorValue();
    }

    @Override
    public double hurtTime() {
        return entity.hurtTime;
    }

    @Override
    public boolean isDeadOrDying() {
        return entity.isDeadOrDying();
    }

    @Override
    public boolean isEating() {
        return entity.getUseItem().getUseAnimation() == UseAnim.EAT;
    }

    @Override
    public boolean isSleeping() {
        return entity.isSleeping();
    }

    @Override
    public boolean isUsingItem() {
        return entity.isUsingItem();
    }

    @Override
    public boolean isAutoSpinAttack() {
        return entity.isAutoSpinAttack();
    }

    @Override
    public boolean isOnClimbable() {
        return entity.onClimbable();
    }

    @Override
    public double usingItemDuration() {
        return entity.getTicksUsingItem() / 20.0;
    }

    @Override
    public double usingItemMaxDuration() {
        var item = entity.getUseItem();
        if (!item.isEmpty()) {
            return item.getUseDuration(entity) / 20.0;
        }
        return 0;
    }

    @Override
    public double usingItemRemainingDuration() {
        return entity.getUseItemRemainingTicks() / 20.0;
    }

    @Override
    public int arrowCount() {
        return entity.getArrowCount();
    }

    @Override
    public int stingerCount() {
        return entity.getStingerCount();
    }

    @Override
    public double attributeValueByName(String name) {
        return AbstractRegistryManager.getAttribute(entity, name);
    }

    @Nullable
    @Override
    public EffectSelector effectByName(String name) {
        var effect = AbstractRegistryManager.getEffect(entity, name);
        if (effect != null) {
            return effectSelector.apply(effect);
        }
        return null;
    }

    @Nullable
    @Override
    public ItemSelector equippedItemBySlot(String name) {
        var slot = NAMED_SLOTS.computeIfAbsent(name, LivingEntitySelectorImpl::findSlot);
        var itemStack = slot.map(it -> entity.getItemBySlot(it)).orElse(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            return itemSelector.apply(itemStack);
        }
        return null;
    }


    @Override
    public int equipmentCount() {
        int count = 0;
        for (var slot : OpenEquipmentSlot.values()) {
            if (slot == OpenEquipmentSlot.MAINHAND || slot == OpenEquipmentSlot.OFFHAND) {
                continue;
            }
            var stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int lastClimbableFacing() {
        var level = entity.getLevel();
        if (level == null) {
            return 0;
        }
        var climbablePos = entity.getLastClimbablePos();
        if (!climbablePos.isPresent()) {
            return 0;
        }
        var blockState = level.getBlockState(climbablePos.get());
        var facing = blockState.getOptionalValue(HorizontalDirectionalBlock.FACING);
        //  0 south, 1 west, 2 north, 3 east
        return facing.map(Direction::get2DDataValue).orElse(0);
    }


    // https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/equipmentslot?view=minecraft-bedrock-stable
    private static Optional<OpenEquipmentSlot> findSlot(String name) {
        // [slot.]Head -> head
        return Optional.ofNullable(FIXED_SLOTS.get(name.toLowerCase()));
    }
}
