package ryan.raymarch

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import org.joml.Matrix4f


class TestGenerator : Generator {
    override val DistanceFunction: Iterable<DistanceFunction>
        get() = listOf(
                object : DistanceFunction {
                    override val Id: Int
                        get() = 0
                    override val Impl: ShaderImpl
                        get() = ShaderImpl("Sphere", """
                        float Sphere(vec3 ray) {
                            return length(ray) - 0.3;
                        }
                        """)

                },

                object : DistanceFunction {
                    override val Id: Int
                        get() = 1
                    override val Impl: ShaderImpl
                        get() = ShaderImpl("Box", """
                        float Box(vec3 ray) {

                              return length(max(abs(ray)-vec3(0.05, 0.05, 0.5),0.0));
                        }
                        """)

                }
        )
    override val Materials: Iterable<Material>
        get() = listOf<Material>()

}


class RayMarchSurface(private val ctx: Context?, Elements: Collection<Element>) : GLSurfaceView(ctx) {


    private val Renderer = ryan.raymarch.Rendering.Renderer(
            ContextAssetLoader(context), TestGenerator(), Elements)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event!!

        if (event.action == MotionEvent.ACTION_MOVE) {
            Renderer.SetMouse(event.x, event.y)
        }

        return true
    }

    init {
        setEGLContextClientVersion(3)
        setRenderer(this.Renderer)
    }
}

class MainActivity : AppCompatActivity() {
    private val Elements = listOf(
            Element(Matrix4f().translate(0f, 0.3f, 0.3f), 0, 0),
            Element(Matrix4f().identity(), 1, 0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surface = RayMarchSurface(this, Elements)
        setContentView(surface)
    }
}
