package ryan.raymarch

import org.joml.Matrix4f

data class Element(
        val Transform: Matrix4f,
        val DistanceFunctionID: Int,
        val MaterialID: Int)