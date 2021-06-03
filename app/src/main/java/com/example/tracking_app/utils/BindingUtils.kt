package com.example.tracking_app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

@BindingAdapter("image")
fun loadImage(view: ImageView, url: String) {
    val imageUrl = url.replace("localhost", "192.168.56.1")
    Glide.with(view)
        .load(imageUrl)
        .into(view)
}

fun ByteArray.toBitmap():Bitmap{
    return BitmapFactory.decodeByteArray(this,0,size)
}

// extension function to convert bitmap to byte array
fun Bitmap.toByteArray():ByteArray{
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG,10,this)
        return toByteArray()
    }
}