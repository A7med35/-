package com.example.expensesapp.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "transactions_table")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "IN" للعهدة، "OUT" للمصروف
    val date: Long = System.currentTimeMillis()
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions_table ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT SUM(amount) FROM transactions_table WHERE type = 'IN'")
    fun getTotalCustody(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions_table WHERE type = 'OUT'")
    fun getTotalExpenses(): Flow<Double?>
}

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_custody_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
