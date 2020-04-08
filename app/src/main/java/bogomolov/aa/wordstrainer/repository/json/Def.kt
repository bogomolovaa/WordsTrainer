package bogomolov.aa.wordstrainer.repository.json

class Def {
    var pos: String? = null
    var text: String? = null
    var tr: List<Tr>? = null
    override fun toString(): String {
        return "text: " + text + ", pos: " + pos + ", tr: " + tr.toString()
    }
}