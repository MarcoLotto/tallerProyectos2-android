package com.example.marco.fiubados.commons;

import android.app.Activity;

import com.example.marco.fiubados.ContextManager;
import com.example.marco.fiubados.activity.group.GroupMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 31/05/2015.
 */
public class ActivityStackManager {

    private static ActivityStackManager instance;
    private List<Activity> activityStack;

    private ActivityStackManager(){
        this.activityStack = new ArrayList<Activity>();
    }

    public static ActivityStackManager getInstance(){
        if(instance == null){
            instance = new ActivityStackManager();
        }
        return instance;
    }

    /**
     * Resetea el stack (indicando que esta en el main screen activity)
     */
    public void resetStack(){
        this.activityStack.clear();
    }

    public void addActivityToStack(Activity activity){
        this.activityStack.add(activity);
    }

    /**
     * Vuelve a la activity d emain screen, matando todas las demas activities
     */
    public void goToMainScreenActivity(){
        for(Activity activity : this.activityStack){
            if(!activity.equals(ContextManager.getInstance().getMainScreenActivity())) {
                activity.finish();
            }
        }
    }

    public void removeActivityFromStack(Activity activity) {
        if(this.activityStack.contains(activity)){
            this.activityStack.remove(activity);
        }
    }
}
