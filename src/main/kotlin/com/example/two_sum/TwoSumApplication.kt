package com.example.two_sum

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.google.gson.Gson
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.FileInputStream
import java.io.FileWriter
import java.io.PrintWriter

@SpringBootApplication
class TwoSumApplication

fun main(args: Array<String>) {
	//	Setting serviceAccount.json
	val path = "./serviceAccount.json"
	val adminSDK = AdminSDK()
	PrintWriter(FileWriter(path)).use {
		val gson = Gson()
		val jsonString = gson.toJson(adminSDK)
		it.write(jsonString)
	}

	//	Add the Firebase Admin SDK
	val serviceAccount = FileInputStream(path)
	val options = FirebaseOptions.builder()
		.setCredentials(GoogleCredentials.fromStream(serviceAccount))
		.build()
	FirebaseApp.initializeApp(options)

	runApplication<TwoSumApplication>(*args)
}

@RestController
@RequestMapping("/api")
class TwoSumController() {
	@GetMapping("/sum")
	fun index(): String {
		return "It's Two Sum API."
	}
	@PostMapping("/sum")
	fun twoSum(@RequestBody number: Number): IntArray {
		println(number)

		for (x in 0 until number.nums.size) {
			for (y in x + 1 until number.nums.size) {
				if (number.nums[x] + number.nums[y] == number.target) {
					return intArrayOf(x, y)
				}
			}
		}
		throw ResponseStatusException(HttpStatus.NOT_FOUND, "No solution")
	}
	//	Hasura Action
	@PostMapping("/sumFromInput")
	fun twoSumFromInput(@RequestHeader("Authorization") jwt: String , @RequestBody hasura: Hasura): Any {
		println(hasura)
		val idToken = jwt.split(' ')[1]
		FirebaseAuth.getInstance().verifyIdToken(idToken)

		for (x in 0 until hasura.input.nums.size) {
			for (y in x + 1 until hasura.input.nums.size) {
				if (hasura.input.nums[x] + hasura.input.nums[y] == hasura.input.target) {
					return object {
						val indices = intArrayOf(x, y)
					}
				}
			}
		}
		throw ResponseStatusException(HttpStatus.NOT_FOUND, "No solution")
	}
}

