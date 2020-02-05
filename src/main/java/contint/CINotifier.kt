package contint

import org.eclipse.egit.github.core.CommitStatus
import org.eclipse.egit.github.core.RepositoryId
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.CommitService
import java.io.IOException

/**
 * Represents a commit status that is yet to be sent. Used by CINotifier.
 */
class CStatus(
        state: Boolean,
        val commitSHA: String,
        val logURL: String?,
        val description: String?) {
    val state = if (state) CState.SUCCESS else CState.ERROR

    enum class CState {
        ERROR, FAILURE, PENDING, SUCCESS;

        override fun toString() = super.toString().toLowerCase()
    }

    /**
     * @return The CommitStatus object that corresponds to this object
     */
    fun toCommitStatus(): CommitStatus = CommitStatus()
            .setState(state.toString())
            .setTargetUrl(logURL)
            .setDescription(description)
}

/**
 *  Wrapper for RepositoryId class. The only function of this class is to enable default values for name
 *  and owner.
 */
class Repo(owner: String = "dd2480-12", repoName: String = "dd2480-contint") : RepositoryId(owner, repoName)

/**
 * Interface for wrapper classes for an oauth2 token. If the token is contained within an environment variable,
 * wrap the name of the variable in an Environment class. If the token is to be used in clear text,
 * wrap the token in a ClearText class.
 */
interface APITokenProvider {
    fun getOAuth2Token(): String?
}

/**
 * Holds the name of an environment variable which holds an oauth2 token
 */
data class TokenEnvironmentVariable(private val varName: String) : APITokenProvider {
    override fun getOAuth2Token(): String? = System.getenv(varName)
}

/**
 * Holds an oauth2 token in clear text
 */
data class TokenClearText(private val token: String) : APITokenProvider {
    override fun getOAuth2Token(): String = token
}

/**
 * Posts commit statuses to the given repository on GitHub
 */
class CINotifier(private val repo: Repo) {

    /**
     * @param cs The commit status to be sent
     * @param tokenProvider Wrapper of an oauth2 token
     * @return Whether the operation was successful or not
     */
    fun postCommitStatus(cs: CStatus, tokenProvider: APITokenProvider): Boolean {
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

