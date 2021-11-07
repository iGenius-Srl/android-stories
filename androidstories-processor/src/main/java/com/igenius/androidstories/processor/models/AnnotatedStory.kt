package com.igenius.androidstories.processor.models

import com.igenius.androidstories.annotations.AsyncVariant
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.processor.*
import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

data class AnnotatedStory(
    val annotation: Story,
    val asyncAnnotation: AsyncVariant?,
    val packageName: String,
    val simpleName: String,
    val sourceSimpleName: String,
    val dataType: ClassName?
) {
    val fragmentClassName: ClassName = ClassName(packageName, simpleName)

    constructor(
        processingEnv: ProcessingEnvironment,
        element: Element,
        simpleName: String? = null
    ) : this(
        annotation = element.getAnnotation(Story::class.java),
        asyncAnnotation = element.getAnnotation(AsyncVariant::class.java),
        packageName = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString(),
        simpleName = simpleName ?: element.simpleName.toString(),
        sourceSimpleName = element.simpleName.toString(),
        dataType = processingEnv.getGenericTypesForParent(element, ASYNC_STORY_FRAGMENT, ASYNC_STORY_LAYOUT).firstOrNull()
    )
}