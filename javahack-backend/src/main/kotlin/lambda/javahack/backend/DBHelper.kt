package lambda.javahack.backend

import kotlinx.coroutines.selects.select
import lambda.javahack.backend.Accounts._id
import lambda.javahack.backend.Accounts._phone
import lambda.javahack.backend.Accounts._token
import lambda.javahack.backend.Transaction._agent_id
import lambda.javahack.backend.Transaction._issue_datetime
import lambda.javahack.backend.Transaction._sum
import lambda.javahack.backend.Transaction._user_id
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class DBHelper {

    init {
        Database.connect("jdbc:postgresql://localhost/db?user=secret&password=secret", driver="org.postgresql.Driver")
        transaction {
            SchemaUtils.create(Accounts, IPSix, Transaction, Agents)
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
    fun addUser(login: String, pwd: String, lastname: String, middlename: String, firstname: String, INN: String,
                codeNalog: String, activity: String): Boolean {
        try {
            transaction {
                val id = Accounts.insertIgnore {
                    it[_phone] = login
                    it[_password] = Util.getSHA256String(pwd)
                    it[_pic] = ""
                    it[_token] = Util.getSHA256String("$login${Random.nextInt()}")
                } get Accounts._id
                IPSix.insertIgnore {
                    it[_id] = id
                    it[_last_name] = lastname
                    it[_patronymic] = middlename
                    it[_first_name] = firstname
                    it[_inn] = INN
                    it[_inn_department] = codeNalog
                    it[_activity_id] = activity
                }
            }
        } catch (e: IllegalStateException) {
            return false
        }
        return true
    }
    fun getTokenByUid(uid: String): String {
        var token = ""
        transaction {
            Accounts.select { _phone eq uid }.forEach {
                token = it[_token]
            }
        }
        return token
    }
    fun getTransactionsByUser(token: String) {
        var id = 0
        val tas = mutableListOf<Map<String,Any>>()
        transaction {
            Accounts.select { _token eq token }.forEach { id = it[_id] }
            Transaction.select { _user_id eq id }.forEach {
                tas += mapOf("transaction_id" to it[_id],
                        "issue_datetime" to  it[_issue_datetime],
                        "sum" to it[_sum],
                        "agent_id" to it[_agent_id],
                        "agent_name" to "",
                        "agent_pic" to "")
            }
            val agents = Agents.selectAll()
            tas.forEach {

            }
        }
    }

}

object Accounts : Table() {
    val _id = integer("id").autoIncrement().primaryKey()
    val _phone = text("phone").uniqueIndex()
    val _password = text("password")
    val _pic = text("pic") //url
    val _token = text("token")
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
    val _user_id = integer("user_id").references(Accounts._id)
    val _agent_id = integer("agent_id").references(Agents._id)
}

object Agents: Table() {
    val _id = integer("id").primaryKey()
    val _name = text("name")
    val _pic = text("pic") //url
}