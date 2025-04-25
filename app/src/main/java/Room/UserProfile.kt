import Room.User
import Room.UserDao
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room


@Database(entities = [User::class], version = 1)
abstract class UserProfile : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserProfile? = null

        fun getDatabase(context: Context): UserProfile {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserProfile::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
