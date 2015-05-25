package com.example.marco.fiubados;

import android.support.v7.app.AppCompatActivity;

import com.example.marco.fiubados.TabScreens.FriendsTabScreen;
import com.example.marco.fiubados.TabScreens.CallbackScreen;
import com.example.marco.fiubados.TabScreens.WallTabScreen;

/**
 * Created by Marco on 08/04/2015.
 * Aquellas activities que funcionan como contenedor de tabs
 */
public abstract class TabbedActivity extends AppCompatActivity {

    /**
     * Accessors para los tabs
     */
    public abstract CallbackScreen getGroupsTabScreen();
    public abstract FriendsTabScreen getFriendsTabScreen();
    public abstract WallTabScreen getWallTabScreen();

    /**
     * Muestra el tab correspondiente
     */
    public abstract void selectGroupsTabScreen();
    public abstract void selectFriendsTabScreen();
    public abstract void selectWallTabScreen();
}
