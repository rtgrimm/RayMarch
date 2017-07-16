package ryan.raymarch.Rendering

import android.opengl.GLES20
import android.opengl.GLES30.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


class UniformList<T>(private val writer: Writer<T>, val ItemCount: Int, val ItemSize: Int) {
    interface Writer<T> {
        fun Write(items: Iterable<T>, buffer: ByteBuffer)
    }

    private val _UniformBuffer: Int

    init {
        val proxyArray = IntArray(1)

        glGenBuffers(1, proxyArray, 0)
        _UniformBuffer = proxyArray[0]

        glBindBuffer(GL_UNIFORM_BUFFER, _UniformBuffer)
        glBufferData(GL_UNIFORM_BUFFER, ItemCount * ItemSize, null, GL_STREAM_DRAW)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    fun BindToBindingPoint(point: Int) {
        glBindBufferBase(GL_UNIFORM_BUFFER, point, _UniformBuffer)
    }

    fun WriteItems(itemOffset: Int, items: Collection<T>) {
        val buffer = ByteBuffer.allocateDirect(ItemCount * ItemSize).order(ByteOrder.nativeOrder())
        writer.Write(items, buffer)

        glBindBuffer(GL_UNIFORM_BUFFER, _UniformBuffer)
        GLES20.glBufferSubData(GL_UNIFORM_BUFFER,
                itemOffset * ItemSize,
                ItemCount * ItemSize, buffer)

        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }
}