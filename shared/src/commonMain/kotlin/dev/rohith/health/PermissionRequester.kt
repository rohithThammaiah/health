package dev.rohith.health

interface PermissionRequester {
    fun register()
    fun requestPermission()
}