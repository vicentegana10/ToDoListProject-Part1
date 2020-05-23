package com.abs.clase11.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.todolistproject.R

@GlideModule
class GlideAppModule: AppGlideModule()

fun ImageView.loadPhoto(uri: String?) {
    Glide.with(this.context)
        .load(uri)
        .override(500)
        .circleCrop()
        .into(this)
}

