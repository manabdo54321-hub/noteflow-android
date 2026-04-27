package com.noteflow.app.features.tags.domain

object TagExtractor {

    private val TAG_PATTERN = Regex("#([\\w\\u0600-\\u06FF]+)")

    fun extractTags(text: String): List<String> {
        return TAG_PATTERN.findAll(text)
            .map { it.groupValues[1].trim().lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()
    }

    fun normalize(name: String): String {
        return name.trim().lowercase()
    }
}
