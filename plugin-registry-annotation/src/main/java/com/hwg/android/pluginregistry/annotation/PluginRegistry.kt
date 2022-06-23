package com.hwg.android.pluginregistry.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class PluginRegistry(val value: String)
