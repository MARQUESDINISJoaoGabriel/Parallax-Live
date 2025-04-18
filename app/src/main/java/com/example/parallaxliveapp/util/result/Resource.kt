package com.example.parallaxliveapp.util.result

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 * @property data The data of the resource
 * @property status Status of the current resource
 * @property message Message in case of error
 */
data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data)
        }
    }
}