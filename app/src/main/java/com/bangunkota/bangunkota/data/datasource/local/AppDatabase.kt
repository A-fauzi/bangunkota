package com.bangunkota.bangunkota.data.datasource.local

//@Database(entities = [CommunityPost::class], version = 1)
//abstract class AppDatabase: RoomDatabase() {
//
//    abstract fun communityPostDao(): CommunityPostDao
//
//    companion object {
//        @Volatile
//        private var instance: AppDatabase? = null
//
//        fun getInstance(context: Context): AppDatabase {
//            return instance ?: synchronized(this) {
//                instance ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "bangun_kota"
//                ).build().also { instance = it }
//            }
//        }
//    }
//}