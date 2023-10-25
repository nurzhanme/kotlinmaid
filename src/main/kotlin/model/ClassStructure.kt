package model

/**
 *
 * @author Nurzhan Aitbayev github.com/nurzhanme
 */
data class ClassStructure(
    val fullname: String,
    val name: String,
    val parentName: String?,
    val methods: List<String>,
    val properties: List<Pair<String, String>>)