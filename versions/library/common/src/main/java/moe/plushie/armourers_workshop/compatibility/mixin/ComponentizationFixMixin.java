package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractDataComponentization;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.PlayerHeadBlockProfileFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, )")
public class ComponentizationFixMixin {

    @Mixin(targets = "net.minecraft.util.datafix.fixes.ItemStackComponentizationFix$ItemStackData")
    public static abstract class ItemFixer {

        @Shadow
        public abstract void moveTagToComponent(String string, String string2);

        @Inject(method = "<init>", at = @At("RETURN"))
        private void aw2$fixItemStack(String id, int i, Dynamic<?> dynamic, CallbackInfo ci) {

            // migrate attached skin fields.
            moveTagToComponent("ArmourersWorkshop", "armourers_workshop:skin");

            // only migrate we item fields.
            if (!id.startsWith("armourers_workshop:")) {
                return;
            }

            moveTagToComponent("Gift", "armourers_workshop:gift");
            moveTagToComponent("Holiday", "armourers_workshop:holiday");

            moveTagToComponent("Color", "armourers_workshop:color");
            moveTagToComponent("Color1", "armourers_workshop:color1");
            moveTagToComponent("Color2", "armourers_workshop:color2");

            moveTagToComponent("Flags", "armourers_workshop:tool_flags");
            moveTagToComponent("Options", "armourers_workshop:tool_options");

            moveTagToComponent("LinkedPos", "armourers_workshop:linked_pos");
        }
    }

    @Mixin(DataFixers.class)
    public static abstract class EntityFixer {

        @Redirect(method = "addFixers", at = @At(value = "NEW", target = "(Lcom/mojang/datafixers/schemas/Schema;)Lnet/minecraft/util/datafix/fixes/PlayerHeadBlockProfileFix;"))
        private static PlayerHeadBlockProfileFix aw$fixEntity(Schema schema, DataFixerBuilder builder) {
            AbstractDataComponentization.init(builder, schema);
            return new PlayerHeadBlockProfileFix(schema);
        }
    }
}

