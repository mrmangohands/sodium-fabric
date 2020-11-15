package me.jellysquid.mods.sodium.mixin.core.pipeline;

import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.VertexSink;
import me.jellysquid.mods.sodium.client.model.vertex.VertexType;
import me.jellysquid.mods.sodium.client.model.vertex.VertexTypeBlittable;
import me.jellysquid.mods.sodium.client.model.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.client.util.UnsafeUtil;
import net.minecraft.class_4588;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.GlAllocationUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder implements VertexBufferView, VertexDrain {
    @Shadow
    private int field_20884;

    @Shadow
    private ByteBuffer bufByte;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    private static int roundBufferSize(int amount) {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private VertexFormat format;

    @Shadow
    private int vertexCount;

    @Override
    public boolean ensureBufferCapacity(int bytes) {
        if (this.field_20884 + bytes <= this.bufByte.capacity()) {
            return false;
        }

        int newSize = this.bufByte.capacity() + roundBufferSize(bytes);

        LOGGER.debug("Needed to grow BufferBuilder bufByte: Old size {} bytes, new size {} bytes.", this.bufByte.capacity(), newSize);

        this.bufByte.position(0);

        ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(newSize);
        byteBuffer.put(this.bufByte);
        byteBuffer.rewind();

        this.bufByte = byteBuffer;

        return true;
    }

    @Override
    public ByteBuffer getDirectBuffer() {
        return this.bufByte;
    }

    @Override
    public int getWriterPosition() {
        return this.field_20884;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return this.format;
    }

    @Override
    public void flush(int vertexCount, VertexFormat format) {
        if (this.format != format) {
            throw new IllegalStateException("Mis-matched vertex format (expected: [" + format + "], currently using: [" + this.format + "])");
        }

        this.vertexCount += vertexCount;
        this.field_20884 += vertexCount * format.getVertexSize();
    }

    @Override
    public <T extends VertexSink> T createSink(VertexType<T> factory) {
        VertexTypeBlittable<T> blittable = factory.asBlittable();

        if (blittable != null && blittable.getBufferVertexFormat() == this.format)  {
            return blittable.createBufferWriter(this, UnsafeUtil.isAvailable());
        }

        return factory.createFallbackWriter((class_4588) this);
    }
}
