package com.example.domesticnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //city name
    private final String CITY = "City";

    //Title bar and sidebar
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView title;
    private WebView newsWebView;


    //search view toolbar
    SearchView.SearchAutoComplete mSearchAutoComplete;
    SearchView mSearchView;
    String CURRENT_URL = "";


    //get time and date
    Date newDate = new Date(System.currentTimeMillis());
    String allshijian = newDate.toString();
    String riqi = allshijian.split(" ")[1] + " " + allshijian.split(" ")[2] + " " + allshijian.split(" ")[5];
    String shijian = allshijian.split(" ")[3];



    //get loaction
    TextView locationText;
    TextView locationTextByManager;

    protected LocationManager locationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_CODE = 1;

    //Listen for activity in the lower column
    BottomNavigationView bottom_navigation;

    //Get 3 buttom_nav fragment
    private News_Fragment news_fragment;
    private Mine_Fragment mine_fragment;
    private Mine_Fragment2 mine_fragment2;
    private int position;
    private static final String POSITION = "position";
    private static final String SELECT_ITEM = "bottomNavigationSelectItem";
    private static final int FRAGMENT_NEWS = 0;
    private static final int FRAGMENT_Mine = 1;
    private static final int FRAGMENT_Mine1 = 2;


    boolean auto_switch = false;
    Button yes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (savedInstanceState != null) {
            //Get the full class name of the 3 class to avoid fragment crush after using switch mode function
            news_fragment = (News_Fragment) getSupportFragmentManager().findFragmentByTag(News_Fragment.class.getName());
            mine_fragment = (Mine_Fragment) getSupportFragmentManager().findFragmentByTag(Mine_Fragment.class.getName());
            mine_fragment2 = (Mine_Fragment2) getSupportFragmentManager().findFragmentByTag(Mine_Fragment2.class.getName());

            //initialize when entering the app and prevent crashes due to no fragment
            showFragment(savedInstanceState.getInt(POSITION));
            bottom_navigation.setSelectedItemId(savedInstanceState.getInt(SELECT_ITEM));
        } else {
            showFragment(FRAGMENT_NEWS);
        }

    }

    private void init() {
        //Get toolbar
        toolbar = findViewById(R.id.toolbar);

        //get location button
        locationText = (TextView) findViewById(R.id.textView111);
        locationTextByManager = (TextView) findViewById(R.id.textView222);

        //show city
        title = findViewById(R.id.title);
        title.setText(CITY);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        //listen the sidebar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.inflateMenu(R.menu.switch_city);


        //sidebar button
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.cebianlan_open, R.string.cebianlan_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //Lower bottom bar
        bottom_navigation = findViewById(R.id.bottom_navigation);

        bottom_navigation.setSelectedItemId(R.id.action_news);


        //lower bottom bar button activity
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setSelectedItemId(R.id.action_news);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                hideFragment(ft);
                switch (item.getItemId()) {
                    case R.id.action_news:
                        showFragment(FRAGMENT_NEWS);
                        break;
                    case R.id.action_mine:
                        showFragment(FRAGMENT_Mine);
                        break;
                    case R.id.action_mine1:
                        showFragment(FRAGMENT_Mine1);
                }
                return true;
            }
        });

    }


    private void showFragment(int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragment(ft);
        position = index;
        switch (index) {
            case FRAGMENT_NEWS:
                title.setText(CITY);
                if (news_fragment == null) {
                    news_fragment = news_fragment.getInstance();
                    ft.add(R.id.container, news_fragment, News_Fragment.class.getName());
                } else {
                    ft.show(news_fragment);
                }
                break;
            case FRAGMENT_Mine:
                title.setText(R.string.mine3);
                if (mine_fragment == null) {
                    mine_fragment = mine_fragment.getInstance();
                    ft.add(R.id.container, mine_fragment, Mine_Fragment.class.getName());
                } else {
                    ft.show(mine_fragment);
                }
                break;
            case FRAGMENT_Mine1:
                title.setText(R.string.mine6);
                if (mine_fragment2 == null) {
                    mine_fragment2 = mine_fragment2.getInstance();
                    ft.add(R.id.container, mine_fragment2, Mine_Fragment2.class.getName());
                } else {
                    ft.show(mine_fragment2);
                }
        }

        ft.commit();

    }

    private void hideFragment(FragmentTransaction ft) {
        if (mine_fragment != null) {
            ft.hide(mine_fragment);
        }
        if (mine_fragment2 != null) {
            ft.hide(mine_fragment2);
        }
        if (news_fragment != null) {
            ft.hide(news_fragment);
        }
    }


    //Still identifies the position of the buttom_navigation after using Switch_Mode
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, position);
        outState.putInt(SELECT_ITEM, bottom_navigation.getSelectedItemId());
    }


