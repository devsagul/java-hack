package lambda.javahack.backend

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DBHelper {

    init {
        Database.connect("jdbc:postgresql://db/db?user=secret&password=secret", driver="org.postgresql.Driver")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Credentials)
        }
    }

    fun validateCredentials(user: String, password: String): Boolean {
        return user == "test" && password == "test123"
    }
}

object Credentials : Table() {
    val _id = integer("id").autoIncrement().primaryKey()
    val _user = text("user").uniqueIndex()
    val _password = text("password")
}

object Test: Table() {
    val _text = text("text")
}