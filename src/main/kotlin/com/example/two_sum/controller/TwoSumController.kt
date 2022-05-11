package com.example.two_sum.controller

import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

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

    @PostMapping("/sumFromInput")
    fun twoSumFromInput(@RequestHeader("Authorization") jwt: String, @RequestBody hasura: Hasura): Any {
        println(hasura)

        val idToken = jwt.split(' ')[1]
        FirebaseAuth.getInstance().verifyIdToken(idToken)

        for (x in 0 until hasura.input.nums.size) {
            for (y in x + 1 until hasura.input.nums.size) {
                if (hasura.input.nums[x] + hasura.input.nums[y] == hasura.input.target) {
                    return mapOf("indices" to intArrayOf(x, y))
                }
            }
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "No solution")
    }
}

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

// Hasura
data class Hasura(val action: ActionName, val input: Number)
data class ActionName(val name: String)
