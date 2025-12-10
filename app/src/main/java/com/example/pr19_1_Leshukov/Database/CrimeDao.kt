package com.example.pr19_1_Leshukov.Database

import androidx.room.Dao
import androidx.room.Query
import com.example.pr19_1_Leshukov.Crime
import java.util.UUID
@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes():List<Crime>
    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id:UUID):Crime?
}