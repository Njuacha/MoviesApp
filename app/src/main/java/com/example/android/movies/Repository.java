package com.example.android.movies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.movies.Database.AppDatabase;
import com.example.android.movies.model.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class Repository {



    public static String saveImageInFile(Context context, String originalTitle, ImageView imageView) {
        // Get Bitmap from image
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        if(drawable == null){
            return null;
        }

        Bitmap bitmap = drawable.getBitmap();

        // Declare File out put stream to be used to write to a file
        FileOutputStream fileOutputStream;
        File directory = context.getFilesDir();

        File file = new File(directory, originalTitle);

        try {

            fileOutputStream = context.openFileOutput(file.getName(), MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return String.valueOf(Uri.fromFile(file));
    }

    public static void removePicFrmInternalMermory(String posterPath) {
        if(posterPath != null){
            File file = new File(Uri.parse(posterPath).getPath());
            file.delete();
        }
    }

    public static void removeFromDatabase(Context context,Movie movie) {
        AppDatabase db = AppDatabase.getDatabaseInstance(context);

        db.movieDoa().deleteMovie(movie);
        db.trailerDoa().deleteTrailersForMovie(movie.getId());
        db.reviewDao().deleteReviewsForMovie(movie.getId());
    }
}
