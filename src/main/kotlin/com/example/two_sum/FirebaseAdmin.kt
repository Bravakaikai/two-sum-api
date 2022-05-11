package com.example.two_sum

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.gson.Gson
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.FileWriter
import java.io.PrintWriter

@Component
class FirebaseAdmin(val adminSDK: AdminSDK) {
    val path = "./serviceAccount.json"
    val serviceAccount = FileInputStream(path)

    init {
        //	Setting serviceAccount.json
        PrintWriter(FileWriter(path)).use {
            val gson = Gson()
            val jsonString = gson.toJson(adminSDK)
            it.write(jsonString)
        }

        //  Add the Firebase Admin SDK
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        FirebaseApp.initializeApp(options)
    }
}

@Component
@ConfigurationProperties(prefix = "firebase.admin.sdk")
data class AdminSDK(
    var type: String = "",
    var project_id: String = "",
    var private_key_id: String = "",
    var private_key: String = "",
    var client_email: String = "",
    var client_id: String = "",
    var auth_uri: String = "",
    var token_uri: String = "",
    var auth_provider_x509_cert_url: String = "",
    var client_x509_cert_url: String = "",
)