package com.example.rojo.guardiannews;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

public class GuardianLoader extends AsyncTaskLoader<List<Guardian>> {

    private static final String LOG_TAG = GuardianLoader.class.getName();

    private String mUrl;

    public GuardianLoader(Context context, String url ){
        super(context);
        mUrl=url;
    }
    @Override
    protected void onStartLoading(){forceLoad();}

    @Override
    public List<Guardian>loadInBackground(){
        if (mUrl == null){
            return null;
        }
        List<Guardian> guardians =QueryUtils.fetchGuardianData(mUrl);
        return guardians;
    }
}
