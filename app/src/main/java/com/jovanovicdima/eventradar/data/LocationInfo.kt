package com.jovanovicdima.eventradar.data

import android.location.Location

object LocationInfo {
    var location: Location? = null

    private var list: MutableList<(Location?) -> Unit> = mutableListOf()

    fun Alert() {
        for(item in list) {
            item(location)
        }
    }

    fun Subscribe(callback: (Location?) -> Unit) {
        if(!list.contains(callback)) {
            list.add(callback)
        }
    }

    fun UnsubscribeAll() {
        for(item in list) {
        item(null)
    }
        list = mutableListOf()
    }
}