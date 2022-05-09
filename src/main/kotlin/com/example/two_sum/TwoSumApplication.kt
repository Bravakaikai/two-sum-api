package com.example.two_sum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@SpringBootApplication
class TwoSumApplication

fun main(args: Array<String>) {
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
	fun twoSumFromInput(@RequestBody hasura: Hasura): Any {
		println(hasura)

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
