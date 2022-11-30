package com.example.kotlinreflection

class Reflect {
    val publicVal: Int = 2
    var publicVar: String = "publicVar"

    private val privateVal: Int = 3
    private var privateVar: String = "privateVar"

    private val simple = object : SimpleInterface {
        override fun simpleFun() = "Reflection Interface"
    }

    fun publicFunction(value: Int) {
        println(value)
    }

    private fun privateFunction(value: String) {
        println(value)
    }
}