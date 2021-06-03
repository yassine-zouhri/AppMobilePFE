package com.example.tracking_app.utils

import android.util.Base64
import com.squareup.moshi.Moshi
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object JWTUtils {

    @Throws(UnsupportedEncodingException::class)
    fun getPayloadDecoded(jwtEncoded: String): String {
        
        val jwtArray = jwtEncoded.split("\\.".toRegex()).toTypedArray()
        val decodedBytes: ByteArray = Base64.decode(jwtArray[1], Base64.URL_SAFE)
        return String(decodedBytes, Charset.forName("UTF-8"))
    }
}