package moe.plushie.armourers_workshop.init.event.client;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface ItemTooltipEvent {

    interface Gather {

        ItemStack itemStack();

        List<Component> tooltips();

        ITooltipContext context();
    }

    interface Render {

        void draw(Consumer<CGGraphicsContext> action);

        ItemStack itemStack();

        CGRect frame();

        float screenWidth();

        float screenHeight();
    }
}
