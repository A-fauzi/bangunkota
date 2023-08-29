package com.bangunkota.bangunkota.utils

import java.util.*

object RandomImageGenerator {
    private val random = Random()

    fun generateRandomImageUrl(width: Int, height: Int): String {
        val imageId = random.nextInt(1000) // Menghasilkan angka acak dari 0 hingga 999
        return "https://picsum.photos/$width/$height?image=$imageId"
    }
}