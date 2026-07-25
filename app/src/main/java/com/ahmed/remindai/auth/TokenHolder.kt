package com.ahmed.remindai.auth

object TokenHolder {
    @Volatile var token: String? = null
}
