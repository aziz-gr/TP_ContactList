package com.example.contactlist

import android.os.Parcel
import android.os.Parcelable

data class Contact(
    val id: String,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoUri: String? = null,
    var photoResId: Int? = null
) : Parcelable {

    val fullName: String
        get() = "$prenom $nom".trim()

    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        nom = parcel.readString() ?: "",
        prenom = parcel.readString() ?: "",
        telephone = parcel.readString() ?: "",
        email = parcel.readString() ?: "",
        photoUri = parcel.readString(),
        photoResId = parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nom)
        parcel.writeString(prenom)
        parcel.writeString(telephone)
        parcel.writeString(email)
        parcel.writeString(photoUri)
        parcel.writeValue(photoResId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact = Contact(parcel)
        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}