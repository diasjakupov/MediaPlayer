package com.example.mediaplayer.data.models

data class SubTitleInfo(
    override val language: String?,
    override val format: String
) : CustomMediaItemFormat
