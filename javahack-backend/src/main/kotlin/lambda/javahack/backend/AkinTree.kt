package lambda.javahack.backend

data class TreeNode(var question: String, var id: Int, val childs: List<Any>)

class AkinTree {
    val nodes = mutableListOf<TreeNode>()

    init {
        nodes += TreeNode("Поздравляем! Сколько денег вы получили?", id = 0, childs = listOf(mapOf("id" to 1, "answer" to "₽")))
        nodes += TreeNode("Солидно! Какого числа это произошло?", id = 1, childs = listOf(
                mapOf("id" to 2, "answer" to "Далее")
        ))
        nodes += TreeNode("Совсем недавно! А чем было подтверждено получение?", id = 2, childs = listOf(
                mapOf("id" to 3, "answer" to "Чек"), mapOf("id" to 4, "answer" to "Договор"), mapOf("id" to 4, "answer" to "Публичная офкрта")
        ))
        nodes += TreeNode("ПОСЛЕДНИЙ ШАГ!\n Подскажите номер подтверждающего документа?", id = 3, childs = listOf(
                mapOf("id" to 5, "answer" to "ГОТОВО")
        ))
        nodes += TreeNode("А данные контрагента какие?", id = 4, childs = listOf(
                mapOf("id" to 3, "answer" to "Далее")
        ))
    }
}
enum class DocType {
    CHEK, DOGOVOR, OFFERTA
}