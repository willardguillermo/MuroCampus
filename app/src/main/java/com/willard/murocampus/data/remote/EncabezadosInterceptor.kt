package com.willard.murocampus.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class EncabezadosInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestConEncabezado = chain.request()
            .newBuilder()
            .header("Accept", "application/json")
            .build()
        return chain.proceed(requestConEncabezado)
    }
}
