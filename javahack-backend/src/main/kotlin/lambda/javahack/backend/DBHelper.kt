package lambda.javahack.backend

import kotlinx.coroutines.selects.select
import lambda.javahack.backend.Accounts._id
import lambda.javahack.backend.Accounts._phone
import lambda.javahack.backend.Accounts._token
import lambda.javahack.backend.Agents._name
import lambda.javahack.backend.Agents._pic
import lambda.javahack.backend.Transaction._agent_id
import lambda.javahack.backend.Transaction._issue_datetime
import lambda.javahack.backend.Transaction._sum
import lambda.javahack.backend.Transaction._user_id
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.random.Random

class DBHelper {

    init {
        Database.connect("jdbc:postgresql://db/db?user=secret&password=secret", driver="org.postgresql.Driver")
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
    fun getTransactionsByUser(token: String): List<Map<String, Any>> {
        var id = 0
        val tas = mutableListOf<MutableMap<String,Any>>()
        transaction {
            Accounts.select { _token eq token }.forEach { id = it[_id] }
            Transaction.select { _user_id eq id }.forEach {
                tas += mutableMapOf<String, Any>("transaction_id" to it[Transaction._id],
                        "issue_datetime" to  it[_issue_datetime].toString(),
                        "sum" to it[_sum],
                        "agent_id" to it[_agent_id],
                        "document_id" to it[Transaction._documet_id],
                        "comment" to it[Transaction._comment],
                        "agent_name" to "",
                        "agent_pic" to "")
            }
            val agents = Agents.selectAll()
            tas.forEach {
                agents.forEach { ag ->
                    if (it["agent_id"] == ag[Agents._id] ) {
                        it["agent_name"] = ag[_name]
                        it["agent_pic"] = ag[_pic]
                    }
                }
            }
        }
        return tas
    }

    fun getDeclaration(token: String): Map<String,Any> {
        var data = mutableMapOf<String,Any>()
        var id = 0
        var phone = ""
        transaction {
            Accounts.select { _token eq token }.forEach {
                id = it[Accounts._id]
                phone = it[_phone]
            }
            IPSix.select { IPSix._id eq id }.forEach { tb ->
                data["inn"] = tb[IPSix._inn]
                data["kpp"] = ""
                data["fio_f_"] = tb[IPSix._last_name]
                data["fio_n_"] = tb[IPSix._first_name]
                data["fio_o_"] = tb[IPSix._patronymic]
                data["mob_n"] = phone
                data["n_p_k"] = ""
                data["o_year"] = Calendar.getInstance().get(Calendar.YEAR)
                data["nal_co"] = tb[IPSix._inn_department]
                data["resp_id"] = 1
                data["day"] = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                data["mounth"] = Calendar.getInstance().get(Calendar.MONTH); //
                data["year"] = Calendar.getInstance().get(Calendar.YEAR);
            }
        }
        return data
    }
    fun addTransaction(token: String, issue_date: String, sum: Double, agent_name: String, document_id: String,
    docType: DocType) {
        var comment = ""
        comment = when (docType) {
            DocType.CHEK -> "Поступление в кассу: оплата от покупателя по чеку №$document_id"
            DocType.DOGOVOR -> "Поступление на счёт от $agent_name по договору №$document_id"
            DocType.OFFERTA -> "Поступление на счёт от $agent_name по публичной оферте №$document_id"
        }
        transaction {
            if (agent_name != "") {
                Agents.insertIgnore {
                    it[Agents._name] = agent_name
                    it[_pic] = ""
                    it[_type] = ""
                    it[_category] = ""
                }
                var ag_id = 0
                Agents.select { Agents._name eq agent_name }.forEach { ag_id = it[Agents._id] }
                var user_id = 0
                Accounts.select { Accounts._token eq token }.forEach { user_id = it[Accounts._id] }
                Transaction.insert {
                    it[_issue_datetime] = issue_date
                    it[_sum] = sum
                    it[_user_id] = user_id
                    it[_agent_id] = ag_id
                    it[_documet_id] = document_id
                    it[_comment] = comment
                }
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
    val _issue_datetime = text("issue_datetime")
    val _sum = double("sum")
    val _user_id = integer("user_id").references(Accounts._id)
    val _agent_id = integer("agent_id").references(Agents._id)
    val _documet_id = text("document_id")
    val _comment = text("comment")
}

object Agents: Table() {
    val _id = integer("id").primaryKey()
    val _name = text("name").uniqueIndex()
    val _pic = text("pic") //url
    val _type = text("type")
    val _category = text("category")
}