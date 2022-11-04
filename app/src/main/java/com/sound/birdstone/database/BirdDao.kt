package com.sound.birdstone.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface BirdDao {


    @Insert
    suspend fun addAllBirds(birdLIST: List<BirdEntity>)


    @Query("Select * from Bird where bird_type=:type ORDER BY bird_name ASC")
    fun getBirdTypeData(type: Int): Flow<List<BirdEntity>>

    @Query("Select*from bird where bird_favorite=1")
    fun getBirdFavorite(): Flow<List<BirdEntity>>


    @Query("select * from Bird")
    fun getAllBird(): List<BirdEntity>


    @Query("select * from Bird ORDER BY bird_name ASC")
    fun allBirds(): Flow<List<BirdEntity>>


    @Query("update Bird set bird_favorite =:favorite WHERE id =:id ")
    suspend fun update(favorite: Int, id: Int)


}