package com.hwg.android.pluginregistry.processor

import javax.annotation.processing.ProcessingEnvironment
import javax.tools.StandardLocation

object PropertiesUtil {

    fun getPropertiesFilePath(pluginName: String): String {
        return "META-INF/gradle-plugins/${pluginName}.properties"
    }

    fun writePropertiesFileContent(pluginName: String, classPath: String, processingEnv: ProcessingEnvironment) {
        processingEnv.filer.createResource(StandardLocation.CLASS_OUTPUT, "", getPropertiesFilePath(pluginName)).openOutputStream().bufferedWriter().use {
            it.write("implementation-class=${classPath}")
            it.flush()
        }
    }

}