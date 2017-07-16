package ryan.raymarch.Rendering

import android.opengl.GLES20
import android.opengl.GLES30.*
import android.opengl.GLSurfaceView
import ryan.raymarch.AssetLoader
import ryan.raymarch.Element
import ryan.raymarch.Generator
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(private val loader: AssetLoader,
               private val generator: Generator,
               private val elements: Collection<Element>) : GLSurfaceView.Renderer {


    class ElementWriter : UniformList.Writer<Element> {
        override fun Write(items: Iterable<Element>, buffer: ByteBuffer) {

            val floatBuffer = buffer.asFloatBuffer()

            items.forEach {
                floatBuffer.put(it.DistanceFunctionID.toFloat())
                        .put(it.MaterialID.toFloat())
                        .put(0f).put(0f)

                it.Transform.invert().get(floatBuffer)
                floatBuffer.position(floatBuffer.position() + 16)
            }
        }

    }

    private var _Program = -1

    private var _Width = 0
    private var _Height = 0

    private var _Time = 0.0f

    private var _MouseX = 0.0f
    private var _MouseY = 0.0f

    private lateinit var _QuadRenderer: QuadRenderer
    private lateinit var _UniformList: UniformList<Element>

    private val UniformLocations = object {
        var Time = 0
        var Resolution = 0
        var Mouse = 0
        var ElementCount = 0
    }

    private fun InitUniformLocations() {
        UniformLocations.Mouse = GLES20.glGetUniformLocation(_Program, "Mouse")
        UniformLocations.Time = GLES20.glGetUniformLocation(_Program, "Time")
        UniformLocations.Resolution = GLES20.glGetUniformLocation(_Program, "Resolution")
        UniformLocations.ElementCount = GLES20.glGetUniformLocation(_Program, "ElementCount")
    }

    private fun InitGL() {
        _QuadRenderer = QuadRenderer()
        _UniformList = UniformList<Element>(ElementWriter(), elements.size, 80)

        _UniformList.WriteItems(0, elements)


        CompileShaders()

        val blockIndex = glGetUniformBlockIndex(_Program, "ElementData")
        glUniformBlockBinding(_Program, blockIndex, 1)


        InitUniformLocations()
    }

    private fun CompileShaders() {
        val fragShader = ShaderBuilder.Build(
                loader.Load("frag.glsl"), generator)

        val vertShader = loader.Load("vert.glsl")

        _Program = ShaderCompiler.CompileProgram(listOf(
                Pair(GL_FRAGMENT_SHADER, fragShader),
                Pair(GL_VERTEX_SHADER, vertShader)
        ))
    }

    override fun onDrawFrame(`_`: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        _UniformList.BindToBindingPoint(1)


        GLES20.glUseProgram(_Program)

        GLES20.glUniform1f(UniformLocations.Time, _Time)
        GLES20.glUniform2f(UniformLocations.Resolution, _Width.toFloat(), _Height.toFloat())
        GLES20.glUniform2f(UniformLocations.Mouse, _MouseX, _MouseY)
        GLES20.glUniform1i(UniformLocations.ElementCount, 2)


        GLCheckError()
        _QuadRenderer.Render()

        _Time += 0.01f;
    }


    override fun onSurfaceChanged(`_`: GL10?, width: Int, height: Int) {
        _Width = width
        _Height = height

        glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(`_`: GL10?, config: EGLConfig?) {
        glClearColor(1F, 0F, 0F, 1F)
        InitGL()
    }


    fun SetMouse(x: Float, y: Float) {
        _MouseX = x
        _MouseY = y
    }
}