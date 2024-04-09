class IncorrectDataException(private val msg: String): RuntimeException(msg) {
    constructor() : this("Error")
}