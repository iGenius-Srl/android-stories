package com.igenius.androidstories.processor.specs

import com.igenius.androidstories.processor.ANDROID_FRAGMENT_STORY_MODEL
import com.igenius.androidstories.processor.STORIES_PROVIDER_INTERFACE
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import java.io.File

class StoriesProviderSpec(
    storiesByPackage: List<Pair<String, AndroidStorySpec>>
) {

    private val packageName = getRootPackage(storiesByPackage.map { it.first })

    private val typeSpec = TypeSpec
        .classBuilder(STORIES_PROVIDER_CLASS_NAME)
        .addSuperinterface(STORIES_PROVIDER_INTERFACE)
        .addTypes(storiesByPackage.map { it.second.build() })
        .addProperty(
            PropertySpec
                .builder(
                    "stories",
                    List::class.asClassName().plusParameter(ANDROID_FRAGMENT_STORY_MODEL)
                )
                .addModifiers(KModifier.OVERRIDE)
                .getter(
                    FunSpec
                        .getterBuilder()
                        .addStatement(
                            "return listOf(${Array(storiesByPackage.size) { "%L" }.joinToString(", ")})",
                            *storiesByPackage.map { it.second.name }.toTypedArray()
                        ).build()
                ).build()
        )

    private val fileSpec = FileSpec
        .builder(packageName, STORIES_PROVIDER_CLASS_NAME)
        .addType(typeSpec.build())

    fun writeTo(file: File) = fileSpec.build().writeTo(file)

    companion object {
        private const val STORIES_PROVIDER_CLASS_NAME = "AppStoriesProvider"
    }
}

private fun getRootPackage(packages: List<String>): String {
    var result: String? = null
    packages.forEach { packageName ->
        result?.let {
            result = it.commonPrefixWith(packageName)
        } ?: run {
            result = packageName
        }
    }

    return checkNotNull(result) { "no root package name" }
}

