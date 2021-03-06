package com.igenius.androidstories.processor.specs

import com.igenius.androidstories.processor.ASYNC_STORY_FRAGMENT
import com.igenius.androidstories.processor.STORY_FRAGMENT
import com.igenius.androidstories.processor.models.AnnotatedStory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


class StoryFragmentSpec(
    layoutStoryElement: Element,
    processingEnv: ProcessingEnvironment
) {
    private val name = "${layoutStoryElement.simpleName.toString().replaceFirstChar { it.uppercase() }}StoryFragment"

    val annotatedStory = AnnotatedStory(processingEnv, layoutStoryElement, name)

    private val superType = annotatedStory.dataType?.let {
        ASYNC_STORY_FRAGMENT.parameterizedBy(it)
    } ?: STORY_FRAGMENT

    private val properties: List<PropertySpec> = listOfNotNull(
        annotatedStory.dataType?.let {
            PropertySpec.builder("preventUiLoader", Boolean::class.asClassName(), KModifier.OVERRIDE)
                .initializer("%L.preventUiLoader", layoutStoryElement.simpleName)
                .build()
        }
    )

    private val variantFunSpec = annotatedStory.dataType?.let {
        FunSpec.builder("onVariantLoaded")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("variant", String::class.asClassName()))
            .addParameter(ParameterSpec("data", annotatedStory.dataType))
            .addStatement(
                "view?.let { %L.onVariantLoaded.invoke(it, variant, data) }",
                layoutStoryElement.simpleName
            )
    } ?: FunSpec.builder("onVariantSelected")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(ParameterSpec("variant", String::class.asClassName()))
            .addStatement(
                "view?.let { %L.onVariantSelected.invoke(it, variant) }",
                layoutStoryElement.simpleName
            )

    private val typeSpec = TypeSpec
        .classBuilder(name)
        .superclass(superType)
        .addProperties(properties)
        .addFunction(
            FunSpec.builder("onCreateView")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter(INFLATER_PARAM)
                .addParameter(CONTAINER_PARAM)
                .addParameter(SAVED_INSTANCE_STATE_PARAM)
                .returns(VIEW_TYPE)
                .addStatement(
                    "return inflater.inflate(%L.layoutId, container, false)",
                    layoutStoryElement.simpleName
                ).build()
        )
        .addFunction(variantFunSpec.build()).build()

    private val fileSpec = FileSpec.builder(annotatedStory.packageName, name)
        .addComment("Autogenerated StoryFragment to expose ${annotatedStory.packageName}.${layoutStoryElement.simpleName}")
        .addType(typeSpec)

    fun writeTo(file: File) = fileSpec.build().writeTo(file)

    companion object {
        private val INFLATER_PARAM = ParameterSpec("inflater", ClassName("android.view", "LayoutInflater"))
        private val CONTAINER_PARAM = ParameterSpec("container", ClassName("android.view", "ViewGroup").copy(nullable = true))
        private val SAVED_INSTANCE_STATE_PARAM = ParameterSpec("savedInstanceState", ClassName("android.os", "Bundle").copy(nullable = true))
        private val VIEW_TYPE = ClassName("android.view", "View")
    }
}