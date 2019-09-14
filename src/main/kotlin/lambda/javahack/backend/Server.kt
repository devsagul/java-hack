package lambda.javahack.backend

import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.toMap
import io.ktor.jackson.jackson
import java.nio.ByteBuffer
import io.ktor.request.receiveText
import io.ktor.response.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            jackson {
                // Configure Jackson's ObjectMapper here
            }
        }
        routing {
            get("/") {
                call.respondText("OK")
            }
        }
    }
    server.start(wait = true)
}