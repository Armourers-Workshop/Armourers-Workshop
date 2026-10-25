package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.GpuFormat.ComponentType;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.ArrayList;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.NativeGuiClip;
import moe.plushie.armourers_workshop.compat.client.gui.renderer.NativeGuiRenderTypes;
import java.util.List;
import java.util.Map;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.FormattedCharSequence;

public class AbstractBufferSource implements IBufferSource {
   private static final ThreadLocal<SubmitNodeCollector> ACTIVE_COLLECTOR = new ThreadLocal<>();
   private static final SubmitNodeStorage DEFAULT_SUBMITS = new SubmitNodeStorage();
   private static final AbstractBufferSource DEFAULT = new AbstractBufferSource(DEFAULT_SUBMITS, 80000);
   private static final AbstractBufferSource OUTLINE = new AbstractBufferSource(DEFAULT_SUBMITS, 80000);
   private static final AbstractBufferSource TESSELATOR = new AbstractBufferSource(DEFAULT_SUBMITS, 80000);
   private final SubmitNodeCollector submitNodeCollector;
   private final int bufferSize;
   private final PoseStack poseStack = new PoseStack();
   private final ArrayList<ByteBufferBuilder> allocations = new ArrayList<>();
   private final Map<RenderType, BufferBuilder> builders = new HashMap<>();
   private final Map<RenderType, IVertexConsumer> consumers = new HashMap<>();

   private AbstractBufferSource(SubmitNodeCollector submitNodeCollector, int bufferSize) {
      this.submitNodeCollector = submitNodeCollector;
      this.bufferSize = bufferSize;
   }

   public static SubmitNodeCollector setActiveCollector(SubmitNodeCollector collector) {
      SubmitNodeCollector previous = ACTIVE_COLLECTOR.get();
      if (collector == null) ACTIVE_COLLECTOR.remove(); else ACTIVE_COLLECTOR.set(collector);
      return previous;
   }

   private SubmitNodeCollector collector() {
      SubmitNodeCollector active = ACTIVE_COLLECTOR.get();
      return submitNodeCollector == DEFAULT_SUBMITS && active != null ? active : submitNodeCollector;
   }

   private ByteBufferBuilder allocate() {
      ByteBufferBuilder allocation = new ByteBufferBuilder(bufferSize);
      allocations.add(allocation);
      return allocation;
   }

   public static AbstractBufferSource shared() {
      return DEFAULT;
   }

   public static AbstractBufferSource outline() {
      return OUTLINE;
   }

   public static AbstractBufferSource tesselator() {
      return TESSELATOR;
   }

   public static AbstractBufferSource create(int size) {
      return new AbstractBufferSource(DEFAULT_SUBMITS, size);
   }

   public static AbstractBufferSource create(SubmitNodeCollector collector) {
      return new AbstractBufferSource(collector, 80000);
   }

   public static AbstractBufferSource wrap(SubmitNodeCollector collector) {
      return create(collector);
   }

   public IVertexConsumer getBuffer(IRenderType renderType) {
      moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager.getInstance().pin(renderType);
      RenderType type = (RenderType)renderType.get();
      return this.consumers.computeIfAbsent(type, key -> {
         BufferBuilder builder = new BufferBuilder(allocate(), key.primitiveTopology(), key.format());
         this.builders.put(key, builder);
         return AbstractVertexConsumer.wrap(builder);
      });
   }

