package moe.plushie.armourers_workshop.init.provider;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public interface ClientNativeProvider {

    void willRegisterItemColor(Consumer<ItemColorRegistry> consumer);

    void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer);

    void willRegisterBlockColor(Consumer<BlockColorRegistry> consumer);

    void willRegisterTexture(Consumer<TextureRegistry> consumer);

    void willRegisterModel(Consumer<ModelRegistry> consumer);

    void willRegisterKeyMapping(Consumer<KeyMappingRegistry> consumer);

    void willPlayerLogin(Consumer<Player> consumer);

    void willPlayerLogout(Consumer<Player> consumer);

    void willTick(Consumer<Boolean> consumer);

    void willInput(Consumer<Minecraft> consumer);

    void willGatherTooltip(GatherTooltip consumer);

    void willRenderTooltip(RenderTooltip consumer);

    void entityRendererRegistry(Consumer<MyEntityRendererRegistry> consumer);

    interface ItemColorRegistry {
        void register(IItemTintColorProvider arg, Item... args);
    }

    interface ItemPropertyRegistry {
        void register(ResourceLocation registryName, Item item, IItemModelProperty property);
    }

    interface BlockColorRegistry {
        void register(IBlockTintColorProvider arg, Block... args);
    }

    interface TextureRegistry {
        void register(ResourceLocation sprite);
    }

    interface KeyMappingRegistry {
        void register(KeyMapping mapping);
    }

    interface ModelRegistry {
        void register(ResourceLocation registryName);
    }

    interface MyEntityRendererRegistry {

        <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, AbstractEntityRendererProvider<T> provider);

        <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, AbstractBlockEntityRendererProvider<T> provider);
    }

    interface GatherTooltip {

        void gather(ItemStack itemStack, List<Component> lines, TooltipFlag flag);
    }

    interface RenderTooltip {

        void render(ItemStack itemStack, CGRect frame, int mouseX, int mouseY, int screenWidth, int screenHeight, IPoseStack poseStack);
    }

    interface RenderLivingEntity {
        void render(LivingEntity entity, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer);
    }
}
