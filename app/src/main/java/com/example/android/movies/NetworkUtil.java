package com.example.android.movies;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by hubert on 8/7/18.
 */

final class NetworkUtil {

    private NetworkUtil(){

    }

    public static String getMoviesFromMovieDbWebsite(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if(hasInput){
                return scanner.next();
            }else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}