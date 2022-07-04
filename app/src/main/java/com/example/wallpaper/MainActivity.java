package com.example.wallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wallpaper.adapters.SuggestedAdapter;
import com.example.wallpaper.adapters.WallpaperAdapter;
import com.example.wallpaper.interfaces.RecyclerViewClickListener;
import com.example.wallpaper.models.SuggestedModel;
import com.example.wallpaper.models.WallpapersModel;
import com.google.android.material.navigation.NavigationView;
import com.tuyenmonkey.mkloader.model.Line;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener {


    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView, topMostRecyclerView;


    WallpaperAdapter wallpaperAdapter;
    List<WallpapersModel> wallpapersModelList;

    SuggestedAdapter suggestedAdapter;

    ArrayList<SuggestedModel> suggestedModels = new ArrayList<>();

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    ProgressBar progressBar;

    TextView replaceTitle;


    EditText searchEt;
    ImageView searchIv;
    int pageNumber = 1;

    String url = "https://api.pexels.com/v1/curated?page=" + pageNumber + "&per_page=80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationDrawer();

        View headerView = navigationView.getHeaderView(0);
        ImageView appLogo = headerView.findViewById(R.id.app_image);

        recyclerView = findViewById(R.id.recyclerView);
        topMostRecyclerView = findViewById(R.id.suggestedRecyclerView);

        wallpapersModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpapersModelList);

        recyclerView.setAdapter(wallpaperAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchWallpaper();
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        replaceTitle = (TextView) findViewById(R.id.topMostTitle);


        fetchWallpaper();

        suggestedItem();


        searchEt = findViewById(R.id.searchEt);
        searchIv = findViewById(R.id.search_image);

        searchIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String search = searchEt.getText().toString().toUpperCase();
                replaceTitle.setText(search);
                progressBar.setVisibility(View.VISIBLE);
                url = "https://api.pexels.com/v1/search?query="+search+"&per_page="+pageNumber;
                //
                wallpapersModelList.clear();
                fetchWallpaper();
                progressBar.setVisibility(View.GONE);
                searchEt.setText("");
            }
        });
    }

    private void navigationDrawer() {


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                final float diffScaledOffSet = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffSet;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffSet / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                Toast.makeText(this, "home clicked", Toast.LENGTH_SHORT).show();

            case R.id.nav_about:
                Toast.makeText(this, "about clicked", Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    private void suggestedItem() {
        topMostRecyclerView.setHasFixedSize(true);
        topMostRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        suggestedModels.add(new SuggestedModel(R.drawable.images,"Trending"));
        suggestedModels.add(new SuggestedModel(R.drawable.naature_back,"Nature"));
        suggestedModels.add(new SuggestedModel(R.drawable.architecture_back,"Architecture"));
        suggestedModels.add(new SuggestedModel(R.drawable.people_back,"People"));
        suggestedModels.add(new SuggestedModel(R.drawable.business_back,"Business"));
        suggestedModels.add(new SuggestedModel(R.drawable.health_back,"Health"));
        suggestedModels.add(new SuggestedModel(R.drawable.fashion_back,"Fashion"));
        suggestedModels.add(new SuggestedModel(R.drawable.film_back,"Film"));
        suggestedModels.add(new SuggestedModel(R.drawable.travel_back,"Travel"));

        suggestedAdapter = new SuggestedAdapter(suggestedModels,MainActivity.this);
        topMostRecyclerView.setAdapter(suggestedAdapter);
    }

    private void fetchWallpaper() {

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for (int i = 0; i < length; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");
                                String photographerName = object.getString("photographer");

                                JSONObject objectImage = object.getJSONObject("src");
                                String originalUrl = objectImage.getString("original");
                                String mediumUrl = objectImage.getString("medium");

                                WallpapersModel wallpapersModel = new WallpapersModel(id, originalUrl, mediumUrl, photographerName);
                                wallpapersModelList.add(wallpapersModel);
                                pageNumber++;
                            }
                            wallpaperAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "563492ad6f91700001000001601bb6fc740040b390e89e707307a23b");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public void onItemClick(int position) {

        progressBar.setVisibility(View.VISIBLE);
        if (position == 0){

            replaceTitle.setText("Trending");
            url = "https://api.pexels.com/v1/search?per_page=80&query=trending&per_page="+pageNumber;
            //"https://api.pexels.com/v1/search?query="+search+"&per_page="+pageNumber;
            //"https://api.pexels.com/v1/search?page="+pageNumber+"&per_page=80&query=trending
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 1){

            replaceTitle.setText("Nature");
            url = "https://api.pexels.com/v1/search?per_page=80&query=nature&per_page="+pageNumber;;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 2){

            replaceTitle.setText("Architecture");
            url = "https://api.pexels.com/v1/search?per_page=80&query=architecture&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 3){

            replaceTitle.setText("People");
            url = "https://api.pexels.com/v1/search?per_page=80&query=people&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 4){

            replaceTitle.setText("Business");
            url = "https://api.pexels.com/v1/search?per_page=80&query=business&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 5){

            replaceTitle.setText("Health");
            url = "https://api.pexels.com/v1/search?per_page=80&query=health&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 6){

            replaceTitle.setText("Fashion");
            url = "https://api.pexels.com/v1/search?per_page=80&query=fashion&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 7){

            replaceTitle.setText("Film");
            url = "https://api.pexels.com/v1/search?per_page=80&query=film&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }else if (position == 8){

            replaceTitle.setText("Travel");
            url = "https://api.pexels.com/v1/search?per_page=80&query=travel&per_page="+pageNumber;
            wallpapersModelList.clear();
            fetchWallpaper();
            progressBar.setVisibility(View.GONE);

        }
    }
}