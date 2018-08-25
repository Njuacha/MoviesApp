package com.example.android.movies;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hubert on 8/7/18.
 */

class JsonUtil {

    public static List<Movie> parseListOfMoviesJson(String json) {

        // Variable to hold movies
        List<Movie> movies = new ArrayList<>();

        try {

            JSONObject sMoviesJson = new JSONObject(json);

            // Gets results JSON object
            JSONArray results = sMoviesJson.getJSONArray("results");

            // Iterates 20 times through results
            for (int i = 0; i < 20; i++) {

                // Gets a movie JSON object for each loop cycle
                JSONObject movieJSON = results.getJSONObject(i);

                // Gets all fields to create a movie object
                int id = movieJSON.optInt("id");
                String title = movieJSON.optString("original_title");
                String poster_path = movieJSON.optString("poster_path");
                String plot_synopsis = movieJSON.optString("overview");
                int user_rating = movieJSON.optInt("vote_average");
                String release_date = movieJSON.optString("release_date");
                // Instantiates movie object with all fields gotten
                Movie movie = new Movie(id, title, poster_path, plot_synopsis, user_rating, release_date);

                // Adds the movie object list of movies
                movies.add(movie);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

}
