package dev.rohith.health

interface HealthSDKError

class SDKNotAvailableException : Throwable("Sdk not present on device"), HealthSDKError

class SDKUpdateRequiredException : Throwable("Update sdk to latest version"), HealthSDKError