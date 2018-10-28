package com.allegorit.rappitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.database.Database;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Retro.DaoMaster;
import Retro.DaoSession;
import Retro.Genre;
import Retro.MyMovie;
import Retro.MyMovieDao;
import Retro.RetroDataService;
import Retro.RetrofitClientInstance;
import Retro.TopMovie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity {
    private int height;
    private int width;
    private String MID;
    private RetroDataService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Bundle bundle = getIntent().getExtras();
        MID = bundle.getString("MID");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        height = height/3;

        service = RetrofitClientInstance.getRetrofitInstance().create(RetroDataService.class);
        Call<MyMovie> popularList = (Call<MyMovie>)service.getMovie(MID,getResources().getString(R.string.api_key_tmdb));
        popularList.enqueue(new Callback<MyMovie>() {
            @Override
            public void onResponse(Call<MyMovie> call, Response<MyMovie> response) {
                fillInfo(response.body(),true);
            }

            @Override
            public void onFailure(Call<MyMovie> call, Throwable t) {
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "notes-db");
                Database db = helper.getWritableDb();
                DaoSession daoSession = new DaoMaster(db).newSession();
                MyMovieDao myMovieDao = daoSession.getMyMovieDao();
                fillInfo(myMovieDao.load(Long.parseLong(MID)),true);
            }
        });





    }

    public void fillInfo(MyMovie movie, boolean online){
        if(online){
            ImageView banner = (ImageView)findViewById(R.id.banner);
            Picasso.get() //
                    .load("https://image.tmdb.org/t/p/w500"+movie.getBackdropPath())
                    //.error()//
                    .placeholder(R.drawable.ic_launcher_background) //
                    .resize(width,height)
                    .tag(getApplicationContext()) //
                    .into(banner);

            TextView title = (TextView)findViewById(R.id.title);
            title.setText(movie.getTitle());

            String release = movie.getReleaseDate();
            try {

                Date datedb=new SimpleDateFormat("yyyy-MM-dd").parse(release);

                Log.d("Detail",release);
                Log.d("Detail",""+datedb.getYear());
                TextView year = (TextView)findViewById(R.id.year);
                year.setText(""+(datedb.getYear()+1900));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView duration = (TextView)findViewById(R.id.duration);
            duration.setText(movie.getRuntime().toString()+" min");

            String genre= "";
            for (int i = 0; i<movie.getGenres().size(); i++) {
                genre+=movie.getGenres().get(i).getName();
                if((i+1)==movie.getGenres().size())genre+= "";
                else genre+="/";
            }
            TextView genreT = (TextView)findViewById(R.id.gener);
            genreT.setText(genre);

            TextView rate = (TextView)findViewById(R.id.rate);
            NumberFormat formatter = new DecimalFormat("#0.0");
            rate.setText(formatter.format(movie.getVoteAverage())+"/10.0");

            TextView descrip = (TextView)findViewById(R.id.description);
            descrip.setText(movie.getOverview());
        }
        else{

        }

    }
}
