package service

import infrastructure.IoHelper
import model.ClassStructure
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

/**
 *
 * @author Nurzhan Aitbayev github.com/nurzhanme
 */
class ClassService {
    private val INSTANCE_VALUE: String = "INSTANCE"
    private val classes: MutableMap<String, ClassStructure> = mutableMapOf()

    fun readFiles(path: String, packageName: String) {
        val fileExtension = "kt" // "kt" for Kotlin files

        //todo read all files from all folders
        val files = File(path).walkTopDown().filter { it.isFile && it.extension == fileExtension }

        for (file in files) {

            val kClass = getkClassFromQualifiedName(packageName, file)

            val classname = kClass.simpleName!!
            val supertype = kClass.supertypes.firstOrNull()?.toString()

            val props: MutableList<Pair<String, String>> = mutableListOf()
            val methods: MutableList<String> = mutableListOf()

            for (kProperty in kClass.declaredMemberProperties) {
                val accessModifier = getVisibility(kProperty.visibility!!)
                val typeName = kProperty.returnType.toString()
                val additionalModifier = StringBuilder()
                if (kProperty.isAbstract) {
                    additionalModifier.append('*')
                }

                val umlProperty = "${accessModifier}${kProperty.name} ${typeName}${additionalModifier}"
                props.add(Pair(umlProperty, typeName))
            }

            for (kFunction in kClass.declaredMemberFunctions) {
                val accessModifier = getVisibility(kFunction.visibility!!)

                // Exclude instance kind name (1st param of any class method is 'this' instance)
                val parameters = kFunction.parameters.filter { it.kind.name != INSTANCE_VALUE }.joinToString(", ") { "${it.name} ${it.type}" }

                val additionalModifier = StringBuilder()
                if (kFunction.isAbstract) {
                    additionalModifier.append('*')
                }

                val umlMethod = "${accessModifier}${kFunction.name}($parameters) ${kFunction.returnType?.toString()}${additionalModifier}"
                methods.add(umlMethod)
            }

            classes[classname] = ClassStructure(
                kClass.qualifiedName!!,
                classname,
                supertype!!.substring(supertype!!.lastIndexOf('.') + 1),
                methods,
                props)
        }

    }

    private fun getkClassFromQualifiedName(packageName: String, file: File): KClass<out Any> {
        val kClass = Class.forName("${packageName}.${file.name.substring(0, file.name.lastIndexOf('.'))}").kotlin
        return kClass
    }

    fun writeUmlText(path: String) {
        val openCurlyBrace = '{'
        val closeCurlyBrace = '}'

        val result = mutableListOf<String>()
        val classRelationships = mutableListOf<String>()

        result.add("```mermaid")
        result.add("classDiagram")
        for (classStructure in classes.values) {
            if (!classStructure.parentName.isNullOrBlank() && classStructure.parentName != "Any") {
                classRelationships.add("${classStructure.parentName}  <|-- ${classStructure.name}")
            }

            if (classStructure.properties.isEmpty() && classStructure.methods.isEmpty()) {
                continue
            }

            result.add("class ${classStructure.name}$openCurlyBrace")
            classStructure.properties.forEach { property ->
                if (classes.containsKey(property.second)) {
                    classRelationships.add("${property.second} o-- ${classStructure.name}")
                }
                result.add(property.first)
            }

            classStructure.methods.forEach { method ->
                result.add(method)
            }

            result.add("$closeCurlyBrace")
        }
        result.addAll(classRelationships)

        result.add("```")

        IoHelper().createFile(path, result)
    }

    private fun getVisibility(visibility: KVisibility): Char{
        when (visibility) {
            KVisibility.PUBLIC -> {
                return '+'
            }
            KVisibility.PRIVATE -> {
                return '-'
            }
            KVisibility.PROTECTED -> {
                return '#'
            }
            else -> return '~'
        }
    }
}