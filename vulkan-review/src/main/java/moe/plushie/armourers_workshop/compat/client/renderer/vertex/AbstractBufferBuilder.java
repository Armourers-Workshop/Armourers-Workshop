package moe.plushie.armourers_workshop.compat.client.renderer.vertex;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.MeshData.DrawState;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;

public class AbstractBufferBuilder extends AbstractVertexConsumer implements IBufferBuilder {
   private static final ThreadLocal<VertexConsumer> OUTPUT = new ThreadLocal<>();
   private final ByteBufferBuilder buffers;
   private BufferBuilder bufferBuilder;

   public AbstractBufferBuilder(int size) {
      super(null);
      this.buffers = new ByteBufferBuilder(size);
   }

   public static void upload(IRenderType renderType, AbstractBufferBuilder builder) {
      VertexConsumer output = OUTPUT.get();
      if (output == null) {
         throw new IllegalStateException("26.2 mesh uploads must run inside a submitted feature geometry callback");
      }

      MeshData meshData = builder.bufferBuilder.build();
      if (meshData != null) {
         MeshData var4 = meshData;

         try {
            AbstractBufferSource.replay(meshData, output);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            var4.close();
         }
      }
   }

   public static void withOutput(VertexConsumer output, Runnable task) {
      VertexConsumer previous = OUTPUT.get();
      OUTPUT.set(output);

      try {
         task.run();
      } finally {
         if (previous == null) {
            OUTPUT.remove();
         } else {
            OUTPUT.set(previous);
         }
      }
   }

   public void begin(IRenderType renderType) {
      RenderType renderType1 = (RenderType)renderType.get();
      BufferBuilder builder = new BufferBuilder(this.buffers, renderType1.primitiveTopology(), renderType1.format());
      this.parent = builder;
      this.bufferBuilder = builder;
   }

   public moe.plushie.armourers_workshop.core.client.buffer.MeshData end() {
      MeshData data = this.bufferBuilder.buildOrThrow();

      moe.plushie.armourers_workshop.core.client.buffer.MeshData var4;
      try {
         DrawState state = data.drawState();
         AbstractVertexFormat format = AbstractVertexFormat.create(state.format(), state.primitiveTopology());
         java.nio.ByteBuffer original = data.vertexBuffer();
         java.nio.ByteBuffer owned = java.nio.ByteBuffer.allocate(original.remaining()).order(java.nio.ByteOrder.nativeOrder());
         owned.put(original).flip();
         var4 = new moe.plushie.armourers_workshop.core.client.buffer.MeshData(owned, state.vertexCount(), format);
      } catch (Throwable var6) {
         if (data != null) {
            try {
               data.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (data != null) {
         data.close();
      }

      return var4;
   }

   public BufferBuilder bufferBuilder() {
      return this.bufferBuilder;
   }
}
