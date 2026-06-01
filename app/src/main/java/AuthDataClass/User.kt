package AuthDataClass

data class RegisterResponse(
    val success: Boolean,
    val token: String,
    val user: UserX
)