# Keep data classes used in Room entities
-keep class com.babynutricare.app.data.local.entity.** { *; }
-keep class com.babynutricare.core.domain.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *