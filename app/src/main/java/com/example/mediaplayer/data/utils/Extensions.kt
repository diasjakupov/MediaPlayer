package com.example.mediaplayer.data.utils

import android.net.Uri
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.mediaplayer.data.db.entites.VideoEntity

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun Int.compareNumber(number:Int):Int{
    return if(this>number){
        number
    }else{
        this
    }
}

fun List<VideoEntity>.ifContains(contentUri: Uri): Boolean{
    var find=false
    this.forEach {
        if(Uri.parse(it.contentUri).path==contentUri.path){
            find=true
        }
    }
    return find
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