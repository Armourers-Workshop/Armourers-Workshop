package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.utils.AWContainerOpener;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.container.WardrobeContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


@SuppressWarnings("NullableProblems")
public class WandOfStyleItem extends FlavouredItem {

    public WandOfStyleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, PlayerEntity player, Entity entity) {
        if (!player.level.isClientSide) {
            openGUI(player, entity);
        }
        return true;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.level.isClientSide) {
            openGUI(player, entity);
        }
        return ActionResultType.sidedSuccess(player.level.isClientSide);
    }

    private void openGUI(PlayerEntity player, Entity entity) {
        Wardrobe wardrobe = Wardrobe.of(entity);
        if (wardrobe != null && wardrobe.getProfile().canCustomize()) {
            AWContainerOpener.open(WardrobeContainer.TYPE, player, wardrobe);
        }
    }
}
