package bogomolov.aa.wordstrainer.repository.json

class Tr {
    var text: String? = null
    var syn: List<Syn>? = null
    override fun toString(): String {
        return "text: " + text + ", syn: " + if (syn != null) syn.toString() else ""
    }
}