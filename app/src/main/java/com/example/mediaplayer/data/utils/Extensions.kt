package com.example.mediaplayer.data.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mediaplayer.data.db.entites.VideoEntity
import com.example.mediaplayer.data.models.video.VideoInfo
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun View.doAsync(
    backgroundWork: suspend (view:View) -> Unit,
) {
    val job = CoroutineScope(Dispatchers.Main)
    job.launch {
        if (isActive) {
            backgroundWork(this@doAsync)
        }
    }

}

fun Long.convertDuration(): String {
    val initialSeconds = TimeUnit.MILLISECONDS.toSeconds(this).toInt()
    val hours = (initialSeconds / 3600)
    val minutes = abs(((hours * 3600 - initialSeconds) / 60))
    val seconds = abs((hours * 3600 + minutes * 60 - initialSeconds))
    val formatMinutes = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }
    val formatHours = if (hours < 10) {
        "0$hours"
    } else {
        hours.toString()
    }
    val formatSeconds = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$formatHours:$formatMinutes:$formatSeconds"
}


fun Int.compareNumber(number: Int?): Int {
    if (number == null) {
        return 0
    }
    return if (this > number) {
        number
    } else {
        this
    }
}

fun List<VideoEntity>.ifContains(contentUri: Uri): Boolean {
    var find = false
    this.forEach {
        if (Uri.parse(it.contentUri).path == contentUri.path) {
            find = true
        }
    }
    return find
}

fun ArrayList<VideoInfo>.updateWithViewedTime(list: List<VideoEntity>): ArrayList<VideoInfo> {
    val updatedList = this
    updatedList.forEach {
        if (list.ifContains(it.contentUri)) {
            it.viewedTime =
                list.find { entity -> Uri.parse(entity.contentUri).path == it.contentUri.path }?.viewedTime
        }
    }
    return updatedList
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}