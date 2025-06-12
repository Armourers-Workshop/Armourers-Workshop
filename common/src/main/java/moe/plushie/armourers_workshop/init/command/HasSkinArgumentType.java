package moe.plushie.armourers_workshop.init.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.LazyValue;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// @p[skin={type=outfit,slot=1,id="<domain>:<skin-id>"}]
public class HasSkinArgumentType implements ArgumentType<EntitySelectorPredicate> {

    private static final DynamicCommandExceptionType ERROR_MISSING_KEY = new DynamicCommandExceptionType(ob -> Component.translatable("commands.armourers_workshop.armourers.error.unknownHasSkinKey", ob));

    private static final Map<String, LazyValue<ArgumentType<?>>> PROPERTIES = Collections.immutableMap(it -> {
        it.put("id", LazyValue.of(() -> StringArgumentType.string()));
        it.put("slot", LazyValue.of(() -> IntegerArgumentType.integer(1)));
        it.put("type", LazyValue.of(() -> new ListArgumentType(Collections.compactMap(SkinSlotType.values(), SkinSlotType::serializedName))));
    });

    @Override
    public EntitySelectorPredicate parse(StringReader reader) throws CommandSyntaxException {
        var conditions = new HashMap<String, Object>();
        reader.expect('{');
        reader.skipWhitespace();
        while (reader.canRead() && reader.peek() != '}') {
            reader.skipWhitespace();
            var key = reader.readUnquotedString();
            reader.skipWhitespace();
            reader.expect('=');
            reader.skipWhitespace();
            var valueProvider = PROPERTIES.get(key);
            if (valueProvider == null) {
                throw ERROR_MISSING_KEY.create(key);
            }
            var value = valueProvider.get().parse(reader);
            conditions.put(key, value);
            reader.skipWhitespace();
            if (reader.canRead() && reader.peek() == ',') {
                reader.skip();
            }
        }
        reader.expect('}');
        return new PredicateImpl(conditions);
    }


    private static class PredicateImpl implements EntitySelectorPredicate {

        private final Map<String, Object> conditions;

        public PredicateImpl(Map<String, Object> conditions) {
            this.conditions = conditions;
        }

        @Override
        public boolean test(Entity entity) {
            var wardrobe = SkinWardrobe.of(entity);
            if (wardrobe == null) {
                return false;
            }
            var id = Objects.safeCast(conditions.get("id"), String.class);
            var type = Objects.flatMap(conditions.get("type"), it -> SkinSlotType.byName((String) it));
            var index = Objects.safeCast(conditions.get("slot"), Integer.class);

            for (var type1 : SkinSlotType.values()) {
                if (type != null && type != type1) {
                    continue;
                }
                if (hasSkinInSlot(id, index, type1, wardrobe)) {
                    return true;
                }
            }

            return false;
        }

        private boolean hasSkinInSlot(String skinId, @Nullable Integer slotIndex, SkinSlotType slotType, SkinWardrobe wardrobe) {
            int maxSlot = wardrobe.getMaximumSize(slotType);
            for (int i = 0; i < maxSlot; ++i) {
                if (slotIndex != null && i + 1 != slotIndex) {
                    continue;
                }
                var descriptor = SkinDescriptor.of(wardrobe.getItem(slotType, i));
                if (skinId != null) {
                    return skinId.equals(descriptor.identifier()); // has specified skin?
                }
                return !descriptor.isEmpty(); // has any skin?
            }
            return false;
        }
    }
}
