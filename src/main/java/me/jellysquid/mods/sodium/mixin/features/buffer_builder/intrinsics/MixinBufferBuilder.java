package me.jellysquid.mods.sodium.mixin.features.buffer_builder.intrinsics;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.vertex.DefaultVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.color.ColorU8;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
import net.minecraft.client.render.AbstractVertexConsumer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({ "SameParameterValue" })
@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder extends AbstractVertexConsumer {
    private boolean field_21594; // is baked quad format

    @Inject(method = "begin", at = @At("RETURN"))
    public void begin(int drawMode, VertexFormat vertexFormat, CallbackInfo ci) {
        boolean bl = vertexFormat == VertexFormats.POSITION_UV_NORMAL_2;
        boolean bl2 = vertexFormat == VertexFormats.POSITION_COLOR_UV_NORMAL;
        this.field_21594 = bl || bl2;
    }

    @Override
    public void quad(Matrix4f matrix4f, BakedQuad quad, float[] brightnessTable, float r, float g, float b, int[] light, boolean colorize) {
        if (!this.field_21594) {
            super.quad(matrix4f, quad, brightnessTable, r, g, b, light, colorize);

            return;
        }

        if (this.field_20889) {
            throw new IllegalStateException();
        }

        ModelQuadView quadView = (ModelQuadView) quad;

        Matrix3f matrix3f = new Matrix3f(matrix4f);
        matrix3f.transpose();
        float f = matrix3f.determinantAndAdjugate();
        if (!(f < 1.0E-5F)) {
           float f2 = matrix3f.determinant();
           matrix3f.multiply(MathHelper.fastInverseCbrt(f2));
        }

        int norm = MatrixUtil.computeNormal(matrix3f, quad.getFace());

        QuadVertexSink drain = VertexDrain.of(this)
                .createSink(DefaultVertexTypes.QUADS);
        drain.ensureCapacity(4);

        for (int i = 0; i < 4; i++) {
            float x = quadView.getX(i);
            float y = quadView.getY(i);
            float z = quadView.getZ(i);

            float fR;
            float fG;
            float fB;

            float brightness = brightnessTable[i];

            if (colorize) {
                int color = quadView.getColor(i);

                float oR = ColorU8.normalize(ColorABGR.unpackRed(color));
                float oG = ColorU8.normalize(ColorABGR.unpackGreen(color));
                float oB = ColorU8.normalize(ColorABGR.unpackBlue(color));

                fR = oR * brightness * r;
                fG = oG * brightness * g;
                fB = oB * brightness * b;
            } else {
                fR = brightness * r;
                fG = brightness * g;
                fB = brightness * b;
            }

            float u = quadView.getTexU(i);
            float v = quadView.getTexV(i);

            int color = ColorABGR.pack(fR, fG, fB, 1.0F);

            Vector4f pos = new Vector4f(x, y, z, 1.0F);
            pos.multiply(matrix4f);

            drain.writeQuad(pos.getX(), pos.getY(), pos.getZ(), color, u, v, light[i], norm);
        }

        drain.flush();
    }
}
