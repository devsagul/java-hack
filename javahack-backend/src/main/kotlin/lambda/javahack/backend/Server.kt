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
import io.ktor.util.getDigestFunction
import java.lang.IllegalArgumentException
import java.util.*

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
                userParamName = "login"
                passwordParamName = "pwd"
                challenge {
                    FormAuthChallenge.Redirect{"/auth"}
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
            post("/reg") {
               try {
                   val post = call.receiveParameters()
                   val login = post["login"]!!
                   val lastname = post["lastname"]!!
                   val middlename = post["middlename"]!!
                   val firstname = post["firstname"]!!
                   val INN = post["INN"]!!
                   val codeNalog = post["CodeNalog"]!!
                   val activity = post["activity"]!!
                   val pwd = post["pwd"]!!
                   if (db.addUser(login, pwd, lastname, middlename, firstname, INN, codeNalog, activity)) {
                       call.respondText("Succsessfully added")
                   } else {
                       call.respondText("User with this number already exists")
                   }
               } catch (e: KotlinNullPointerException) {
                   call.respondText("Error in data. Please try again.")
               }
            }
            authenticate("form") {
                post("/auth") {
                    val principal = call.authentication.principal<UserIdPrincipal>()
                    try {
                        call.sessions.set(LoginSession(principal!!.name))
                        call.respondRedirect("/")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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