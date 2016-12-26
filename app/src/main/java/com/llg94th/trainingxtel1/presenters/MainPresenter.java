package com.llg94th.trainingxtel1.presenters;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.llg94th.trainingxtel1.models.AccountKitResult;
import com.llg94th.trainingxtel1.views.inf.BasicActivityInterface;



public class MainPresenter {
    private BasicActivityInterface basicActivityInterface;



    public MainPresenter(BasicActivityInterface basicActivityInterface) {
        this.basicActivityInterface = basicActivityInterface;
    }

    public void sendAccountResultToSever(AccountKitResult accountKitResult,boolean isByPhone){
        String link;
        if (isByPhone) {
            link = "http://124.158.5.112:9180/nipum/v1.0/m/user/accountkit/login";
        } else {
            link = "http://124.158.5.112:9180/nipum/v1.0/m/user/fb/login";
        }

        Ion.with(basicActivityInterface.getActivity())
                .load(link)
                .setJsonObjectBody(accountKitResult.toJsonObject())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            basicActivityInterface.showToast(result.toString());
                        } else {
                            basicActivityInterface.showToast("Errror on sendAccountResultToSever: "+e.getMessage());
                        }
                    }
                });

    }
}
