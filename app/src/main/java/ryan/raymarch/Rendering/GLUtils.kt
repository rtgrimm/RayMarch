package ryan.raymarch.Rendering

import android.opengl.GLES20

fun GLCheckError() {
    val error = GLES20.glGetError()

    if (error != 0) {
        throw RuntimeException("OpenGL Error : $error")
    }
}

