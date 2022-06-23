package com.hwg.android.pluginregistry.processor

import com.google.auto.service.AutoService
import com.hwg.android.pluginregistry.annotation.PluginRegistry
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class PluginRegistryProcessor : AbstractProcessor() {

    private val pluginRegistryMap = mutableMapOf<String, String>()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(PluginRegistry::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }

    override fun process(
        typeElementSet: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment?
    ): Boolean {
        if (typeElementSet == null || roundEnvironment == null) return false
        if (roundEnvironment.processingOver()) {
            generate()
        } else {
            parseAnnotation(typeElementSet, roundEnvironment)
        }
        return true
    }

    private fun parseAnnotation(
        typeElementSet: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment
    ) {
        roundEnvironment
            .getElementsAnnotatedWith(PluginRegistry::class.java)
            .forEach { element ->
                val pluginName = element.getAnnotation(PluginRegistry::class.java).value
                pluginRegistryMap[pluginName] =
                    getClassQualifiedName(element as TypeElement, element.simpleName.toString())
            }
    }

    private fun generate() {
        pluginRegistryMap.forEach { (pluginName, classPath) ->
            PropertiesUtil.writePropertiesFileContent(pluginName, classPath, processingEnv)
        }
    }

    private fun getClassQualifiedName(element: TypeElement, className: String): String {
        val closingElement = element.enclosingElement
        if (closingElement is PackageElement) {
            if (closingElement.isUnnamed) {
                return className
            }
            return "${closingElement.qualifiedName}.$className"
        }
        return getClassQualifiedName(
            closingElement as TypeElement,
            "${closingElement.simpleName}\$$className"
        )
    }

}