package ryan.raymarch

data class ShaderImpl(
        val Name: String,
        val Source: String
)

interface NamedShader {
    val Id: Int
    val Impl: ShaderImpl
}

interface Material : NamedShader
interface DistanceFunction : NamedShader

interface Generator {
    val Materials: Iterable<Material>
    val DistanceFunction: Iterable<DistanceFunction>
}