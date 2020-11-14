package ca.sashaphoto.jam20;

import androidx.room.*;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}