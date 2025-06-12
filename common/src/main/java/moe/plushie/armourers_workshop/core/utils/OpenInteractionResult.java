package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface OpenInteractionResult {

    Success SUCCESS = new Success(SwingSource.CLIENT, ItemContext.DEFAULT);
    Success SUCCESS_SERVER = new Success(SwingSource.SERVER, ItemContext.DEFAULT);
    Success CONSUME = new Success(SwingSource.NONE, ItemContext.DEFAULT);
    Fail FAIL = new Fail();
    Pass PASS = new Pass();
    TryEmptyHandInteraction TRY_WITH_EMPTY_HAND = new TryEmptyHandInteraction();

    default boolean consumesAction() {
        return false;
    }

    static Success sidedSuccess(boolean bl) {
        return bl ? SUCCESS : CONSUME;
    }

    final class Success implements OpenInteractionResult {

        private final SwingSource swingSource;
        private final ItemContext itemContext;

        public Success(SwingSource swingSource, ItemContext itemContext) {
            this.swingSource = swingSource;
            this.itemContext = itemContext;
        }

        @Override
        public boolean consumesAction() {
            return true;
        }

        public Success heldItemTransformedTo(ItemStack itemStack) {
            return new Success(swingSource, new ItemContext(true, itemStack));
        }

        public Success withoutItem() {
            return new Success(swingSource, ItemContext.NONE);
        }

        public boolean wasItemInteraction() {
            return itemContext.wasItemInteraction;
        }

        @Nullable
        public ItemStack heldItemTransformedTo() {
            return itemContext.heldItemTransformedTo;
        }

        public SwingSource swingSource() {
            return swingSource;
        }

        public ItemContext itemContext() {
            return itemContext;
        }
    }

    final class Fail implements OpenInteractionResult {
    }

    final class Pass implements OpenInteractionResult {
    }

    final class TryEmptyHandInteraction implements OpenInteractionResult {
    }

    enum SwingSource {
        NONE,
        CLIENT,
        SERVER
    }

    class ItemContext {
        private final boolean wasItemInteraction;
        private final ItemStack heldItemTransformedTo;

        static ItemContext NONE = new ItemContext(false, null);
        static ItemContext DEFAULT = new ItemContext(true, null);

        public ItemContext(boolean bl, @Nullable ItemStack itemStack) {
            this.wasItemInteraction = bl;
            this.heldItemTransformedTo = itemStack;
        }

        public boolean wasItemInteraction() {
            return wasItemInteraction;
        }

        @Nullable
        public ItemStack heldItemTransformedTo() {
            return heldItemTransformedTo;
        }
    }
}
