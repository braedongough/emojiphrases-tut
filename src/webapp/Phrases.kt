package com.raywenderlich.webapp

import com.raywenderlich.*
import com.raywenderlich.model.*
import com.raywenderlich.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "/phrases"
@Location(PHRASES)
class Phrases

fun Route.phrases(db: Repository) {
    authenticate("auth") {
        get<Phrases> {
            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl",
                    mapOf("phrases" to phrases, "displayName" to user.displayName)
                )
            )
        }

        post<Phrases> {
            val params = call.receiveParameters()
            val action = params["action"] ?: throw IllegalArgumentException("Missing param: Action")
            when (action) {
                "delete" -> {
                    val id = params["id"] ?: throw java.lang.IllegalArgumentException("Missing param: Id")
                    db.remove(id)
                }
                "add" -> {
                    val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing param: Emoji")
                    val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing param: Phrase")
                    db.add(EmojiPhrase(emoji, phrase))
                }
            }
             call.redirect(Phrases())
        }
    }

}