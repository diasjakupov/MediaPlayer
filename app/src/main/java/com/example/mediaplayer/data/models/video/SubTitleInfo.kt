package com.example.mediaplayer.data.models.video

data class SubTitleInfo(
    override val language: String?,
    override val format: String
) : CustomMediaItemFormat
