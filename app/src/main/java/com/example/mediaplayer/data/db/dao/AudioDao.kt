package com.example.mediaplayer.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediaplayer.data.db.entites.AudioEntity
import com.example.mediaplayer.data.db.entites.PlaylistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AudioDao {

    @Query("SELECT * FROM audio WHERE playlistId =:playlistId")
    fun getAudioListByPlaylistId(playlistId:Int): Flow<List<AudioEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewAudio(audio: AudioEntity)

    @Query("DELETE FROM audio WHERE playlistId =:playlistId")
    suspend fun delete(playlistId: Int)
}