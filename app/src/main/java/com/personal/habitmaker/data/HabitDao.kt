package com.personal.habitmaker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<Completion>>

    @Query("SELECT * FROM completions WHERE habitId = :habitId")
    suspend fun getCompletionsForHabit(habitId: Long): List<Completion>

    @Query("DELETE FROM completions WHERE habitId = :habitId AND date = :date AND time = :time")
    suspend fun deleteCompletion(habitId: Long, date: String, time: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(completion: Completion)

    @Query("DELETE FROM completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)
}
