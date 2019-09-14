package lambda.javahack.backend

import lambda.javahack.backend.Accounts._user
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DBHelper {

    init {
        Database.connect("jdbc:postgresql://db/db?user=secret&password=secret", driver="org.postgresql.Driver")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Accounts)
            Accounts.insertIgnore {
                it[_user] = "user"
                it[_password] = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
            }
        }
    }

    fun validateCredentials(user: String, password: String): Boolean {
        var dbPass = ""
        transaction {
            Accounts.select {_user eq user}.forEach {
                dbPass = it[Accounts._password]
            }
        }
        return  Util.getSHA256String(password) == dbPass
    }
}

object Accounts : Table() {
    val _id = integer("id").autoIncrement().primaryKey()
    val _user = text("user").uniqueIndex()
    val _password = text("password")
}