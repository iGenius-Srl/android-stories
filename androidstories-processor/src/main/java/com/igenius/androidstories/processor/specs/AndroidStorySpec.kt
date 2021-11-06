package com.igenius.androidstories.processor.specs

import com.igenius.androidstories.processor.ANDROID_ASYNC_FRAGMENT_STORY_MODEL
import com.igenius.androidstories.processor.ANDROID_FRAGMENT_STORY_MODEL
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class AndroidStorySpec(
    id: Int,
    completePath: String,
    description: String?,
    asyncDataType: ClassName?,
    variants: Array<String>,
    generateFragment: ClassName,
    dataProvider: ClassName?
) {

    val name = "story_$id"

    private val type = asyncDataType?.let {
        ANDROID_ASYNC_FRAGMENT_STORY_MODEL.parameterizedBy(it)
    } ?: ANDROID_FRAGMENT_STORY_MODEL

    private val variantsType = Array::class.asClassName().parameterizedBy(String::class.asClassName())

    private val properties = mutableListOf(
        // id
        PropertySpec.builder("id", Int::class, KModifier.OVERRIDE)
            .initializer("%L", id),

        // completePath
        PropertySpec.builder("completePath", String::class, KModifier.OVERRIDE)
            .initializer("%S", completePath),

        // description
        PropertySpec.builder("description", String::class.asClassName().copy(nullable = true), KModifier.OVERRIDE)
            .initializer(description?.let {"%S" } ?: "%L", description),

        // variants
        PropertySpec.builder("variants", variantsType, KModifier.OVERRIDE)
            .initializer("arrayOf(${variants.joinToString(", ") { "%S" }})", *variants),

        // dataProvider
        dataProvider?.let {
            PropertySpec.builder("dataProvider", it, KModifier.OVERRIDE)
                .getter(FunSpec.getterBuilder().addStatement("return %L()", it.simpleName).build())
        }
    )

    private val functions = listOf(
        // generateFragment
        FunSpec
            .builder("generateFragment")
            .addModifiers(KModifier.OVERRIDE)
            .returns(generateFragment)
            .addStatement("return %L()", generateFragment.simpleName)
            .build()
    )

    private val typeSpec = TypeSpec
        .objectBuilder(name)
        .addSuperinterface(type)
        .addProperties(properties.mapNotNull { it?.build() })
        .addFunctions(functions)

    fun build() = typeSpec.build()
}