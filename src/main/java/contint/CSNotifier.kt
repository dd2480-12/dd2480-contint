package contint

import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.CommitService
import java.io.File
import java.io.IOException

/**
 *  Wrapper for RepositoryId class. The only function of this class is to enable default values for name
 *  and owner.
 */
class Repo(owner: String = "dd2480-12", repoName: String = "dd2480-contint") : RepositoryId(owner, repoName)

/**
 * Interface for wrapper classes for an oauth2 token. If the token is contained within an environment variable,
 * wrap the name of the variable in a TokenEnvironmentVariable class. If the token is contained within a file,
 * wrap the File in a TokenFile class.
 */
interface APITokenProvider {
    fun getOAuth2Token(): String?
}

/**
 * Holds the name of an environment variable which holds an oauth2 token
 */
data class TokenEnvironmentVariable(private val varName: String) : APITokenProvider {

    /**
     * Read environment variable
     *
     * @return Value of environment variable or null if variable does not exist
     */
    override fun getOAuth2Token(): String? = System.getenv(varName)
}

/**
 * Holds the file containing an oauth2 token
 */
class TokenFile(private val fileName : String) : APITokenProvider {

    /**
     * Read contents of the associated file
     *
     * @return Contents of file or null if unable to read or open file
     */
    override fun getOAuth2Token(): String? = try {
        File(fileName).readText()
    } catch  (e : Throwable) {
        println("Warning: Unable to read token: $e")
        null
    }
}

class CSNotifier(private val repo: Repo) : CSNotifierInterface {

    /**
     * Post a commit status to GitHub
     *
     * @param cs The commit status to be sent
     * @param tokenProvider Wrapper of an oauth2 token
     * @return Whether the operation was successful or not
     */
    override fun postCommitStatus(cs: CStatus, tokenProvider: APITokenProvider): Boolean {
        val authToken = tokenProvider.getOAuth2Token()
        if (authToken == null) {
            println("Error: authentication token is null")
            return false
        }
        if (authToken.isEmpty()) {
            println("Error: authentication token is empty")
            return false
        }
        if (!validSHA(cs.commitSHA)) {
            println("Error: Invalid SHA")
            return false
        }

        val client = GitHubClient().setOAuth2Token(authToken)

        val commitService = CommitService(client)
        return try {
            val resCS = commitService.createStatus(repo, cs.commitSHA, cs.toCommitStatus())
            println("Commit status successfully sent at ${resCS.createdAt}")
            true
        } catch (e: IOException) {
            println("Failed to send commit status")
            println(e.message)
            false
        }
    }

    /**
     * Check if the sha given for a POST request is valid.
     */
    private fun validSHA(commit: String) =
            commit.matches("^[a-fA-F0-9]{7,40}\$".toRegex())
}