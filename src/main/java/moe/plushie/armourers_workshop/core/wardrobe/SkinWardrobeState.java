package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.collect.Iterators;
import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.render.other.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinDye;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeState {

    public final ArrayList<BakedSkin> armorSkins = new ArrayList<>();
    public final ArrayList<BakedSkin> itemSkins = new ArrayList<>();

    public final HashSet<ISkinPartType> hasOverriddenParts = new HashSet<>();
    public final HashSet<ISkinPartType> hasParts = new HashSet<>();

    public SkinDye dye = new SkinDye();

    public void invalidateAll() {
    }

    public void loadSkinTheme(Entity entity, List<ItemStack> itemStacks) {




    }

    public void loadSkin(Entity entity, List<ItemStack> itemStacks) {
        Iterator<ItemStack> slots = itemStacks.iterator();
        Iterator<ItemStack> handSlots = Iterators.singletonIterator(ItemStack.EMPTY);

        if (entity != null) {
            slots = Iterators.concat(slots, entity.getArmorSlots().iterator());
            handSlots = entity.getHandSlots().iterator();
        }

        Iterators.transform(slots, SkinDescriptor::of).forEachRemaining(descriptor -> loadSkin(descriptor, true));
        Iterators.transform(handSlots, SkinDescriptor::of).forEachRemaining(descriptor -> loadSkin(descriptor, false));

        armorSkins.forEach(this::loadSkinPart);
        itemSkins.forEach(this::loadSkinPart);
    }

    private void loadSkin(SkinDescriptor descriptor, boolean allowsArmor) {
        if (descriptor.isEmpty()) {
            return;
        }
        BakedSkin skin = null;
        String s = descriptor.identifier;
        int iq = Integer.parseInt(s);
        if (iq < BakedSkin.skins.size()) {
            skin = BakedSkin.skins.get(iq);
        }
        if (skin == null) {
            return;
        }
        ISkinType type = skin.getSkin().getType();
        if (type instanceof ISkinArmorType && allowsArmor) {
            armorSkins.add(skin);
        }
        if (type instanceof ISkinToolType) {
            itemSkins.add(skin);
        }
    }

    private void loadSkinPart(BakedSkin skin) {
        for (SkinPart part : skin.getSkin().getRenderParts()) {
            ISkinPartType partType = part.getType();
            hasParts.add(partType);
            if (partType.isModelOverridden(part.getProperties())) {
                hasOverriddenParts.add(partType);
            }
        }
    }
}
