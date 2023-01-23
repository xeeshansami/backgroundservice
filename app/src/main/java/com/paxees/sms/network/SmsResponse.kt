package com.paxees.sms.network;

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList
class SmsResponse() : Parcelable {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("Information")
    var Information: String? = null

    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
        msg = parcel.readString()
        Information = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(msg)
        parcel.writeString(Information)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SmsResponse> {
        override fun createFromParcel(parcel: Parcel): SmsResponse {
            return SmsResponse(parcel)
        }

        override fun newArray(size: Int): Array<SmsResponse?> {
            return arrayOfNulls(size)
        }
    }


}