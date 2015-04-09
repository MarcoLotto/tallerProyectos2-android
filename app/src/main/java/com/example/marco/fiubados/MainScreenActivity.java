package com.example.marco.fiubados;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.marco.fiubados.TabScreens.FriendsTabScreen;
import com.example.marco.fiubados.TabScreens.TabScreen;
import com.example.marco.fiubados.TabScreens.WallTabScreen;

/**
 * Activity contenedora de todos los tabs
 */
public class MainScreenActivity extends TabbedActivity {

    private final int NEWS_TAB_INDEX = 0;
    private final int WALL_TAB_INDEX = 1;
    private final int GROUPS_TAB_INDEX = 2;
    private final int FRIENDS_TAB_INDEX = 3;

    private TabHost tabHost;
    private FriendsTabScreen friendsTabScreen;
    private WallTabScreen wallTabScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Configuramos los tabs
        this.configureTabHost();

    }

    private void configureTabHost() {
        this.tabHost = (TabHost) findViewById(R.id.tabHost);
        this.tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
               handleTabChange();
            }
        });
        this.tabHost.setup();
        this.addTabSpectToTabHost(this.tabHost, "Inicio", R.id.TabInicio);
        this.addTabSpectToTabHost(this.tabHost, "Muro", R.id.TabMuro);
        this.addTabSpectToTabHost(this.tabHost, "Grupos", R.id.TabGrupos);
        this.addTabSpectToTabHost(this.tabHost, "Amigos", R.id.TabAmigos);

        // Inicializamos los controladores de los tabs
        Button addFriendButton = (Button) findViewById(R.id.addFriendButton);
        TextView wallTitleTextView = (TextView) findViewById(R.id.wallTitleTextView);
        this.wallTabScreen = new WallTabScreen(this, addFriendButton, wallTitleTextView);
        ListView friendsListView = (ListView) findViewById(R.id.friendsListView);
        this.friendsTabScreen = new FriendsTabScreen(this, friendsListView);
    }

    /**
     * Maneja el cambio de pestañas
     */
    private void handleTabChange() {
        int currentTabIndex = this.tabHost.getCurrentTab();
        switch(currentTabIndex){
            case 0:
                // Tab de inicio
                break;
            case 1:
                // Tab de muro
                this.wallTabScreen.onFocus();
                break;
            case 2:
                // Tab de grupos
                break;
            case 3:
                // Tab de amigos
                this.friendsTabScreen.onFocus();
                break;
        };
    }

    private void addTabSpectToTabHost(TabHost tabHost, String tabLabel, int tabId) {
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabLabel);
        tabSpec.setContent(tabId);
        tabSpec.setIndicator(tabLabel);
        tabHost.addTab(tabSpec);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
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
    public TabScreen getNewsTabScreen() {
        return null;  // TODO
    }

    @Override
    public TabScreen getGroupsTabScreen() {
        return null;  // TODO
    }

    @Override
    public FriendsTabScreen getFriendsTabScreen() {
        return this.friendsTabScreen;
    }

    @Override
    public WallTabScreen getWallTabScreen() {
        return this.wallTabScreen;
    }

    @Override
    public void selectNewsTabScreen() {
        this.tabHost.setCurrentTab(NEWS_TAB_INDEX);
    }

    @Override
    public void selectGroupsTabScreen() {
        this.tabHost.setCurrentTab(GROUPS_TAB_INDEX);
    }

    @Override
    public void selectFriendsTabScreen() {
        this.tabHost.setCurrentTab(FRIENDS_TAB_INDEX);
    }

    @Override
    public void selectWallTabScreen() {
        this.tabHost.setCurrentTab(WALL_TAB_INDEX);
    }
}
