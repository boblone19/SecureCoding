package securecodewarrior.ecommerceapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RefreshTokenDAO {

    @Insert
    fun insertToken(token: RefreshToken)

    @Delete
    fun deleteToken(token: RefreshToken)

    @Query("SELECT * FROM refreshtoken ORDER BY date LIMIT 1")
    fun getLastToken(): RefreshToken

}