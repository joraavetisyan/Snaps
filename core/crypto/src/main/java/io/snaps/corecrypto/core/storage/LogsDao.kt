package io.snaps.corecrypto.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.snaps.corecrypto.entities.LogEntry

@Dao
interface LogsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(logEntry: LogEntry)

    @Query("SELECT * FROM LogEntry ORDER BY id")
    fun getAll(): List<LogEntry>

}
