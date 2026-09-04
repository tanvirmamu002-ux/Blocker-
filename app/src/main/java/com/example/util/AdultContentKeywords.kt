package com.example.util

import java.util.regex.Pattern

object AdultContentKeywords {

    val blockedKeywords = listOf(
        "blowjob",
        "handjob",
        "deepthroat",
        "creampie",
        "cumshot",
        "gangbang",
        "porn",
        "pornography",
        "xxx",
        "hentai",
        "nude",
        "nudity",
        "naked",
        "sexcam",
        "camgirl",
        "camsex",
        "pornhub",
        "xvideos",
        "xnxx",
        "onlyfans",
        "chaturbate",
        "stripchat",
        "brazzers",
        "redtube",
        "xhamster",
        "youporn",
        "rule34",
        "nhentai",
        "jizz",
        "bukkake",
        "gokkun",
        "fisting",
        "pegging",
        "facesitting",
        "squirting",
        "threesome",
        "orgy",
        "cuckold",
        "femdom",
        "maledom",
        "spitroast",
        "teabagging",
        "snowballing",
        "paizuri",
        "ahegao",
        "tentacles",
        "spankbang",
        "eporner",
        "hqporner",
        "tube8",
        "camsoda",
        "bongacams",
        "livejasmin",
        "myfreecams",
        "xhamsterlive",
        "gelbooru",
        "danbooru",
        "e621"
    )

    // Precompiled case-insensitive exact word/phrase boundary patterns
    // e.g. "\bporn\b", "\bxxx\b" to avoid matching harmless words like "annude", "pornos", or "expand"
    private val compiledPatterns: List<Pair<String, Pattern>> = blockedKeywords.map { keyword ->
        val trimmed = keyword.trim().lowercase()
        // If keyword contains digits or special chars, \b works well with alphanumeric boundaries
        val regexString = "\\b" + Pattern.quote(trimmed) + "\\b"
        trimmed to Pattern.compile(regexString, Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE)
    }

    /**
     * Checks if the given text contains any high-risk adult/NSFW keyword.
     * Returns true if a match is found using exact word/phrase boundary matching.
     */
    fun containsAdultKeyword(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val lowerText = text.lowercase()

        for ((rawKeyword, pattern) in compiledPatterns) {
            // Quick fast-path rejection before running regex matcher
            if (lowerText.contains(rawKeyword)) {
                if (pattern.matcher(text).find()) {
                    return true
                }
            }
        }
        return false
    }
}
