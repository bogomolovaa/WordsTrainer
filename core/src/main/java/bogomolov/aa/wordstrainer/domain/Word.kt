package bogomolov.aa.wordstrainer.domain

data class Word(
    var id: Int = 0,
    val word: String,
    val translation: String,
    var rank: Int = 0,
    val json: String? = null
) {

    override fun toString() = "$id $word $translation"

    override fun hashCode(): Int = id.toString().hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return id == (other as Word).id
    }
}