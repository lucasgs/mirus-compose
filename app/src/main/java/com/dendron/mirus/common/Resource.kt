package com.dendron.mirus.common

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String?) : Resource<T>()
    class Loading<T>() : Resource<T>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}