package com.example.bookapp.screens.login

data class LoadingState(val status : Status, val messege: String? =null){

    companion object {
        val IDLE = LoadingState(Status.IDLE)
        val RUNNING = LoadingState(Status.RUNNING)
        val FAILED = LoadingState(Status.FAILED)
        val LOADING = LoadingState(Status.LOADING)
    }

    enum class Status{
        RUNNING,
        FAILED,
        LOADING,
        IDLE
    }

}
