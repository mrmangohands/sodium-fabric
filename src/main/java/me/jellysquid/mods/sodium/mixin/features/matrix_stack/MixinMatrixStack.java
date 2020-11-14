package me.jellysquid.mods.sodium.mixin.features.matrix_stack;

import me.jellysquid.mods.sodium.client.util.math.Matrix3fExtended;
import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatrixStack.class)
public abstract class MixinMatrixStack {
    @Shadow
    public abstract Matrix4f peek();

    @Shadow
    public abstract Matrix3f method_23478();


    /**
     * @reason Use our faster specialized function
     * @author JellySquid
     */
    @Overwrite
    public void translate(double x, double y, double z) {
        Matrix4fExtended mat = MatrixUtil.getExtendedMatrix(this.peek());
        mat.translate((float) x, (float) y, (float) z);
    }

    /**
     * @reason Use our faster specialized function
     * @author JellySquid
     */
    @Overwrite
    public void multiply(Quaternion q) {
        Matrix4fExtended mat4 = MatrixUtil.getExtendedMatrix(this.peek());
        mat4.rotate(q);

        Matrix3fExtended mat3 = MatrixUtil.getExtendedMatrix(this.method_23478());
        mat3.rotate(q);
    }
}
