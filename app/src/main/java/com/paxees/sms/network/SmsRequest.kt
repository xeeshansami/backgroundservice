package com.hbl.hblaccountopeningapp.network.models.response.base;

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


class SmsRequest() : Parcelable {
    @SerializedName("securecode")
    var securecode: String? = null

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("sms")
    var sms = ""

    @SerializedName("datetime")
    var datetime = ""

    constructor(parcel: Parcel) : this() {
        securecode = parcel.readString()
        phone = parcel.readString()
        sms = parcel.readString().toString()
        datetime = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(securecode)
        parcel.writeString(phone)
        parcel.writeString(sms)
        parcel.writeString(datetime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SmsRequest> {
        override fun createFromParcel(parcel: Parcel): SmsRequest {
            return SmsRequest(parcel)
        }

        override fun newArray(size: Int): Array<SmsRequest?> {
            return arrayOfNulls(size)
        }
    }

}