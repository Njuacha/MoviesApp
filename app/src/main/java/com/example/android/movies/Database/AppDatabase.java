package com.example.android.movies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.movies.model.Movie;
import com.example.android.movies.model.ReviewWithId;
import com.example.android.movies.model.TrailerVideoWithId;

@Database(entities = {Movie.class, TrailerVideoWithId.class, ReviewWithId.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{

    private static final String DATABASE_NAME = "MoviesApp";
    private static AppDatabase databaseInstance;
    private static final Object LOCK = new Object();

    public static AppDatabase getDatabaseInstance(Context context){
        if(databaseInstance == null){
            synchronized (LOCK){
                databaseInstance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }

        return databaseInstance;
    }

    public abstract MovieDao movieDoa();
    public abstract TrailerDao trailerDoa();
    public abstract ReviewDao reviewDao();

}