data class Hasura(val action: ActionName, val input: Number)
data class ActionName(val name: String)
data class Number(val nums: IntArray, val target: Int) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Number

		if (!nums.contentEquals(other.nums)) return false

		return true
	}

	override fun hashCode(): Int {
		return nums.contentHashCode()
	}
}
// Firebase
data class AdminSDK (val type: String = "service_account",
					 val project_id: String = "wanin-dev-project",
					 val private_key_id: String = "390b2779972331992a535e86f282732d82234cc3",
					 val private_key: String = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpMm4ODc/kM+26\nuP5IP5m9SlZaUHgZm8W/VZ8joeZpZEJ+4klGLJwtXGO8+pPe/0h5gThcus8hg3vF\nDTVMzw48MQicF35cUdHecaCsCse/dMPB2H0AnIMvywXP30xdtY+YT1GRot5VpWYi\ncftIo2ovigWtQ5VPgfW0kIvFf2QhP2Qda0tUhv7DPJ7ojWK7sIjB414a9u0/EYTP\n3G5RoWB29zjomdpiDlFmRe/I62K4rSKr9XmMj+jss8Fi9fz/xYFbpzjO4QyK1PHh\nXU7/++OTEI6/RljGrRosCOQOQ9jpExeemwZIjxUTfmWu16nNRWyRtG+ibwDprq/4\nG33Ll/n9AgMBAAECggEAEeYg2GHDrVuE+azaUaBQzkKP8gvzOJpPCBextx5IdrvB\ngRTFNaRAfVf5wSMtA3k0HI57/OtSQDRdopHsIo0A74Msav7dQjzxm7ufiLO3P9zF\nXLtfNfJYunSaz8vKk1tCrpUtht+Y76yIqnouYBfRgufyA8tTf067RHfKIYGBgTa8\nchwKKOA8fyvePSYXob8UC0UhEOkCWA+rW2zGOSZ6Rt10N9RAT3qO7VxNoDG/1FBI\nTyjfb4WmLUwPk8chO9jp8FeKUxDXr7KRVC9lrKQMs4EVI5LxcYU/LgJe0oSWr95k\nX2uLwgcmqsIwaZrlUGcJM3ynSAo8qRQSoPHKq+1NTQKBgQDbyoVQZ0jEwSznKean\nEqFRWsQEOqccbXBlqfcHUHPc+J4kwSLSL8pul41rd8hMcyMJGJZRfp+r5iXDxRym\npIG4xhVo1OXPpIrQyt5CyFDhSSdY9FKRj9eJRF3JeiPczysRAvRrgS+nJqJs63ar\nSVwdpiL0bS14L5PfGRfTdnZQWwKBgQDFEiazEvc00tIvkLRdzamk1dEYS9WMUhcn\nrJfWE8yIgK8lwcGwXLJZonnuA7EZXvn/AqgPoe4bnFAlbXOI+zfBAVXlS/LKj27U\n4lTifbAOFY88yMVKBZaCa8Pi+VIfA+Vuodtv/6ZMqMZwL46sWLJEmU4aG2OzXRBg\nghiKxa3uhwKBgErKbnT+wG9bgMkko/1ERKqnZT4/KzBsOSwPGyVdBiAyZbYCFrRJ\nWOwrHA9u0jTZlRl6SWRv3yuihEVK5MX8Arx6gzyJ/a2bwzTrY5h8C7Gtr/sAILyx\notlt7zApJNz1jiUayUtvyWRqrCi+M0J0EylvqENeh3N3VS08n7PwgrAzAoGAXd+B\nk7erDfyGpqW1ZSF8d8pqKbql5IuOABYU1aLkwPkbdU7QZqQhSNU+Y4Rarg0x5Klt\nSNUHyRsyikGS0WNpcYSjV/BPnPGqB7owWRsp6+uVKY0xfUsgIMwYqVu+JIwElCiB\nkN4kZ0vWCsZ8ndg6QrcMHx+A5IWmMXKHIkN70K0CgYEApBdMRawkNiBdt29Aj/99\nKOEJfyHQJdPNM6/WyxCX8jVa/Eyy+h4TWMZ005c0FrGaRURNrGxFu4vntVPXYG5l\nARglL8BBD2mXY9Q3Fc896Xh0kS9W+VcrPY6ZubF+rZDKF6CL/vdzYXFpiy/pP2qI\nZr0Kt1lebAL9eMlXrPX4O8Y=\n-----END PRIVATE KEY-----\n",
					 val client_email: String = "firebase-adminsdk-97a3q@wanin-dev-project.iam.gserviceaccount.com",
					 val client_id: String = "117382146471169662373",
					 val auth_uri: String = "https://accounts.google.com/o/oauth2/auth",
					 val token_uri: String = "https://oauth2.googleapis.com/token",
					 val auth_provider_x509_cert_url: String = "https://www.googleapis.com/oauth2/v1/certs",
					 val client_x509_cert_url: String = "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-97a3q%40wanin-dev-project.iam.gserviceaccount.com")
//data class AdminSDK (val type: String = System.getenv("type"),
//					 val project_id: String = System.getenv("project_id"),
//					 val private_key_id: String = System.getenv("private_key_id"),
//					 val private_key: String = System.getenv("private_key"),
//					 val client_email: String = System.getenv("client_email"),
//					 val client_id: String = System.getenv("client_id"),
//					 val auth_uri: String = System.getenv("auth_uri"),
//					 val token_uri: String = System.getenv("token_uri"),
//					 val auth_provider_x509_cert_url: String = System.getenv("auth_provider_x509_cert_url"),
//					 val client_x509_cert_url: String = System.getenv("client_x509_cert_url"))