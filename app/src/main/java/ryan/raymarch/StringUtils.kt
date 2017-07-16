package ryan.raymarch

fun Iterable<String>.Concat(): String {
    val builder = StringBuilder()
    this.forEach({ builder.append(it) })
    return builder.toString()
}

fun MapReplace(template: String, leftWrapper: String, rightWrapper: String, map: Map<String, String>): String =
        map.toList().fold(template, { target, pair ->
            target.replace(leftWrapper + pair.first + rightWrapper, pair.second)
        })
