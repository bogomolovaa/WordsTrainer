package bogomolov.aa.wordstrainer.repository.json

class Translation {
    var def: List<Def>? = null
    override fun toString() = def.toString()
}