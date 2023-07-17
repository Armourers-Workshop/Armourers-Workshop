package moe.plushie.armourers_workshop.compatibility.core;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootContext;
import moe.plushie.armourers_workshop.api.common.ILootContextParam;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Available("[1.20, )")
public abstract class AbstractHorizontalBlockImpl extends HorizontalDirectionalBlock {

    public AbstractHorizontalBlockImpl(Properties properties) {
        super(properties);
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return this.getDrops(blockState, new LootContextBuilder(builder));
    }

    public List<ItemStack> getDrops(BlockState blockState, ILootContext context) {
        return super.getDrops(blockState, ((LootContextBuilder) context).builder);
    }

    public static class LootContextBuilder implements ILootContext {

        private static final ImmutableMap<ILootContextParam<?>, LootContextParam<?>> KEYS = ImmutableMap.<ILootContextParam<?>, LootContextParam<?>>builder()
                .put(ILootContextParam.THIS_ENTITY, LootContextParams.THIS_ENTITY)
                .put(ILootContextParam.LAST_DAMAGE_PLAYER, LootContextParams.LAST_DAMAGE_PLAYER)
                .put(ILootContextParam.DAMAGE_SOURCE, LootContextParams.DAMAGE_SOURCE)
                .put(ILootContextParam.KILLER_ENTITY, LootContextParams.KILLER_ENTITY)
                .put(ILootContextParam.DIRECT_KILLER_ENTITY, LootContextParams.DIRECT_KILLER_ENTITY)
                .put(ILootContextParam.ORIGIN, LootContextParams.ORIGIN)
                .put(ILootContextParam.BLOCK_STATE, LootContextParams.BLOCK_STATE)
                .put(ILootContextParam.BLOCK_ENTITY, LootContextParams.BLOCK_ENTITY)
                .put(ILootContextParam.TOOL, LootContextParams.TOOL)
                .put(ILootContextParam.EXPLOSION_RADIUS, LootContextParams.EXPLOSION_RADIUS)
                .build();

        private final LootParams.Builder builder;

        public LootContextBuilder(LootParams.Builder builder) {
            this.builder = builder;
        }

        @Override
        public <T> T getParameter(ILootContextParam<T> param) {
            Object value = builder.getParameter(KEYS.get(param));
            return param.getValueClass().cast(convert(value));
        }

        @Override
        @Nullable
        public <T> T getOptionalParameter(ILootContextParam<T> param) {
            Object value = builder.getOptionalParameter(KEYS.get(param));
            if (value != null) {
                return param.getValueClass().cast(convert(value));
            }
            return null;
        }

        private Object convert(Object value) {
            Vec3 pos = ObjectUtils.safeCast(value, Vec3.class);
            if (pos != null) {
                return new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
            }
            return value;
        }
    }
}
