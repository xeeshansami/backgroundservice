package com.paxees.sms.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paxees.sms.SmsModel;

import java.lang.reflect.Type;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController<T> {
    private static RealmController instance;
    private final Realm realm;

    public static void resetObject() {
        instance = null;
    }

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }


    public static RealmController getInstance() {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public Realm getRealm() {
        Realm realm = Realm.getDefaultInstance();
        return realm;
    }

    public void refresh() {
        Realm realm = Realm.getDefaultInstance();
        realm.refresh();
    }

    public void clearAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void saveSms(SmsModel mObj,String id) {
        String json = new Gson().toJson(mObj);
        Type listType = new TypeToken<OSmsModel>() {
        }.getType();
        OSmsModel dta = new Gson().fromJson(json, listType);
        RealmResults<OSmsModel> alldata = realm.where(OSmsModel.class).findAll();
        if(alldata.size()==0){
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(dta);
            realm.commitTransaction();
        }
        for (OSmsModel x : alldata) {
            if (!x.getId().equals(id)) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.insertOrUpdate(dta);
                realm.commitTransaction();
            }
        }
    }

    public RealmResults<OSmsModel> getSms() {
        return realm.where(OSmsModel.class).findAll();
    }

//    public void deleteSMS(String id) {
//        Realm realm = Realm.getDefaultInstance();
//        OSmsModel object = realm.where(OSmsModel.class).equalTo("id", id, Case.SENSITIVE).findFirst();
//        if (object != null) {
//            realm.beginTransaction();
//            object.deleteFromRealm();
//            realm.commitTransaction();
//        }
//    }

}

