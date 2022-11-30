package com.example.kotlinreflection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.* // 확장함수 선언을 Import
import kotlin.reflect.jvm.isAccessible

/** Reflection
- 실행 시점에 동적으로 객체의 프로퍼티나 메서드에 접근할 수 있는 방법
- JSON 직렬화 라이브러리가 대표적인 예

코플린에서는 아래 두가지 리플렉션 API 가 있다.
1. 자바 표준 리플렉션 : java.lang.reflect
- 코틀린 클래스도 일반 자바 바이트 코드로 컴파일 되기 때문에 자바 리플렉션 API도 코틀린 클래스를 지원
2. 코틀린 제공 리플렉션 : kotlin.reflect 패키지
- 이 API는 .jar 파일에 담겨 제공되며, 자동으로 프로젝트에 dependencies 되지 않는다.
- implementation "org.jetbrains.kotlin:kotlin-reflect"

[Reflection API]

- 클래스내 모든 멤버는 KCallable 인스턴스의 컬렉션이다.
- KCallable 은 함수와 프로퍼티를 모두 아우르는 공통 상위 인터페이스
: call 메서드를 통해 함수나 게터를 호출할 수 있다.

 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reflection()
    }

    private fun reflection() {
        val reflect: Reflect = Reflect()
        showClassInfo(reflect::class)

        showProperty(reflect::class)

        showFunction(reflect::class)

        callInterfaceFun(reflect::class)
    }

    private fun showClassInfo(kClass: KClass<*>) {
        println("isAbstract : ${kClass.isAbstract}") // 클래스가 abstract로 선언되었는지 판단
        println("isCompanion : ${kClass.isCompanion}") // 클래스가 companion로 선언되었는지 판단
        println("isData : ${kClass.isData}") // 클래스가 data로 선언되었는지 판단
        println("isFinal : ${kClass.isFinal}") // 클래스가 final로 선언되었는지 판단
        println("isInner : ${kClass.isInner}") // 클래스가 inner로 선언되었는지 판단
        println("isOpen : ${kClass.isOpen}") // 클래스가 open으로 선언되었는지 판단
        println("isSealed : ${kClass.isSealed}") // 클래스가 sealed로 선언되었는지 판단
    }

    private fun showProperty(kClass: KClass<*>) {
        // val property = kClass.declaredMemberProperties; // 확장 프로퍼티를 제외한 클래스에 선언된 모든 프로퍼티 반환
        val property = kClass.memberProperties;// 확장 프로퍼티를 제외한 클래스와 상위 클래스에 선언된 모든 프로퍼티 반환
        // val property = kClass.declaredMemberExtensionProperties; // 클래스에 선언된 확장 프로퍼티 반환

        property.forEach {
            println("${it.name} : ${it.returnType}")
        }

        // Instance
        val primaryConstructor = kClass.primaryConstructor
        val instance = primaryConstructor?.call()

        // Public Property
        val pubVar = Reflect::publicVar
        val pubVal = Reflect::publicVal

        println("publicVar : ${pubVar.getter.call(instance)}")
        println("publicVal : ${pubVal.getter.call(instance)}")

        pubVar.setter.call(instance, "Set publicVar")
        println("publicVar : ${pubVar.getter.call(instance)}")

        // Private Property
        val priVar = property.filterIsInstance<KMutableProperty<*>>().find { it.name == "privateVar" }
        val priVal = property.filterIsInstance<KProperty<*>>().find { it.name == "privateVal" }

        priVar?.isAccessible = true
        priVal?.isAccessible = true

        println("privateVar : ${priVar?.getter?.call(instance)}")
        println("privateVal : ${priVal?.getter?.call(instance)}")

        priVar?.setter?.call(instance, "Set privateVar")
        println("privateVar : ${priVar?.getter?.call(instance)}")
    }

    private fun showFunction(kClass: KClass<*>) {
        // val function = kClass.declaredMemberFunctions; // 확장 함수를 제외한 클래스에 선언된 모든 함수 반환
        val function = kClass.memberFunctions; // 확장 함수를 제외한 클래스와 상위 클래스에 선언된 모든 함수 반환
        // val function = kClass.declaredMemberExtensionFunctions; // 클래스에 선언된 확장 함수 반환

        function.forEach {
            println("${it.name} : ${it.returnType}")
        }

        // Instance
        val primaryConstructor = kClass.primaryConstructor
        val instance = primaryConstructor?.call()

        // Function Call
        val publicFunction = Reflect::publicFunction
        val privateFunction = function.find { it.name == "privateFunction" }

        publicFunction.call(instance, 4444)

        privateFunction?.isAccessible = true
        privateFunction?.call(instance, "privateFunction call")
    }

    private fun callInterfaceFun(kClass: KClass<*>) {
        // Instance
        val primaryConstructor = kClass.primaryConstructor
        val instance = primaryConstructor?.call()

        // Get Interface property
        val property = kClass.memberProperties.find { it.name == "simple" }

        property?.isAccessible = true
        val simpleInterface: SimpleInterface = property?.getter?.call(instance) as SimpleInterface
        println(simpleInterface.simpleFun())
    }
}