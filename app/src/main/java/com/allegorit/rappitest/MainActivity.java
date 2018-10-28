package com.allegorit.rappitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import Retro.DaoMaster;
import Retro.DaoSession;
import Retro.MovieList;
import Retro.MovieListDao;
import Retro.RetroDataService;
import Retro.RetrofitClientInstance;
import Retro.TopMovie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private GridView gridL;
    private RetroDataService service;
    private int  height, width, page;

    private DaoSession daoSession;
    private MovieListDao movieListDao;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_Popular);
                    popuulateGrid("Popular");
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    popuulateGrid("Top");
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    popuulateGrid("Upcoming");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //retrofit services
        service = RetrofitClientInstance.getRetrofitInstance().create(RetroDataService.class);

        //greendao db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "notes-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        movieListDao = daoSession.getMovieListDao();

        gridL = (GridView) findViewById(R.id.gridL);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        popuulateGrid("Popular");
    }

    private void popuulateGrid(String type){

        if(type.equals("Popular")){
            Call<TopMovie> popularList = (Call<TopMovie>)service.getPopular(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                    Log.d("Main","Popular)");
                    assert response.body() != null;
                    fillGrid(response.body().getResults(),true);
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {
                    fillGrid(movieListDao.queryBuilder()
                            .orderDesc(MovieListDao.Properties.Popularity).list(),false);

                }
            });
        }
        if(type.equals("Top")){
            Call<TopMovie> popularList = (Call<TopMovie>)service.getTop(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                    Log.d("Main","Top)");
                    fillGrid(response.body().getResults(),true);
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {

                    fillGrid(movieListDao.queryBuilder()
                            .orderDesc(MovieListDao.Properties.VoteAverage).list(),false);
                }
            });
        }
        if(type.equals("Upcoming")){
            Call<TopMovie> popularList = (Call<TopMovie>)service.getUpcoming(getResources().getString(R.string.api_key_tmdb));
            popularList.enqueue(new Callback<TopMovie>() {
                @Override
                public void onResponse(Call<TopMovie> call, Response<TopMovie> response) {
                    Log.d("Main","Up)");
                    fillGrid(response.body().getResults(),true);
                }

                @Override
                public void onFailure(Call<TopMovie> call, Throwable t) {

                    fillGrid(movieListDao.loadAll(),false);
                }
            });
        }
    }

    private void insertMovies(List<MovieList> results){
        for (MovieList movieList : results) {
            movieListDao.insertOrReplace(movieList);
        }
    }

    private void fillGrid(List<MovieList> topMovie,boolean online){
        //page = topMovie.getPage();
        insertMovies(topMovie);
        gridL.setAdapter(new  MovieAdapter(height, width,getApplicationContext(),topMovie));
        gridL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, MovieDetail.class);
                intent.putExtra("MID",""+gridL.getAdapter().getItemId(i));
                startActivity(intent);
            }
        });
    }
}
