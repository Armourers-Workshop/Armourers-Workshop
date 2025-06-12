package moe.plushie.armourers_workshop.init.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ItemTooltipEvent {

    interface Gather {

        ItemStack itemStack();

        List<Component> tooltips();

        ITooltipContext context();
    }

    interface Render {

        ItemStack itemStack();

        CGRect frame();

        float screenWidth();

        float screenHeight();

        CGGraphicsContext context();
    }
}
