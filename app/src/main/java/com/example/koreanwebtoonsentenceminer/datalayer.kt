package com.example.koreanwebtoonsentenceminer

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

@Entity(tableName = "dictionary")
data class Translation(
    // SHA-256 hash prevents duplicate captures from entering Anki
    @PrimaryKey
    @ColumnInfo(name="id_hash")
    val idHash: String,

    @ColumnInfo(name="korean_word")
    val koreanWord: String,

    @ColumnInfo(name="english_definition") 
    val englishDefinition: String,

    @ColumnInfo(name="parts_of_speech") 
    val partsOfSpeech: String,

    @ColumnInfo(name="payload_data")
    val payloadData: String
) {
    companion object {
        fun generateHash(text: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(text.trim().toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}

// Appended _fts to the table name so it's instantly recognizable as a shadow table
@Fts4(contentEntity = Translation::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "koreanWord_fts") 
data class KoreanWordFts(
    @ColumnInfo(name = "korean_word") 
    val koreanWord: String
)

@Dao
interface DictionaryDao {
    // OnConflictStrategy.IGNORE to silently drop duplicates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg translation: Translation)

    @Delete
    suspend fun delete(translation: Translation)

    // Room maps the main table's implicit rowid to the FTS table automatically
    @Query("""
        SELECT dictionary.* FROM dictionary 
        JOIN koreanWord_fts ON dictionary.rowid = koreanWord_fts.rowid 
        WHERE koreanWord_fts MATCH :searchQuery
    """)
    fun getAll(searchQuery: String): Flow<List<Translation>>
}