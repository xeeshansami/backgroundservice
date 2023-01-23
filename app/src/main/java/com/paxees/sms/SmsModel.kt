package com.paxees.sms

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class SmsModel() : Parcelable {
    var number: String? = null
    var msg: String? = null
    var securecode=""
    var dateTime=""
    constructor(parcel: Parcel) : this() {
        number = parcel.readString()
        msg = parcel.readString()
        securecode = parcel.readString().toString()
        dateTime = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(securecode)
        parcel.writeString(dateTime)
        parcel.writeString(number)
        parcel.writeString(msg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SmsModel> {
        override fun createFromParcel(parcel: Parcel): SmsModel {
            return SmsModel(parcel)
        }

        override fun newArray(size: Int): Array<SmsModel?> {
            return arrayOfNulls(size)
        }
    }
}