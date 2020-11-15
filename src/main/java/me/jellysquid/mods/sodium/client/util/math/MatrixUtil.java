package me.jellysquid.mods.sodium.client.util.math;

import me.jellysquid.mods.sodium.client.util.Norm3b;
import net.minecraft.class_4581;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.math.Direction;

@SuppressWarnings("ConstantConditions")
public class MatrixUtil {
    public static int computeNormal(class_4581 normalMatrix, Direction facing) {
        return ((Matrix3fExtended) (Object) normalMatrix).computeNormal(facing);
    }

    public static Matrix4fExtended getExtendedMatrix(Matrix4f matrix) {
        return (Matrix4fExtended) (Object) matrix;
    }

    public static Matrix3fExtended getExtendedMatrix(class_4581 matrix) {
        return (Matrix3fExtended) (Object) matrix;
    }

    public static int transformPackedNormal(int norm, class_4581 matrix) {
        Matrix3fExtended mat = MatrixUtil.getExtendedMatrix(matrix);

        float normX1 = Norm3b.unpackX(norm);
        float normY1 = Norm3b.unpackY(norm);
        float normZ1 = Norm3b.unpackZ(norm);

        float normX2 = mat.transformVecX(normX1, normY1, normZ1);
        float normY2 = mat.transformVecY(normX1, normY1, normZ1);
        float normZ2 = mat.transformVecZ(normX1, normY1, normZ1);

        return Norm3b.pack(normX2, normY2, normZ2);
    }
}
