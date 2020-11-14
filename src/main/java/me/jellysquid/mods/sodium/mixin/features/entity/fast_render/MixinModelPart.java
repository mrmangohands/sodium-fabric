package me.jellysquid.mods.sodium.mixin.features.entity.fast_render;

import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import me.jellysquid.mods.sodium.client.model.vertex.DefaultVertexTypes;
import me.jellysquid.mods.sodium.client.model.vertex.VertexDrain;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.client.util.math.Matrix3fExtended;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ModelPart.class)
public class MixinModelPart {
    @Shadow
    @Final
    private List<ModelPart.Cuboid> cuboids;

    /**
     * @author JellySquid
     * @reason Use optimized vertex writer, avoid allocations, use quick matrix transformations
     */
    @Overwrite
    private void renderCuboids(Matrix4f matrix4f, VertexConsumer vertexConsumer, float pivotDistance, int light, int overlay, @Nullable Sprite sprite, float red, float green, float blue) {
        //Matrix3fExtended normalExt = MatrixUtil.getExtendedMatrix(matrices.method_23762());
        Matrix3f matrix3f = new Matrix3f(matrix4f);
        Matrix4fExtended modelExt = MatrixUtil.getExtendedMatrix(matrix4f);

        QuadVertexSink drain = VertexDrain.of(vertexConsumer).createSink(DefaultVertexTypes.QUADS);
        drain.ensureCapacity(this.cuboids.size() * 6 * 4);

        // FIXME
        // Investigate 1.0F, 1.15-pre1 -> 19w46b
        int color = ColorABGR.pack(red, green, blue, 1.0F);

        for (ModelPart.Cuboid cuboid : this.cuboids) {
            for (ModelPart.Quad quad : ((ModelCuboidAccessor) cuboid).getQuads()) {
                //FIXME
                //19w46a -> 19w45b
                Vector3f vector3f = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[0].pos));
                Vector3f vector3f2 = new Vector3f(quad.vertices[1].pos.reverseSubtract(quad.vertices[2].pos));
                vector3f.multiply(matrix3f);
                vector3f2.multiply(matrix3f);
                vector3f2.cross(vector3f);
                vector3f2.reciprocal();
                float normX = vector3f2.getX();
                float normY = vector3f2.getY();
                float normZ = vector3f2.getZ();

                //float normX = normalExt.transformVecX(quad.field_21618);
                //float normY = normalExt.transformVecY(quad.field_21618);
                //float normZ = normalExt.transformVecZ(quad.field_21618);

                int norm = Norm3b.pack(normX, normY, normZ);

                for (ModelPart.Vertex vertex : quad.vertices) {
                    Vec3d pos = vertex.pos;

                    float x1 = (float)pos.getX() * pivotDistance;
                    float y1 = (float)pos.getY() * pivotDistance;
                    float z1 = (float)pos.getZ() * pivotDistance;

                    float x2 = modelExt.transformVecX(x1, y1, z1);
                    float y2 = modelExt.transformVecY(x1, y1, z1);
                    float z2 = modelExt.transformVecZ(x1, y1, z1);

                    float u;
                    float v;
                    if (sprite == null) {
                       u = vertex.u;
                       v = vertex.v;
                    } else {
                       u = sprite.getU((double)(vertex.u * 16.0F));
                       v = sprite.getV((double)(vertex.v * 16.0F));
                    }

                    drain.writeQuad(x2, y2, z2, color, u, v, light, overlay, norm);
                }
            }
        }

        drain.flush();
    }
}
