package com.takemetomyanmar.myanmarticket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.takemetomyanmar.myanmarticket.Authentication.AuthService;
import com.takemetomyanmar.myanmarticket.Authentication.AuthenticationApplication;
import com.takemetomyanmar.myanmarticket.Authentication.CustomLoginActivity;
import com.takemetomyanmar.myanmarticket.model.NavDrawerItem;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    protected AuthService mAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        AuthenticationApplication myApp = (AuthenticationApplication) getApplication();
        myApp.setCurrentActivity(this);
        mAuthService = myApp.getAuthService();
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:

                if (!mAuthService.isUserAuthenticated()) {
                    Intent customLoginIntent = new Intent(getApplicationContext(), CustomLoginActivity.class);
                    customLoginIntent.putExtra("position", position);
                    startActivity(customLoginIntent);
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AirportFragment.newInstance(position + 1))
                        .addToBackStack("AirportFragment")
                        .commit();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                NavDrawerItem item = (NavDrawerItem)mNavigationDrawerFragment.getDrawerItem(position);
                if(item.getTitle().equals("Login")) {
                    Intent customLoginIntent = new Intent(getApplicationContext(), CustomLoginActivity.class);
                    customLoginIntent.putExtra("position", position);
                    startActivity(customLoginIntent);
                } else {
                    mAuthService.logout(true);
                }
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_home);
                break;
            case 2:
                mTitle = getString(R.string.title_transfer);
                break;
            case 3:
                mTitle = getString(R.string.title_bus);
                break;
            case 4:
                mTitle = getString(R.string.title_tour);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        //mNavigationDrawerFragment.updateDrawer();

    }
}
