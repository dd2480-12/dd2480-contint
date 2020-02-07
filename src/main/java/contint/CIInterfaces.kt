package contint

/**
 * Interface for handlers responsible for generating CStatus objects from JSON in
 */
interface CommitStatusGeneratorInterface {

    /**
     * @param jsonBuildFilePath to a file containing build information in a JSON format.
     */
    fun generateCStatus(jsonBuildFilePath : String) : CStatus
}

/**
 * Interface for handlers responsible for sending a POST request with a commit status
 */
interface CSNotifierInterface {

    /**
     * Post a commit status through the GitHub API. Requires an oauth2 token.
     *
     * @param cs Representation of the commit status to be sent
     * @param tokenProvider Token provider that will make the API oauth2 token accessible as a string
     */
    fun postCommitStatus(cs : CStatus, tokenProvider: APITokenProvider) : Boolean
}

/**
 * Interface for handlers responsible for cloning repositories into temporary folders.
 */
interface GitHandlerInterface {

    /**
     * Clone a remote repo to a local temporary folder
     *
     * @param uri Repo URI
     * @param branch Name of the branch to be cloned
     */
    fun cloneRepoToTmp(uri : String, branch : String) : String

    /**
     * Delete the temporary folder
     */
    fun deleteTemp() : Boolean
}

/**
 * Interface for handlers responsible for running gradle tasks.
 */
interface GradleHandlerInterface {

    /**
     * Run the tasks in the handler
     *
     * @param tmpPath Path to the directory to run gradle in
     * @return Exit code of Gradle
     */
    fun runGradle(tmpPath : String) : Int
}

/**
 * Holds the four handlers needed by the CIServer to handle side effects.
 */
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







