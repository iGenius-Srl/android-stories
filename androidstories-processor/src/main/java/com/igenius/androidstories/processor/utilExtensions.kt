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

fun ProcessingEnvironment.getGenericTypesForParent(element: Element, vararg parents: ClassName) =
    typeUtils
        .getSupertypes(element.asType())
        .asSequence()
        .map { it.asTypeName()}
        .filterIsInstance<ParameterizedTypeName>()
        .filter { parents.contains(it.rawType) }
        .map { it.typeArguments }
        .flatten()
        .map { elementUtils.getTypeElement(it.toString()).asType() }
        .map {
            val type = try {
                typeUtils.unboxedType(it)
                    .toString()
                    .replaceFirstChar { char -> char.uppercase() }
            } catch (e: Exception) {
                it.toString().removePrefix("java.lang.")
            }
            ClassName.bestGuess(type)
        }
        .toList()