package com.igenius.androidstories.processor

import com.google.auto.service.AutoService
import com.igenius.androidstories.annotations.Story
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("com.igenius.androidstories.annotations.Story")
@SupportedOptions(StoryProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor::class)
class StoryProcessor : AbstractProcessor() {

    override fun process(set: Set<TypeElement?>?, roundEnvironment: RoundEnvironment?): Boolean {
        val generatedSourcesRoot =
            File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME].orEmpty())
        val elements = roundEnvironment?.getElementsAnnotatedWith(Story::class.java)

        val generatedFragments = mutableListOf<ClassName>()
        elements?.filterNotNull()?.forEach { element ->
            if (element.kind == ElementKind.METHOD) {
                createFragment(element).let {
                    generatedFragments.add(it.first)
                    it.second.writeTo(generatedSourcesRoot)
                }
            }
        }
        if (roundEnvironment?.processingOver() != true)
            generateProvider(generatedSourcesRoot, generatedFragments)

        return false
    }


    private fun createFragment(element: Element): Pair<ClassName, FileSpec> {
        val packageName =
            processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()

        val fragmentClassName =
            "${element.simpleName.toString().replaceFirstChar { it.uppercase() }}StoryFragment"

        val className = ClassName(packageName, fragmentClassName)
        val file = FileSpec.builder(packageName, fragmentClassName)
            .addComment("Autogenerated Fragment to expose $packageName.${element.simpleName}")
            .addType(
                TypeSpec
                    .classBuilder(fragmentClassName)
                    .superclass(ClassName("androidx.fragment.app", "Fragment"))
                    .addFunction(
                        FunSpec.builder("onCreateView")
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(
                                ParameterSpec(
                                    "inflater",
                                    ClassName("android.view", "LayoutInflater")
                                )
                            )
                            .addParameter(
                                ParameterSpec(
                                    "container",
                                    ClassName("android.view", "ViewGroup")
                                        .copy(nullable = true)
                                )
                            )
                            .addParameter(
                                ParameterSpec(
                                    "savedInstanceState",
                                    ClassName("android.os", "Bundle")
                                        .copy(nullable = true)
                                )
                            )
                            .returns(ClassName("android.view", "View"))
                            .addStatement("return $packageName.${element.simpleName}(inflater, container)")
                            .build()
                    )
                    .build()
            )
            .build()

        return className to file
    }

    private fun generateProvider(generatedSourcesRoot: File, classNames: List<ClassName>) {
        var packageName: String? = null
        classNames.forEach { className ->
            packageName?.let {
                packageName = it.commonPrefixWith(className.packageName)
            } ?: run {
                packageName = className.packageName
            }
        }

        val finalPackageName = checkNotNull(packageName) {
            "no common package name"
        }

        val providerName = "AppStoriesProvider"
        val file = FileSpec
            .builder(finalPackageName, providerName)
            .addType(
                TypeSpec.classBuilder(providerName)
                    .addSuperinterface(
                        ClassName(
                            "com.igenius.androidstories.app",
                            "StoriesProvider"
                        )
                    )
                    .addProperty(
                        PropertySpec.builder(
                            "stories",
                            ClassName("kotlin.collections", "List")
                                .plusParameter(
                                    ClassName(
                                        "com.igenius.androidstories.app",
                                        "FragmentStory"
                                    )
                                )
                        )
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer(
                                """
                                listOf(${
                                    classNames.joinToString(",") {
                                        """
                            object: FragmentStory {
                                override val title = "${it.simpleName}"
                                override val description: String? = null
                                override fun generateFragment() = ${it.canonicalName}()
                            }"""
                                    }
                                }
                        )
                        """.trimIndent()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        file.writeTo(generatedSourcesRoot)
    }


    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}