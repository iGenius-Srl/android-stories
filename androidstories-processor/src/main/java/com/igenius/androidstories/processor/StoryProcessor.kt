package com.igenius.androidstories.processor

import com.google.auto.service.AutoService
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.processor.models.AnnotatedStory
import com.igenius.androidstories.processor.specs.*
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.igenius.androidstories.annotations.Story")
@SupportedOptions(StoryProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor::class)
class StoryProcessor : AbstractProcessor() {

    override fun process(set: Set<TypeElement?>?, roundEnvironment: RoundEnvironment?): Boolean {
        val generatedSourcesRoot = File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty())

        val elements = roundEnvironment?.getElementsAnnotatedWith(Story::class.java)

        elements?.forEach {
            if(!processingEnv.isValidStory(it)) return@process false
        }

        val generatedElement = elements?.mapNotNull {
            when (it.kind) {
                ElementKind.CLASS -> AnnotatedStory(processingEnv, it)
                ElementKind.FIELD -> createFragmentFromStory(it, generatedSourcesRoot)
                else -> null
            }
        }


        if (roundEnvironment?.processingOver() != true)
            generatedElement?.let { generateProvider(generatedSourcesRoot, it) }

        return false
    }

    private fun createFragmentFromStory(element: Element, sourceRoot: File): AnnotatedStory? {

        val isCompose = processingEnv.extends(element.asType(), STORY_COMPOSE) || processingEnv.extends(element.asType(), ASYNC_STORY_COMPOSE)
        val isLayout = processingEnv.extends(element.asType(), STORY_LAYOUT) || processingEnv.extends(element.asType(), ASYNC_STORY_LAYOUT)
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "ZOID ${element.asType()} isLay: $isLayout isCompo: $isCompose")
        return if(isCompose)
            ComposeStoryFragmentSpec(element, processingEnv).let {
                it.writeTo(sourceRoot)
                it.annotatedStory
            }
        else if(isLayout)
            LayoutStoryFragmentSpec(element, processingEnv).let {
                it.writeTo(sourceRoot)
                it.annotatedStory
            }
        else null
    }

    private fun generateProvider(
        generatedSourcesRoot: File,
        stories: List<AnnotatedStory>
    ) = StoriesProviderSpec(
        stories.map {
            it.packageName to generateFragmentStory(it, stories.indexOf(it))
        }
    ).writeTo(generatedSourcesRoot)

    private fun generateFragmentStory(story: AnnotatedStory, id: Int): AndroidStorySpec = story.run {

        val title = annotation.title.takeIf { it.isNotEmpty() }
            ?: sourceSimpleName.split("_").joinToString(" ")

        return AndroidStorySpec(
            id = id,
            completePath = title,
            description = annotation.description,
            asyncDataType = dataType,
            variants = annotation.variants,
            generateFragment = fragmentClassName,
            dataProvider = asyncAnnotation?.dataProviderQualifiedName?.let { ClassName.bestGuess (it) }
        )
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}