package securecodewarrior.ecommerceapp.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.commonsware.cwac.saferoom.SafeHelperFactory
import securecodewarrior.ecommerceapp.SingletonHolder


@Database(entities = [RefreshToken::class], version = 1, exportSchema = false)
abstract class CipherDatabase : RoomDatabase() {

    abstract fun refreshTokenDAO(): RefreshTokenDAO

    companion object {

        @Volatile
        private var INSTANCE: CipherDatabase? = null

        fun getInstance(context: Context, factory: SafeHelperFactory): CipherDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context, factory).also { INSTANCE = it }
                }

        fun getInstance(): CipherDatabase? = INSTANCE

        private fun buildDatabase(context: Context, safeHelperFactory: SafeHelperFactory) =
                Room.databaseBuilder(context.applicationContext,
                        CipherDatabase::class.java, "social_app.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .openHelperFactory(safeHelperFactory)
                        .build()
    }
}