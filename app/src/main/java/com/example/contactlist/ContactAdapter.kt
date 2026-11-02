package com.example.contactlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(
    context: Context,
    private val contacts: List<Contact>
) : ArrayAdapter<Contact>(context, 0, contacts) {

    private class ViewHolder(
        val tvName: TextView,
        val tvPhone: TextView,
        val ivPhoto: ImageView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(
                tvName = view.findViewById(R.id.tvItemName),
                tvPhone = view.findViewById(R.id.tvItemPhone),
                ivPhoto = view.findViewById(R.id.ivItemPhoto)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val contact = getItem(position)
        if (contact != null) {
            holder.tvName.text = contact.fullName
            holder.tvPhone.text = contact.telephone

            ImageUtils.loadContactPhoto(
                context = context,
                photoUriStr = contact.photoUri,
                photoResId = contact.photoResId,
                imageView = holder.ivPhoto
            )
        }

        return view
    }
}