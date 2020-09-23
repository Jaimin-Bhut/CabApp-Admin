package com.jb.dev.cabapp_admin.fragment.Cab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CabViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CabViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}