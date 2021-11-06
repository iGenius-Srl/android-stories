package com.igenius.androidstories.processor

import com.igenius.androidstories.annotations.AsyncVariant
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.lang.Exception
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

val AsyncVariant.dataProviderQualifiedName: String? get() = try {
    dataProvider.qualifiedName
} catch( exception: MirroredTypeException) {
    exception.typeMirror.toString()
}

fun Types.getSupertypes(type: TypeMirror): List<TypeMirror> {
    val result = mutableListOf(type)
    directSupertypes(type).forEach {
        result.addAll(getSupertypes(it))
    }
    return result.toList().distinct()
}

fun ProcessingEnvironment.getAsyncStoryDataType (element: Element): ClassName? {
    val dataType = typeUtils
        .getSupertypes(element.asType())
        .map { it.asTypeName()}
        .filterIsInstance<ParameterizedTypeName>()
        .find {
            it.rawType == ASYNC_STORY_FRAGMENT
                    || it.rawType == ASYNC_STORY_LAYOUT
        }
        ?.typeArguments
        ?.first()
        ?.let { elementUtils.getTypeElement(it.toString()) }
        ?.asType()

    val completeName = try {
        typeUtils.unboxedType(dataType)
            .toString().replaceFirstChar { it.uppercase() }
    } catch (e: Exception) {
        dataType?.toString()?.removePrefix("java.lang.")
    }

    return completeName?.let { elementUtils.getTypeElement(it)?.asClassName() }
}