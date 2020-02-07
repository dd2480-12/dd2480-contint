package contint

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Handles cloning of git repos into a temporary directory
 */
class GitHandler : GitHandlerInterface {
    private var tmpDir : File? = null
    val defaultURI = "https://github.com/dd2480-12/dd2480-contint.git"

    /**
     * Clone a branch of a GitHub repository into a temporary folder.
     *
     * Note: It is the responsibility of the caller to delete the folder, either
     * directly or through the method deleteTemp()
     *
     * @param uri Repository URI, ex "https://github.com/grp/repoName.git"
     * @param branch Name of branch to clone
     * @return Path to created temporary directory
     */
    override fun cloneRepoToTmp(uri : String, branch : String) : String {
        val tmpPath = Files.createTempDirectory(Paths.get("."), "tmp_git")
        tmpDir = File(tmpPath.toString())
        Git.cloneRepository()
                .setURI(uri)
                .setDirectory(tmpDir)
                .setBranchesToClone(listOf(branch))
                .setBranch(branch)
                .call()
        return tmpPath.toString()
    }

    /**
     * Delete the last created temporary folder by this handler
     *
     * @return Success of deletion
     */
    override fun deleteTemp() : Boolean =
            try {
                FileUtils.deleteDirectory(tmpDir)
                tmpDir = null
                true
            } catch (e: Exception) {
                false
            }
}