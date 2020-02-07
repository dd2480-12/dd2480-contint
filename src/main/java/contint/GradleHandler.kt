package contint

import java.io.File

class GradleHandler : GradleHandlerInterface {
    private fun setGradleWrapperPermissions(wrapperFile: File): Boolean =
            if (!wrapperFile.exists()) {
                println("Wrapper file not found")
                false
            } else {
                listOf(wrapperFile.setExecutable(true),
                        wrapperFile.setReadable(true),
                        wrapperFile.setWritable(true)).all { it }
            }

    /**
     * Run Gradle with the --scan flag, and wait for it to exit
     *
     * @return Exit code of Gradle
     */
    override fun runGradle(tmpPath: String): Int {
        setGradleWrapperPermissions(File("$tmpPath/gradlew.bat"))
        val cmd = arrayOf("/bin/sh", "-c", "cd $tmpPath; gradle build --scan;")
        val p : Process = Runtime.getRuntime().exec(cmd)
        p.waitFor()
        return p.exitValue()
    }
}