package moe.plushie.armourers_workshop.compatibility.core.data;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

import java.util.HashMap;

@Available("[1.21, )")
public class AbstractDataComponentization {

    public static void init(DataFixerBuilder builder, Schema schema) {
        builder.addFixer(new SkinWardrobeFix(schema));
        builder.addFixer(new MannequinEntityFix(schema));
        builder.addFixer(new BlockEntityFixer(schema));
    }

    private static Dynamic<?> updateItem(Dynamic<?> item) {
        DataFixer dataFixer = DataFixers.getDataFixer();
        return dataFixer.update(References.ITEM_STACK, item, 3816, 3820);
    }

    private static Dynamic<?> updateItemList(Dynamic<?> list) {
        return list.createList(list.asStream().map(AbstractDataComponentization::updateItem));
    }

    public static class SkinWardrobeFix extends DataFix {

        public SkinWardrobeFix(Schema schema) {
            super(schema, false);
        }

        @Override
        protected TypeRewriteRule makeRule() {
            Schema schema = getInputSchema();
            return TypeRewriteRule.seq(
                    fixTypeEverywhereTyped("(AW) SkinWardrobeFix", schema.getType(References.ENTITY), typed -> typed.update(DSL.remainderFinder(), this::fix)),
                    fixTypeEverywhereTyped("(AW) SkinWardrobeFix", schema.getType(References.PLAYER), typed -> typed.update(DSL.remainderFinder(), this::fix)));
        }

        private Dynamic<?> fix(Dynamic<?> entityTag) {
            // only upgrade when old format exists.
            String registryName = "armourers_workshop:entity-skin-provider";
            Dynamic<?> oldWardrobe = entityTag.get(Constants.Key.OLD_CAPABILITY).get(registryName).result().orElse(null);
            if (oldWardrobe == null) {
                return entityTag;
            }
            // upgrade the inventory.
            Dynamic<?> newWardrobe = oldWardrobe.update("Items", AbstractDataComponentization::updateItemList);
            Dynamic<?> attachments = entityTag.get(Constants.Key.NEW_CAPABILITY).result().orElse(null);
            if (attachments == null) {
                attachments = entityTag.createMap(new HashMap<>());
            }
            return entityTag
                    .update(Constants.Key.OLD_CAPABILITY, caps -> caps.remove(registryName))
                    .set(Constants.Key.NEW_CAPABILITY, attachments.set(registryName, newWardrobe));
        }
    }

    public static class MannequinEntityFix extends NamedEntityFix {

        public MannequinEntityFix(Schema schema) {
            super(schema, false, "(AW) MannequinEntityFix", References.ENTITY, "armourers_workshop:mannequin");
        }

        @Override
        protected Typed<?> fix(Typed<?> typed) {
            return typed.update(DSL.remainderFinder(), data -> data
                    .update("HandItems", AbstractDataComponentization::updateItemList)
                    .update("ArmorItems", AbstractDataComponentization::updateItemList));
        }
    }

    public static class BlockEntityFixer extends DataFix {

        public BlockEntityFixer(Schema schema) {
            super(schema, false);
        }

        @Override
        protected TypeRewriteRule makeRule() {
            Schema inputSchema = getInputSchema();
            return fixTypeEverywhereTyped("(AW) BlockEntityFix", inputSchema.getType(References.BLOCK_ENTITY), typed -> typed.update(DSL.remainderFinder(), this::fix));
        }

        protected Dynamic<?> fix(Dynamic<?> entityTag) {
            String id = entityTag.get("id").asString().result().orElse(null);
            if (id == null || !id.startsWith("armourers_workshop:")) {
                return entityTag;
            }
            return entityTag.update("Items", AbstractDataComponentization::updateItemList);
        }
    }
}
