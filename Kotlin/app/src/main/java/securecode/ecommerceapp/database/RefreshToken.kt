package securecodewarrior.ecommerceapp.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RefreshToken(@NonNull @PrimaryKey var token: String, var date : Long)

