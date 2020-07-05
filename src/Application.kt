package com.raywenderlich

import com.raywenderlich.api.*
import com.raywenderlich.model.*
import com.raywenderlich.repository.*
import com.raywenderlich.webapp.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Authentication) {
        basic(name = "auth") {
            realm = "ktor server"
            validate { credentials ->
                if (credentials.password == "${credentials.name}123") User(credentials.name) else null
            }
        }
    }

    install(Locations)

    val db = InMemoryRepository()

    routing {
        static("/static") {
            resources("images")
        }
        home()
        about()
        phrases(db)

        //API
        phrase(db)

    }
}

const val API_VERSION = "/api/v1"

// todo: Lesson 19

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}