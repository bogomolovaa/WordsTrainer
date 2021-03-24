package bogomolov.aa.wordstrainer.repository.json

class Tr {
    var text: String? = null
    var syn: List<Syn>? = null
    override fun toString() = "text: $text, syn: ${syn ?: ""}"
}