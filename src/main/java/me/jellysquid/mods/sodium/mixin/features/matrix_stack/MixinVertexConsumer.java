package me.jellysquid.mods.sodium.mixin.features.matrix_stack;

import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VertexConsumer.class)
public interface MixinVertexConsumer {
    @Shadow
    VertexConsumer vertex(double x, double y, double z);

    /**
     * @reason Avoid allocations
     * @author JellySquid
     */
    @Overwrite
    default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Matrix4fExtended ext = MatrixUtil.getExtendedMatrix(matrix);
        float x2 = ext.transformVecX(x, y, z);
        float y2 = ext.transformVecY(x, y, z);
        float z2 = ext.transformVecZ(x, y, z);

        return this.vertex(x2, y2, z2);
    }
}
