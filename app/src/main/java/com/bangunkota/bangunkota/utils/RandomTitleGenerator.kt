package com.bangunkota.bangunkota.utils

import java.util.*

object RandomTitleGenerator {
    private val adjectives = listOf("Amazing", "Incredible", "Fantastic", "Wonderful", "Awesome")
    private val nouns = listOf("Adventure", "Journey", "Experience", "Discovery", "Moment")

    private val random = Random()

    fun generateRandomTitle(): String {
        val adjective = adjectives[random.nextInt(adjectives.size)]
        val noun = nouns[random.nextInt(nouns.size)]

        return "$adjective $noun"
    }
}