package com.igenius.androidstories.processor.specs

import com.igenius.androidstories.annotations.AsyncVariant
import com.igenius.androidstories.processor.*
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.tools.Diagnostic

fun ProcessingEnvironment.isValidStory(story: Element): Boolean =
    checkStoryType(story) && checkDataType(story)


private fun ProcessingEnvironment.checkStoryType(story: Element): Boolean {

    val isAsync = story.getAnnotation(AsyncVariant::class.java)?.let { true } ?: false

    val expectedStoryClass = when(story.kind) {
        ElementKind.CLASS -> if(isAsync) ASYNC_STORY_FRAGMENT else GENERIC_FRAGMENT
        else -> if(isAsync) ASYNC_STORY_LAYOUT else STORY_LAYOUT
    }.canonicalName

    typeUtils
        .getSupertypes(story.asType())
        .map { it.asTypeName() }
        .map { if (it is ParameterizedTypeName) it.rawType.canonicalName else it.toString() }
        .find { expectedStoryClass == it }
        ?.let { return@checkStoryType true }

    messager.printMessage(
        Diagnostic.Kind.ERROR,
        "${story.simpleName} should extend $expectedStoryClass",
        story
    )

    return false
}

private fun ProcessingEnvironment.checkDataType(story: Element): Boolean {

    val annotation = story.getAnnotation(AsyncVariant::class.java) ?: return true

    val providerElement = elementUtils.getTypeElement(annotation.dataProviderQualifiedName)

    val providerDataType = getGenericTypesForParent(
        providerElement,
        ASYNC_VARIANT_PROVIDER,
        ASYNC_CONTEXT_VARIANT_PROVIDER
    ).firstOrNull()

    val storyDataType = getGenericTypesForParent(story, ASYNC_STORY_FRAGMENT, ASYNC_STORY_LAYOUT)
        .firstOrNull()

    if (storyDataType == providerDataType) return true

    messager.printMessage(
        Diagnostic.Kind.ERROR,
        "${story.simpleName} has a different data type of ${providerElement.qualifiedName}",
        story
    )

    return false
}