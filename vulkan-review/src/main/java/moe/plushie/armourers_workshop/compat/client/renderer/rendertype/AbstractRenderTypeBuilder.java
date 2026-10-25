package moe.plushie.armourers_workshop.compat.client.renderer.rendertype;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import moe.plushie.armourers_workshop.api.core.IResourceKey;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractVertexFormat;
import moe.plushie.armourers_workshop.compat.extensions.com.mojang.blaze3d.pipeline.RenderPipeline.BuilderExt;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexFormat;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType.BlendMode;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType.DepthTestMode;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType.PolygonMode;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Target;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.resources.Identifier;

public abstract class AbstractRenderTypeBuilder extends moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder {
   public static SkinRenderType create(RenderType renderType) {
      return (SkinRenderType)DataContainer.of(renderType, AbstractRenderTypeBuilder.Wrapper::new);
   }

   public static moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder create(SkinVertexFormat format) {
      Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder> provider = AbstractRenderTypeBuilder.Provider.INSTANCES.get(format);
      if (provider != null) {
         return provider.get();
      } else {
         throw new RuntimeException("can't supported render mode");
      }
   }

   private static class Provider {
      private static final Map<SkinVertexFormat, Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>> INSTANCES = Collections.immutableMap(
         it -> {
            it.put(
               SkinVertexFormat.LINE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_COLOR, PrimitiveTopology.DEBUG_LINES, RenderPipelines.GUI
               )
            );
            it.put(
               SkinVertexFormat.LINE_STRIP,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_COLOR, PrimitiveTopology.DEBUG_LINE_STRIP, RenderPipelines.GUI
               )
            );
            it.put(
               SkinVertexFormat.BLIT_MASK,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_COLOR, PrimitiveTopology.TRIANGLES, RenderPipelines.GUI
               )
            );
            it.put(
               SkinVertexFormat.BLIT_TEXTURED,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_TEX_COLOR, PrimitiveTopology.QUADS, RenderPipelines.GUI_TEXTURED
               )
            );
            it.put(
               SkinVertexFormat.GUI_COLOR,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_COLOR, PrimitiveTopology.QUADS, RenderPipelines.GUI
               )
            );
            it.put(
               SkinVertexFormat.GUI_TEXTURED,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.POSITION_TEX_COLOR, PrimitiveTopology.QUADS, RenderPipelines.GUI_TEXTURED
               )
            );
            it.put(
               SkinVertexFormat.BLOCK,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_SOLID
               )
            );
            it.put(
               SkinVertexFormat.BLOCK_CUTOUT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_CUTOUT
               )
            );
            it.put(
               SkinVertexFormat.ENTITY_CUTOUT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_CUTOUT
               )
            );
            it.put(
               SkinVertexFormat.ENTITY_CUTOUT_NO_CULL,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_CUTOUT
                  )
                  .cull(false)
            );
            it.put(
               SkinVertexFormat.ENTITY_TRANSLUCENT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_TRANSLUCENT
               )
            );
            it.put(
               SkinVertexFormat.ENTITY_ALPHA,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                  DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_SHADOW
               )
            );
            it.put(
               SkinVertexFormat.SKIN_PARTICLE_CUTOUT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.TRANSLUCENT_PARTICLE
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_PARTICLE_CUTOUT_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.TRANSLUCENT_PARTICLE
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_BLOCK_FACE_SOLID,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_SOLID
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_BLOCK_FACE_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_SOLID
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_BLOCK_FACE_TRANSLUCENT_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_CUBE_FACE_SOLID,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_CUTOUT
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_CUBE_FACE_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_CUTOUT
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_CUBE_FACE_TRANSLUCENT_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.QUADS, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_MESH_FACE_SOLID,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.TRIANGLES, RenderPipelines.ENTITY_CUTOUT
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_MESH_FACE_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.TRIANGLES, RenderPipelines.ENTITY_CUTOUT
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
            it.put(
               SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.TRIANGLES, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
            );
            it.put(
               SkinVertexFormat.SKIN_MESH_FACE_TRANSLUCENT_EMISSIVE,
               (Supplier<moe.plushie.armourers_workshop.core.client.other.SkinRenderType.Builder>)() -> _builder(
                     DefaultVertexFormat.ENTITY, PrimitiveTopology.TRIANGLES, RenderPipelines.ENTITY_TRANSLUCENT
                  )
                  .overlay()
                  .lightmap()
                  .emissive()
            );
         }
      );

      private static AbstractRenderTypeBuilder.Provider.Builder _builder(VertexFormat format, PrimitiveTopology mode, RenderPipeline pipeline) {
         return new AbstractRenderTypeBuilder.Provider.Builder(format, mode, pipeline);
      }

      private static class Builder extends AbstractRenderTypeBuilder {
         private boolean isOutline = false;
         private boolean affectsCrumbling = false;
         private boolean sortOnUpload = false;
         private boolean useLightmap = false;
         private boolean useOverlay = false;
         private Identifier usedTexture;
         private OutputTarget outputTarget = OutputTarget.MAIN_TARGET;
         private Optional<BlendFunction> blendFunction = Optional.empty();
         private CompareOp depthTest = CompareOp.GREATER_THAN_OR_EQUAL;
         private boolean writeDepth = true;
         private int writeMask = 15;
         private float depthBiasScaleFactor = 0.0F;
         private float depthBiasConstant = 0.0F;
         private final RenderPipeline parent;
         private final com.mojang.blaze3d.pipeline.RenderPipeline.Builder pipelineBuilder;

         private Builder(VertexFormat format, PrimitiveTopology mode, RenderPipeline parent) {
            this.parent = parent;
            DepthStencilState depth = parent.getDepthStencilState();
            this.depthTest = depth == null ? CompareOp.ALWAYS_PASS : depth.depthTest();
            this.writeDepth = depth != null && depth.writeDepth();
            this.pipelineBuilder = BuilderExt.builder(RenderPipeline.class, parent).withVertexBinding(0, format).withPrimitiveTopology(mode);
            this.setupDefault();
         }

         private void setupDefault() {
            this.pipelineBuilder.withCull(false);
         }

         public AbstractRenderTypeBuilder.Provider.Builder texture(IResourceKey texture, boolean blur, boolean mipmap) {
            this.usedTexture = texture.get();
            this.pipelineBuilder.withBindGroupLayout(BindGroupLayout.builder().withSampler("Sampler0").build());
            super.texture(texture, blur, mipmap);
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder target(Target target) {
            this.outputTarget = switch (target) {
               case MAIN, TRANSLUCENT -> OutputTarget.MAIN_TARGET;
               case OUTLINE -> OutputTarget.OUTLINE_TARGET;
               case CLOUDS, WEATHER -> OutputTarget.WEATHER_TARGET;
               case PARTICLES, ITEM_ENTITY -> OutputTarget.ITEM_ENTITY_TARGET;
               default -> throw new MatchException(null, null);
            };
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder blend(BlendMode mode) {
            this.blendFunction = switch (mode) {
               case NONE -> Optional.empty();
               case NORMAL -> Optional.empty();
               case LIGHTNING -> Optional.of(BlendFunction.LIGHTNING);
               case GLINT -> Optional.of(BlendFunction.GLINT);
               case OVERLAY -> Optional.of(BlendFunction.OVERLAY);
               case TRANSLUCENT -> Optional.of(BlendFunction.TRANSLUCENT);
               case TRANSLUCENT_PREMULTIPLIED_ALPHA -> Optional.of(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA);
               case ADDITIVE -> Optional.of(BlendFunction.ADDITIVE);
               case INVERT -> Optional.of(BlendFunction.INVERT);
               default -> throw new MatchException(null, null);
            };
            super.blend(mode);
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder colorWrite(boolean writeColor, boolean writeAlpha) {
            int writeMask = 0;
            if (writeColor) {
               writeMask |= 7;
            }

            if (writeAlpha) {
               writeMask |= 8;
            }

            this.writeMask = writeMask;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder depthWrite(boolean writeDepth) {
            this.writeDepth = writeDepth;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder depthTest(DepthTestMode mode) {
            this.depthTest = switch (mode) {
               case NO_DEPTH_TEST -> CompareOp.ALWAYS_PASS;
               case EQUAL_DEPTH_TEST -> CompareOp.EQUAL;
               case LEQUAL_DEPTH_TEST -> CompareOp.GREATER_THAN_OR_EQUAL;
               case LESS_DEPTH_TEST -> CompareOp.GREATER_THAN;
               case GREATER_DEPTH_TEST -> CompareOp.LESS_THAN;
               default -> throw new MatchException(null, null);
            };
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder polygonMode(PolygonMode mode) {
            this.pipelineBuilder.withPolygonMode(switch (mode) {
               case FILL -> com.mojang.blaze3d.platform.PolygonMode.FILL;
               case WIREFRAME -> com.mojang.blaze3d.platform.PolygonMode.WIREFRAME;
               default -> throw new MatchException(null, null);
            });
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder polygonOffset(float factor, float units) {
            this.depthBiasScaleFactor = factor;
            this.depthBiasConstant = units;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder stroke(float width) {
            this.pipelineBuilder.withPolygonMode(com.mojang.blaze3d.platform.PolygonMode.WIREFRAME);
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder cull(boolean flag) {
            this.pipelineBuilder.withCull(flag);
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder lightmap(boolean flag) {
            this.useLightmap = flag;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder emissive(boolean flag) {
            super.emissive(flag);
            this.pipelineBuilder.withShaderDefine("EMISSIVE").withShaderDefine("NO_CARDINAL_LIGHTING");
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder overlay(boolean flag) {
            this.useOverlay = flag;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder outline(boolean flag) {
            this.isOutline = flag;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder crumbling(boolean flag) {
            this.affectsCrumbling = flag;
            return this;
         }

         public AbstractRenderTypeBuilder.Provider.Builder sortOnUpload(boolean flag) {
            this.sortOnUpload = flag;
            return this;
         }

         protected SkinRenderType create(String registryName) {
            RenderPipeline pipeline = this.createPipeline(registryName);
            RenderSetupBuilder setupBuilder = RenderSetup.builder(pipeline);
            setupBuilder.setOutputTarget(this.outputTarget);
            setupBuilder.setOutline(this.isOutline ? OutlineProperty.AFFECTS_OUTLINE : OutlineProperty.NONE);
            if (this.usedTexture != null) {
               setupBuilder.withTexture("Sampler0", this.usedTexture);
            }

            if (this.useLightmap) {
               setupBuilder.useLightmap();
            }

            if (this.useOverlay) {
               setupBuilder.useOverlay();
            }

            if (this.affectsCrumbling) {
               setupBuilder.affectsCrumbling();
            }

            if (this.sortOnUpload) {
               setupBuilder.sortOnUpload();
            }

            try {
               var constructor = RenderType.class.getDeclaredConstructor(String.class, RenderSetup.class);
               constructor.setAccessible(true);
               return AbstractRenderType.wrap(constructor.newInstance(registryName, setupBuilder.createRenderSetup()));
            } catch (ReflectiveOperationException exception) {
               throw new IllegalStateException("Cannot create native Armourers Workshop render type", exception);
            }
         }

         private RenderPipeline createPipeline(String registryName) {
            return this.pipelineBuilder
               .withLocation(Identifier.parse(registryName.replace(":rendertype/", ":pipeline/")))
               .withColorTargetState(new ColorTargetState(this.blendFunction, GpuFormat.RGBA8_UNORM, this.writeMask))
               .withDepthStencilState(new DepthStencilState(this.depthTest, this.writeDepth, -this.depthBiasScaleFactor, -this.depthBiasConstant))
               .build();
         }
      }
   }

   private static class Wrapper extends SkinRenderType {
      private final RenderType impl;

      private Wrapper(RenderType renderType) {
         this.format = AbstractVertexFormat.create(renderType.format(), renderType.primitiveTopology());
         this.outline = renderType.outline().map(AbstractRenderType::wrap);
         this.isOutline = renderType.isOutline();
         this.name = renderType.toString().replaceAll("RenderType\\[(.+?):CompositeState.+$", "$1");
         this.bufferSize = 4194304;
         this.impl = renderType;
      }

      public RenderType get() {
         return this.impl;
      }
   }
}
