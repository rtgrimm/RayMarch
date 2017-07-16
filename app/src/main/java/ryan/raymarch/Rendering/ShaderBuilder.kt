package ryan.raymarch.Rendering

import ryan.raymarch.*


object ShaderBuilder {

    private fun BuildIndexedFunction(name: String, returnType: String,
                                     params: String,
                                     defaultReturn: String,
                                     functions: Iterable<NamedShader>,
                                     call: (NamedShader) -> String) = """
    $returnType $name(int id, $params) {
        if(false) {}

        ${functions.map {
        """
        else if(id == ${it.Id}) {
            ${if (returnType == "void") "" else "return"}
            ${call(it)};
        }"""
    }.Concat()}

     return $defaultReturn;
    }
"""

    private fun BuildDistanceFieldFunction(distanceFunctions: Iterable<DistanceFunction>) = """
        ${distanceFunctions.map { it.Impl.Source + "\n" }.Concat()}
        ${BuildIndexedFunction("distanceField",
            "float", "vec3 ray", "0.0",
            distanceFunctions, {
        "${it.Impl.Name}(ray)"
    }
    )}
"""

    fun Build(template: String, generator: Generator): String {
        return MapReplace(template, "//{", "}", mapOf(
                Pair("DistanceField", BuildDistanceFieldFunction(generator.DistanceFunction))
        ))
    }
}