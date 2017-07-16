package ryan.raymarch

import android.content.Context

interface AssetLoader {
    fun Load(name: String): String
}

class ContextAssetLoader(private val context: Context) : AssetLoader {
    override fun Load(name: String): String =
            context.assets.open(name).bufferedReader().readText()

}