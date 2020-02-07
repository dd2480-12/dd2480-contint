package contint

import java.io.File

interface CommitStatusGeneratorInterface {
    fun generateCStatus(jsonBuild : String) : CStatus
}

interface CSNotifierInterface {
    fun postCommitStatus(cs : CStatus, tokenProvider: APITokenProvider) : Boolean
}

interface GitHandlerInterface {
    fun cloneRepoToTmp(uri : String, branch : String) : String
    fun deleteTemp() : Boolean
}

interface GradleHandlerInterface {
    fun runGradle(tmpPath : String) : Int
}

data class CIHandlers(val gradleHandler: GradleHandlerInterface,
                      val gitHandler : GitHandlerInterface,
                      val notifier : CSNotifierInterface,
                      val commitStatusFactory: CommitStatusGeneratorInterface) {
    companion object {
        fun getDefaultHandlers() : CIHandlers =
                CIHandlers(
                        GradleHandler(),
                        GitHandler(),
                        CSNotifier(Repo()),
                        CommitStatusGenerator())

    }
}







