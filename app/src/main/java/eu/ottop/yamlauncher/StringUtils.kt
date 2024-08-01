package eu.ottop.yamlauncher

class StringUtils {

    fun addEndTextIfNotEmpty(value: String, addition: String): String {
        return if (value.isNotEmpty()) "$value$addition" else value
    }

    fun addStartTextIfNotEmpty(value: String, addition: String): String {
        return if (value.isNotEmpty()) "$addition$value" else value
    }

    fun cleanString(string: String?) : String? {
        return string?.replace("[^a-zA-Z0-9]".toRegex(), "")
    }

}