   public void endBatch() {
      try {
      this.builders.forEach((renderType, builder) -> {
         MeshData mesh = builder.build();
         if (mesh != null) {
            MeshData twrVar0$ = mesh;

            try {
               DrawState state = mesh.drawState();
               ByteBuffer source = mesh.vertexBuffer();
               byte[] bytes = new byte[source.remaining()];
               source.get(bytes);
               VertexFormat format = state.format();
               int vertexCount = state.vertexCount();
               int stride = format.getVertexSize();
               NativeGuiClip.Snapshot clip = NativeGuiClip.snapshot();
               NativeGuiClip.ordered(collector()).submitCustomGeometry(this.poseStack, clip.shapes().isEmpty() ? renderType : NativeGuiRenderTypes.independent(renderType), (pose, output) -> {
                  NativeGuiClip.ClippedConsumer clipped = clip.wrap(output, renderType.primitiveTopology());
                  VertexConsumer target = clipped == null ? output : clipped;
                  ByteBuffer data = ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder());

                  for (int vertex = 0; vertex < vertexCount; vertex++) {
                     emitVertex(data, vertex * stride, format.getElements(), target);
                  }
                  if (clipped != null) clipped.finish();
               });
            } catch (Throwable t$) {
               if (twrVar0$ != null) {
                  try {
                     twrVar0$.close();
                  } catch (Throwable x2) {
                     t$.addSuppressed(x2);
                  }
               }

               throw t$;
            }

            if (twrVar0$ != null) {
               twrVar0$.close();
            }
         }
      });
      } finally {
         this.builders.clear();
         this.consumers.clear();
         this.allocations.forEach(ByteBufferBuilder::close);
         this.allocations.clear();
      }
   }

   public void submitText(Font font, Iterable<FormattedCharSequence> lines, float x, float y, int color, boolean shadow) {
      submitText(font, this.poseStack, lines, x, y, color, shadow);
   }

   public void submitText(Font font, PoseStack textPose, Iterable<FormattedCharSequence> lines, float x, float y, int color, boolean shadow) {
      org.joml.Matrix4f transform = new org.joml.Matrix4f(textPose.last().pose());
      NativeGuiClip.Snapshot clip = NativeGuiClip.snapshot();
      for (FormattedCharSequence line : lines) {
         font.prepareText(line, x, y, color, shadow, false, 0).visit(new Font.GlyphVisitor() {
            @Override public void acceptRenderable(net.minecraft.client.gui.font.TextRenderable glyph) {
               RenderType type = glyph.renderType(DisplayMode.NORMAL);
               NativeGuiClip.ordered(collector()).submitCustomGeometry(poseStack, clip.shapes().isEmpty() ? type : NativeGuiRenderTypes.independent(type), (pose, output) -> {
                  var clipped = clip.wrap(output, type.primitiveTopology());
                  glyph.render(transform, clipped == null ? output : clipped, 15728880, false);
                  if (clipped != null) clipped.finish();
               });
            }
         });
         y += font.lineHeight;
      }
   }

   private static void emitVertex(ByteBuffer data, int base, List<VertexFormatElement> elements, VertexConsumer output) {
      VertexFormatElement position = null;
      VertexFormatElement color = null;
      VertexFormatElement uv0 = null;
      VertexFormatElement uv1 = null;
      VertexFormatElement uv2 = null;
      VertexFormatElement normal = null;
      VertexFormatElement lineWidth = null;

      for (VertexFormatElement element : elements) {
         switch (element.name().toLowerCase()) {
            case "position":
               position = element;
               break;
            case "color":
               color = element;
               break;
            case "uv0":
               uv0 = element;
               break;
            case "uv1":
               uv1 = element;
               break;
            case "uv2":
               uv2 = element;
               break;
            case "normal":
               normal = element;
               break;
            case "linewidth":
               lineWidth = element;
         }
      }

      if (position != null) {
         double[] p = values(data, base, position);
         output.addVertex((float)p[0], (float)p[1], (float)p[2]);
         if (color != null) {
            double[] c = values(data, base, color);
            output.setColor((int)(c[0] * 255.0), (int)(c[1] * 255.0), (int)(c[2] * 255.0), (int)(c[3] * 255.0));
         }

         if (uv0 != null) {
            double[] uv = values(data, base, uv0);
            output.setUv((float)uv[0], (float)uv[1]);
         }

         if (uv1 != null) {
            double[] uv = values(data, base, uv1);
            output.setUv1((int)uv[0], (int)uv[1]);
         }

         if (uv2 != null) {
            double[] uv = values(data, base, uv2);
            output.setUv2((int)uv[0], (int)uv[1]);
         }

         if (normal != null) {
            double[] n = values(data, base, normal);
            output.setNormal((float)n[0], (float)n[1], (float)n[2]);
         }

         if (lineWidth != null) {
            output.setLineWidth((float)values(data, base, lineWidth)[0]);
         }
      }
   }

   public static void replay(MeshData mesh, VertexConsumer output) {
      DrawState state = mesh.drawState();
      ByteBuffer source = mesh.vertexBuffer().duplicate().order(ByteOrder.nativeOrder());
      int stride = state.format().getVertexSize();

      for (int vertex = 0; vertex < state.vertexCount(); vertex++) {
         emitVertex(source, vertex * stride, state.format().getElements(), output);
      }
   }

   private static double[] values(ByteBuffer data, int base, VertexFormatElement element) {
      GpuFormat format = element.format();
      double[] values = new double[format.componentCount()];
      int offset = base + element.offset();
      ComponentType componentType = format.componentType();
      int componentBytes = componentType.byteSize();

      for (int i = 0; i < values.length; i++) {
         int index = offset + i * componentBytes;

         values[i] = switch (componentType) {
            case UNORM_8 -> Byte.toUnsignedInt(data.get(index)) / 255.0;
            case SNORM_8 -> Math.max(-1.0, data.get(index) / 127.0);
            case UINT_8 -> Byte.toUnsignedInt(data.get(index));
            case SINT_8 -> data.get(index);
            case UNORM_16 -> Short.toUnsignedInt(data.getShort(index)) / 65535.0;
            case SNORM_16 -> Math.max(-1.0, data.getShort(index) / 32767.0);
            case UINT_16 -> Short.toUnsignedInt(data.getShort(index));
            case SINT_16 -> data.getShort(index);
            case FLOAT_16 -> halfToFloat(data.getShort(index));
            case UINT_32 -> Integer.toUnsignedLong(data.getInt(index));
            case SINT_32 -> data.getInt(index);
            case FLOAT_32 -> data.getFloat(index);
            default -> throw new IllegalArgumentException("Unsupported vertex component format: " + componentType);
         };
      }

      return values;
   }

   private static float halfToFloat(short value) {
      int half = Short.toUnsignedInt(value);
      int sign = (half & 32768) << 16;
      int exponent = half >>> 10 & 31;
      int mantissa = half & 1023;
      int bits;
      if (exponent == 0) {
         if (mantissa == 0) {
            bits = sign;
         } else {
            for (exponent = 1; (mantissa & 1024) == 0; exponent--) {
               mantissa <<= 1;
            }

            bits = sign | exponent + 112 << 23 | (mantissa & 1023) << 13;
         }
      } else if (exponent == 31) {
         bits = sign | 2139095040 | mantissa << 13;
      } else {
         bits = sign | exponent + 112 << 23 | mantissa << 13;
      }

      return Float.intBitsToFloat(bits);
   }
}
