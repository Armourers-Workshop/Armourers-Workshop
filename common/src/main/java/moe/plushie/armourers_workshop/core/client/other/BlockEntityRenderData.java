package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.BlockEntityRenderPatch;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.DataStorage;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderData implements IAssociatedContainer, SkinBakery.IBakeListener {

    private final ArrayList<String> missingSkins = new ArrayList<>();

    private final Ticket loadTicket = Ticket.wardrobe();
    private final AnimationManager animationManager = new AnimationManager();
    private final DataStorage dataStorage = new DataStorage();
    private final ArrayList<SkinDescriptor> lastEquipmentSlots = new ArrayList<>();
    private final HashMap<SkinDescriptor, BakedSkin> lastActiveSkins = new HashMap<>();
    private final ArrayList<EntityRenderData.Entry> loadedAllSkins = new ArrayList<>();

    private Object customTextureProvider;
    private BlockEntityRenderPatch<? super BlockEntity> renderPatch;
    private ColorScheme colorScheme = ColorScheme.EMPTY;

    private boolean isListening = false;

    private int version = 0;
    private int lastVersion = Integer.MAX_VALUE;

    public BlockEntityRenderData(BlockEntity blockEntity) {
        this.renderPatch = new BlockEntityRenderPatch<>(blockEntity);
        this.renderPatch.setAnimationManager(animationManager);
    }

    @Nullable
    public static BlockEntityRenderData of(@Nullable BlockEntity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).getRenderData().orElse(null);
        }
        return null;
    }

    public void tick(BlockEntity blockEntity) {
        loadEquipmentSlots(blockEntity);
        if (this.lastVersion != this.version) {
            this.reload(blockEntity);
            this.lastVersion = this.version;
        }
        // check animation play status.
        this.animationManager.tick();
    }

    protected void reload(BlockEntity blockEntity) {
        invalidateAll();

        loadAllSlots(blockEntity);

        if (missingSkins.isEmpty()) {
            if (isListening) {
                SkinBakery.getInstance().removeListener(this);
                isListening = false;
            }
        } else {
            if (!isListening) {
                SkinBakery.getInstance().addListener(this);
                isListening = true;
            }
        }

        animationManager.load(lastActiveSkins);
    }

    protected void invalidateAll() {
        lastVersion = Integer.MAX_VALUE;

        loadedAllSkins.clear();
        lastActiveSkins.clear();
        missingSkins.clear();

        loadTicket.invalidate();
    }

    protected void loadEquipmentSlots(BlockEntity blockEntity) {
        int index = 0;
        for (var descriptor : getOverrideSlots(blockEntity)) {
            if (index >= lastEquipmentSlots.size()) {
                lastEquipmentSlots.add(descriptor);
                version += 1;
            } else if (lastVersion != version || lastEquipmentSlots.get(index) != descriptor) {
                lastEquipmentSlots.set(index, descriptor);
                version += 1;
            }
            index += 1;
        }
        // clear expired item stack
        while (index < lastEquipmentSlots.size()) {
            lastEquipmentSlots.remove(index);
        }
    }

    private void loadAllSlots(BlockEntity blockEntity) {
        int i = 0;
        for (var descriptor : lastEquipmentSlots) {
            updateSkin(descriptor, 400 + i++);
        }
    }

    private void updateSkin(SkinDescriptor descriptor, float renderPriority) {
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
        lastActiveSkins.put(descriptor, bakedSkin);
        if (bakedSkin == null) {
            missingSkins.add(descriptor.getIdentifier());
            return;
        }
        var entry = new EntityRenderData.Entry(ItemStack.EMPTY, descriptor, bakedSkin, colorScheme, renderPriority, false);
        loadedAllSkins.add(entry);
    }


    private Iterable<SkinDescriptor> getOverrideSlots(BlockEntity blockEntity) {
        if (blockEntity instanceof SkinnableBlockEntity blockEntity1) {
            return Collections.singleton(blockEntity1.getDescriptor());
        }
        return Collections.emptyList();
    }


    @Override
    public void didBake(String identifier, BakedSkin bakedSkin) {
        if (missingSkins.contains(identifier)) {
            RenderSystem.call(this::invalidateAll);
        }
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        return dataStorage.getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(T value, IAssociatedContainerKey<T> key) {
        dataStorage.setAssociatedObject(value, key);
    }

    public List<EntityRenderData.Entry> getAllSkins() {
        return loadedAllSkins;
    }

    public BlockEntityRenderPatch<? super BlockEntity> getRenderPatch() {
        return renderPatch;
    }

    public void setCustomTextureProvider(Object customTextureProvider) {
        this.customTextureProvider = customTextureProvider;
    }

    public Object getCustomTextureProvider() {
        return customTextureProvider;
    }
}
