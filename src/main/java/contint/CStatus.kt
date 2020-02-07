package contint

import org.eclipse.egit.github.core.CommitStatus

/**
 * Represents a commit status that is yet to be sent. Used by CSNotifier.
 */
data class CStatus(val state: CState,
                   val commitSHA: String,
                   val logURL: String?,
                   val description: String?) {


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