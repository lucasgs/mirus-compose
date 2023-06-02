package com.dendron.mirus.domain

interface NetworkChecker {
    fun isOnline(): Boolean
}