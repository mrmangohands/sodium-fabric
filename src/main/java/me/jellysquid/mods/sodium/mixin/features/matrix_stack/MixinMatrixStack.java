package me.jellysquid.mods.sodium.mixin.features.matrix_stack;

import me.jellysquid.mods.sodium.client.util.math.Matrix4fExtended;
import me.jellysquid.mods.sodium.client.util.math.MatrixUtil;
import net.minecraft.class_4587;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_4587.class)
public abstract class MixinMatrixStack {
    @Shadow
    public abstract Matrix4f method_22910();

    /**
     * @reason Use our faster specialized function
     * @author JellySquid
     */
    @Overwrite
    public void method_22904(double x, double y, double z) {
        Matrix4fExtended mat = MatrixUtil.getExtendedMatrix(this.method_22910());
        mat.translate((float) x, (float) y, (float) z);
    }

    /**
     * @reason Use our faster specialized function
     * @author JellySquid
     */
    @Overwrite
    public void method_22907(Quaternion q) {
        Matrix4fExtended mat4 = MatrixUtil.getExtendedMatrix(this.method_22910());
        mat4.rotate(q);
    }
}
