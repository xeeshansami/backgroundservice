package com.paxees.sms.db

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import java.util.*

open class OSmsModel() :RealmObject(){
    var number: String? = null
    var msg: String? = null
    var securecode=""
    var dateTime=""
}