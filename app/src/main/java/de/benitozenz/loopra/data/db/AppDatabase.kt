package de.benitozenz.loopra.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.benitozenz.loopra.data.model.ChallengeEntity
import de.benitozenz.loopra.data.model.ChallengeProgressEntity
import de.benitozenz.loopra.data.model.ScriptEntity
import de.benitozenz.loopra.data.model.UserProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ScriptEntity::class,
        ChallengeEntity::class,
        ChallengeProgressEntity::class,
        UserProgressEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scriptDao(): ScriptDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun challengeProgressDao(): ChallengeProgressDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loopra_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedChallenges(database.challengeDao())
                    database.userProgressDao().upsertProgress(UserProgressEntity())
                }
            }
        }

        private suspend fun seedChallenges(dao: ChallengeDao) {
            val challenges = listOf(
                ChallengeEntity(
                    id = 1,
                    title = "Hello World",
                    description = "Output exactly 'Hello World!' followed by a newline.\n\nThe classic first program.",
                    code = "",
                    expectedOutput = "Hello World!\n",
                    difficulty = "EASY",
                    optimalSteps = 78,
                    goodSteps = 100,
                    maxSteps = 200,
                    sortOrder = 1
                ),
                ChallengeEntity(
                    id = 2,
                    title = "Simple Addition",
                    description = "Add the values in cell 0 and cell 1, store the result in cell 0, and output it.\n\nStart with cell 0 = 3, cell 1 = 5.",
                    code = "+++>+++++",
                    expectedOutput = (8).toChar().toString(),
                    difficulty = "EASY",
                    optimalSteps = 16,
                    goodSteps = 25,
                    maxSteps = 50,
                    sortOrder = 2
                ),
                ChallengeEntity(
                    id = 3,
                    title = "Multiplication",
                    description = "Multiply cell 0 × cell 1 and output the result.\n\nStart with cell 0 = 3, cell 1 = 4.\nExpected output: 12 as a character (ASCII 12 = form feed, shown as a dot).",
                    code = "+++>++++",
                    expectedOutput = (12).toChar().toString(),
                    difficulty = "MEDIUM",
                    optimalSteps = 35,
                    goodSteps = 50,
                    maxSteps = 100,
                    sortOrder = 3
                ),
                ChallengeEntity(
                    id = 4,
                    title = "Alphabet",
                    description = "Output the lowercase alphabet from 'a' to 'z'.\n\nHint: Start with 'a' (97) and increment until 'z' (122).",
                    code = "",
                    expectedOutput = "abcdefghijklmnopqrstuvwxyz",
                    difficulty = "MEDIUM",
                    optimalSteps = 45,
                    goodSteps = 70,
                    maxSteps = 150,
                    sortOrder = 4
                ),
                ChallengeEntity(
                    id = 5,
                    title = "Fibonacci",
                    description = "Output the first 10 Fibonacci numbers as ASCII characters.\n\n0, 1, 1, 2, 3, 5, 8, 13, 21, 34\n\nDon't worry about non-printable chars — just output them in order.",
                    code = "",
                    expectedOutput = "\u0000\u0001\u0001\u0002\u0003\u0005\b\r\u0015\"",
                    difficulty = "HARD",
                    optimalSteps = 120,
                    goodSteps = 180,
                    maxSteps = 300,
                    sortOrder = 5
                )
            )
            dao.insertAll(challenges)
        }
    }
}
