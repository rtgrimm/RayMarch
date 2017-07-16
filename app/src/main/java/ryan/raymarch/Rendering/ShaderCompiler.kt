package ryan.raymarch.Rendering

import android.opengl.GLES20
import android.opengl.GLES30.*
import android.util.Log

object ShaderCompiler {
    private fun CompileShader(src: String, type: Int): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, src)
        glCompileShader(shader)

        val log = glGetShaderInfoLog(shader)
        if (log.count() != 0) {
            Log.d("gl", log)
            throw RuntimeException(log)
        }


        return shader
    }

    fun CompileProgram(sources: Iterable<Pair<Int, String>>): Int {
        val shaders = sources.map { CompileShader(it.second, it.first) }
        val program = glCreateProgram()



        shaders.forEach { glAttachShader(program, it) }
        glLinkProgram(program)

        val log = glGetProgramInfoLog(program)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (log.count() != 0 && linkStatus[0] != GLES20.GL_TRUE) {
            Log.d("gl", log)
            throw RuntimeException(log)
        }

        shaders.forEach({ glDeleteShader(it) })

        return program
    }
}