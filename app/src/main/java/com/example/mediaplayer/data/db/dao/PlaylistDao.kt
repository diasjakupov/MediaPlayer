package com.example.mediaplayer.data.db.dao

import androidx.room.*
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import com.example.mediaplayer.data.db.entites.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    fun getPlaylistEntity(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist ORDER BY id DESC LIMIT 1 ")
    fun getLastCreatedPlaylist(): PlaylistEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePlaylistEntity(entity: PlaylistEntity)

    @Delete
    suspend fun delete(playlistEntity: PlaylistEntity)

    @Update
    suspend fun update(playlistEntity: PlaylistEntity)
}