package ryan.raymarch.Rendering

import android.opengl.GLES20
import android.opengl.GLES30.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class QuadRenderer {
    private var _VertexBuffer = -1
    private var _VertexArray = -1

    val vertices = arrayOf<Float>(
            -1.0f, 1.0f, 0.0f,
            0.0f, 1.0f,

            1.0f, 1.0f, 0.0f,
            1.0f, 1.0f,

            -1.0f, -1.0f, 0.0f,
            0.0f, 0.0f,

            1.0f, -1.0f, 0.0f,
            1.0f, 0.0f
    )

    init {
        GenObjects()

        val vertexBufferSize = vertices.size * 4
        val vertexData = ByteBuffer.allocateDirect(vertexBufferSize)
        vertexData.order(ByteOrder.nativeOrder())

        vertexData.asFloatBuffer().put(vertices.toFloatArray())

        vertexData.position(0)

        glBindVertexArray(_VertexArray)
        glBindBuffer(GL_ARRAY_BUFFER, _VertexBuffer)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBufferSize,
                vertexData, GLES20.GL_STATIC_DRAW)


        glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 4 * 5, 0)
        glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 4 * 5, 0)
        GLES20.glEnableVertexAttribArray(0)
        GLES20.glEnableVertexAttribArray(1)

        glBindVertexArray(0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    fun Render() {
        glBindVertexArray(_VertexArray)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        glBindVertexArray(0)
    }

    private fun GenObjects() {
        val proxyArray = IntArray(1)

        glGenBuffers(1, proxyArray, 0)
        _VertexBuffer = proxyArray[0]

        glGenVertexArrays(1, proxyArray, 0)
        _VertexArray = proxyArray[0]
    }

}