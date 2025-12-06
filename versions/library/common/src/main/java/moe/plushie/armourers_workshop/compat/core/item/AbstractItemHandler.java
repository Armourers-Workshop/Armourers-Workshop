package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public interface AbstractItemHandler {

    /**
     * Called when the player Left Clicks (attacks) an entity. Processed before
     * damage is done, if return value is true further processing is canceled and
     * the entity is not attacked.
     *
     * @param itemStack The Item being used
     * @param player    The player that is attacking
     * @param entity    The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    OpenInteractionResult attackLivingEntity(ItemStack itemStack, Player player, Entity entity);

    OpenInteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, OpenInteractionHand hand);

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    OpenInteractionResult useOnFirst(ItemStack itemStack, UseOnContext context);

    /**
     * fill all display item stack into creative mode tab.
     *
     * @param displayItems all display items.
     * @param tab          the current creative mode tab.
     */
    void fill(List<ItemStack> displayItems, CreativeModeTab tab);

    void appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder);

    int getModelTintColor(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex);
}
