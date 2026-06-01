package PermissionsDataClass

data class Permission(
    val name: String,
    val description: String,
    val type: String,
    val status: Boolean
)
