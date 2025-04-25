package Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserProfile")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name:String,
    val age: String,
    val dateOfBirth: String,
    val phone1: String,
    val phone2:String
)