//        //Restore the state
//        if (savedInstanceState != null) {
//            newsWebView.restoreState(savedInstanceState.getBundle("webViewState"));
//        }


    @Override

    //click sidebar effect
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.night_mode:
                int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (mode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                break;
            case R.id.rili:
                Toast.makeText(this, "Time:" + riqi, Toast.LENGTH_SHORT).show();
                break;
            case R.id.shijian:
                Toast.makeText(this, shijian, Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //click button to open the sidebar
    public void OnBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.switch_city, menu);

        //select city
        return super.onCreateOptionsMenu(menu);
    }


    //toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_switch_city:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose City");
                builder.setIcon(R.drawable.location);
                final String itemsId[] = {"姑苏区", "相城区", "虎丘区", "吴中区", "太仓市"};
                final boolean[] checkedItems = new boolean[]{false, false, false, false, false};
                builder.setMultiChoiceItems(itemsId, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
                builder.setPositiveButton("Yes", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("checkedItems", "checkedItems: " + checkedItems);
                        String text = "";
                        boolean hasSelected = false;
                        for (int i = 0; i < itemsId.length; i++) {
                            text += checkedItems[i] ? itemsId[i] + "," : "";
                            if (checkedItems[i]) {
                                hasSelected = checkedItems[i];
                                title.setText(itemsId[i]);
                                newsWebView = findViewById(R.id.newsWebview);
                                newsWebView.setWebViewClient(new WebViewClient());
                                newsWebView.setWebChromeClient(new WebChromeClient());
                                newsWebView.loadUrl("http://121.37.95.54:3001/news?address=" + itemsId[i]);
                                WebSettings webSettings = newsWebView.getSettings();
                                newsWebView.setVerticalScrollBarEnabled(true);
                                webSettings.setJavaScriptEnabled(true);

                                break;
                            }
                        }
                        if (hasSelected) {
                            Toast.makeText(MainActivity.this, "Submit Successfully！", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Need a choice！", Toast.LENGTH_SHORT).show();
                            return;
                        }


                    }
                });
                break;
            case R.id.toolbar_quit:
                My_Dialogue m1 = new My_Dialogue(this, R.style.mydialogue);
                m1.show();
                break;
            case R.id.toolbar_fixed_switch_mode:
                My_Dialogue6 m6 = new My_Dialogue6(this, R.style.mydialogue);
                m6.show();
                yes = m6.findViewById(R.id.yes111111);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        auto_switch_mode();
                        m6.dismiss();
                    }
                });

        }

        return super.onOptionsItemSelected(item);


    }

    //auto_switch_mode
    //during 22:00-6:00, app will turn night mode automatically, otherwise it will turn day mode
    private void auto_switch_mode() {
        int nightStartHour = 22;
        int nightStartMinute = 00;
        int dayStartHour = 06;
        int dayStartMinute = 00;

        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        int nightValue = nightStartHour * 60 + nightStartMinute;
        int dayValue = dayStartHour * 60 + dayStartMinute;
        int currentValue = currentHour * 60 + currentMinute;

        if (currentValue >= nightValue || currentValue <= dayValue) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    // Get last known location
    public void getLastKnownLocation(View view) {


        Location defaultLocation = new Location("");
        defaultLocation.setLatitude(31.278097d);
        defaultLocation.setLongitude(120.744352d);
        Location location = null;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                Log.d("providers", provider);
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l;
                }
            }

//          location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //Log.d("location", location.toString());

            if (location != null) {


                String locationString = location.getLatitude() + "," + location.getLongitude();

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connMgr != null) {
                    networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        new FetchAddress().execute(locationString);


                    }

                }


            } else {
                //title.setText(R.string.connectionFailed);
                Toast.makeText(MainActivity.this, R.string.checkNetworkState, Toast.LENGTH_LONG).show();
                String locationString = defaultLocation.getLatitude() + "," + defaultLocation.getLongitude();

                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connMgr != null) {
                    networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        new FetchAddress().execute(locationString);


                    }

                }

//              drawer.closeDrawer(GravityCompat.START);
            }

        } else {
            requestLocationPermission();
            //drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new android.app.AlertDialog.Builder(this).setTitle("Permission needed")
                    .setMessage("Need Permission")
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, MY_PERMISSIONS_REQUEST_LOCATION_CODE))
                    .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())

                    .create()
                    .show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSIONS_REQUEST_LOCATION_CODE);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && newsWebView.canGoBack()) {
            newsWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


    public class FetchAddress extends AsyncTask<String, Void, String> {


//        private WeakReference<TextView> title;
//        private WeakReference<WebView> newsWebView;
//
//
//        public FetchAddress(TextView locationText, WebView webview) {
//            this.title = new WeakReference<>(locationText);
//            this.newsWebView = new WeakReference<>(webview);
//        }


        @Override
        protected String doInBackground(String... strings) {
            return NetworkUtils.getAddress(strings[0]);
        }

        //Load the URL corresponding to the address
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void onPostExecute(String s) {



//Update the UI at the interface layer and update the webview here
            super.onPostExecute(s);
            try {
                //...
                JSONObject q = new JSONObject(s);
                Log.d("Json", q.toString());
                String formatted_address = null;


                try {
                    JSONObject result = q.getJSONObject("result");
                    formatted_address = result.getJSONObject("addressComponent").getString("district");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (formatted_address != null) {
//                locationText.get().setText(formatted_address);
                    title.setText(formatted_address);
                    String titleText = title.getText().toString();
                    Log.d("title text:", titleText);

                    newsWebView = findViewById(R.id.newsWebview);
                    newsWebView.setWebViewClient(new WebViewClient());
                    newsWebView.setWebChromeClient(new WebChromeClient());
                    newsWebView.loadUrl("http://121.37.95.54:3001/news?address=" + titleText);
                    WebSettings webSettings = newsWebView.getSettings();
                    newsWebView.setVerticalScrollBarEnabled(true);
                    webSettings.setJavaScriptEnabled(true);


                } else {
//                locationText.get().setText(R.string.no_results);
                    title.setText(R.string.no_results);
                }


            } catch (JSONException e) {
                // If onPostExecute does not receive a proper JSON string,
                // update the UI to show failed results.
//            locationText.get().setText(R.string.no_results);
                e.printStackTrace();
            }

        }
    }

}
