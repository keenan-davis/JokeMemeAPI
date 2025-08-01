package vcmsa.projects.apiacitivity

data class Joke(
    val type: String,
    val setup: String?,
    val delivery: String?,
    val joke: String?
)

data class MemeResponse(
    val success: Boolean,
    val data: MemeData
)

data class MemeData(
    val url: String
)