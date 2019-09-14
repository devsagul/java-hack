package lambda.javahack.backend

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
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
import io.ktor.routing.route
import io.ktor.sessions.*

data class LoginSession(val user: String, var token: String = "NO_TOKEN")

val db = DBHelper()

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            jackson {}
        }
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        install(Sessions) {
            cookie<LoginSession>("LOGIN_SESSION", SessionStorageMemory())
        }
        install(Authentication) {
            form(name = "form") {
                userParamName = "user"
                passwordParamName = "password"
                challenge {
                    FormAuthChallenge.Redirect{"/login"}
                }
                validate { if (db.validateCredentials(it.name,it.password)) UserIdPrincipal(it.name) else null }
            }
        }
        routing {
            static("/") {
                resources("static")
            }
            get("/") {
                call.respond(FreeMarkerContent("index.ftl",null,""))
            }
            get("/auth") {
                call.respond(FreeMarkerContent("auth.ftl",null,""))
            }
            get("/reg") {
                call.respond(FreeMarkerContent("reg.ftl",null,""))
            }
            get("/test") {
                val json = mapOf<String,Any>("question" to "Ты пишешь на котлине?", "id" to 0, "childs"
                to listOf<Any>(mapOf("id" to 1, "answer" to "да"), mapOf("id" to 2, "answer" to "нет")))
                call.respond(json)
            }
            get("/login") {
                val html = """
            <form action="/login" method="post" enctype="application/x-www-form-urlencoded">
            <div>User</div>
            <div><input type="text" name="user" /></div>
            <div>Password:</div>
            <div><input type="password" name="password" /></div>
            <div><input type="submit" value="Add" /></div>
        </form>
            """
                call.respondText(html, ContentType.Text.Html)
            }
            authenticate("form") {
                post("/login") {
                    val principal = call.authentication.principal<UserIdPrincipal>()
                    call.sessions.set(LoginSession(principal!!.name))
                    call.respondRedirect("/")
                }
                get("/protected") {
                    val session = call.sessions.get<LoginSession>()
                    call.respondText("Success, ${session?.user}. Your token is: ${session?.token}")
                }
            }
        }
    }
    server.start(wait = true)
}