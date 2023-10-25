import service.ClassService

fun main(args: Array<String>) {
    println("Welcome to kotlinmaid!")

    val entitiesPath = args[0]
    val packageName = args[1]
    val savePath = args[2]

    val classService = ClassService()

    classService.readFiles(entitiesPath, packageName)

    classService.writeUmlText(savePath)
}