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
		val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken)

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
data class AdminSDK (val type: String = System.getenv("type"),
					 val project_id: String = System.getenv("project_id"),
					 val private_key_id: String = System.getenv("private_key_id"),
					 val private_key: String = System.getenv("private_key"),
					 val client_email: String = System.getenv("client_email"),
					 val client_id: String = System.getenv("client_id"),
					 val auth_uri: String = System.getenv("auth_uri"),
					 val token_uri: String = System.getenv("token_uri"),
					 val auth_provider_x509_cert_url: String = System.getenv("auth_provider_x509_cert_url"),
					 val client_x509_cert_url: String = System.getenv("client_x509_cert_url"))