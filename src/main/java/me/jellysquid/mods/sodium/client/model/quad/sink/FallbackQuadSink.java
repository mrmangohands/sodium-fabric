package me.jellysquid.mods.sodium.client.model.quad.sink;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.color.ColorU8;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.*;

/**
 * A fallback implementation of {@link ModelQuadSink} for when we're writing into an arbitrary {@link BufferBuilder}.
 * This implementation is considerably slower than other sinks as it must perform many matrix transformations for every
 * vertex and unpack values as assumptions can't be made about what the backing buffer type is.
 */
public class FallbackQuadSink implements ModelQuadSink, ModelQuadSinkDelegate {
    private final class_4588 consumer;

    // Hoisted matrices to avoid lookups in peeking
    private final Matrix4f modelMatrix;

    // Cached vectors to avoid allocations
    private final Vector4f vector;
    private final Vector3f normal;

    public FallbackQuadSink(class_4588 consumer, class_4587 matrixStack) {
        this.consumer = consumer;
        this.modelMatrix = matrixStack.method_22910();
        this.vector = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
        this.normal = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void write(ModelQuadViewMutable quad) {
        Vector4f posVec = this.vector;
        Vector3f normVec = this.normal;

        for (int i = 0; i < 4; i++) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);

            // FIXME
            //posVec.method_23851(x, y, z, 1.0F);
            posVec = new Vector4f(x, y, z, 1.0F);
            posVec.method_22674(this.modelMatrix);

            int color = quad.getColor(i);

            float r = ColorU8.normalize(ColorABGR.unpackRed(color));
            float g = ColorU8.normalize(ColorABGR.unpackGreen(color));
            float b = ColorU8.normalize(ColorABGR.unpackBlue(color));
            float a = ColorU8.normalize(ColorABGR.unpackAlpha(color));

            float u = quad.getTexU(i);
            float v = quad.getTexV(i);

            int light = quad.getLight(i);
            int norm = quad.getNormal(i);

            float normX = Norm3b.unpackX(norm);
            float normY = Norm3b.unpackY(norm);
            float normZ = Norm3b.unpackZ(norm);

            normVec.set(normX, normY, normZ);

            this.consumer.vertex((double)posVec.getX(), (double)posVec.getY(), (double)posVec.getZ()).method_22915(r, g, b, a).texture(u, v).method_22916(light).method_22914(normVec.getX(), normVec.getY(), normVec.getZ()).next();
        }
    }

    @Override
    public ModelQuadSink get(ModelQuadFacing facing) {
        return this;
    }
}
