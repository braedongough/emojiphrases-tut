package com.raywenderlich.webapp

import com.raywenderlich.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "/phrases"

fun Route.phrases(db: Repository) {
    get(PHRASES) {
        val phrases = db.phrases()

        val mapPhrases = mapOf("phrases" to phrases)
        print("mapPhrases $mapPhrases")
        call.respond(FreeMarkerContent("phrases.ftl", mapPhrases))
    }
}