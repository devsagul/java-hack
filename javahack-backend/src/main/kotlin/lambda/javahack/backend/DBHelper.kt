package lambda.javahack.backend

import lambda.javahack.backend.Accounts._phone
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DBHelper {

    init {
        Database.connect("jdbc:postgresql://localhost/db?user=secret&password=secret", driver="org.postgresql.Driver")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Accounts)
            Accounts.insertIgnore {
                it[_phone] = "user"
                it[_password] = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
            }
        }
    }

    fun validateCredentials(phone: String, password: String): Boolean {
        var dbPass = ""
        transaction {
            Accounts.select {_phone eq phone}.forEach {
                dbPass = it[Accounts._password]
            }
        }
        return  Util.getSHA256String(password) == dbPass
    }
}

object Accounts : Table() {
    val _id = integer("id").autoIncrement().primaryKey()
    val _phone = text("phone").uniqueIndex()
    val _password = text("password")
    val _pic = text("pic") //url
}

object IPSix : Table() {
    val _id = integer("id").uniqueIndex().references(Accounts._id)
    val _first_name = text("first_name")
    val _patronymic = text("patronymic")
    val _last_name = text("last_name")
    val _inn = text("inn")
    val _activity_id = text("activity_id")
    val _inn_department = text("inn_departmant")
}

object Transaction : Table() {
    val _id = integer("id").autoIncrement().primaryKey()
    val _issue_datetime = datetime("issue_datetime")
    val _sum = double("sum")
    val _user_id = integer("id").references(Accounts._id)
    val _agent_id = integer("agent_id").references(Agents._id)
}

object Agents: Table() {
    val _id = integer("id").primaryKey()
    val _name = text("name")
    val _pic = text("pic") //url
}