package AuthDataClass

data class LoginResponse(
    val success: Boolean,
    val token: String,
    val user: UserX
)