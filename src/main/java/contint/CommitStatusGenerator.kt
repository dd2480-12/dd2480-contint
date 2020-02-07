package contint

import com.google.gson.Gson
import java.io.File

/**
 *  Generates CStatus from a file containing the output of gradle build --scan
 */
class CommitStatusGenerator : CommitStatusGeneratorInterface {

    /**
     * @param jsonBuild Path to a file containing output of gradle build --scan
     *
     * @return A CStatus representing the commit status to be sent based on the build data
     */
    override fun generateCStatus(jsonBuild : String): CStatus {
        val gson = Gson()
        val jsonString = File(jsonBuild).readText()
        val data = gson.fromJson(jsonString, Response::class.java)
        return CStatus(toState(data), data.commit, data.url, generateDescription(data))
    }

    /*
        Convert the success field to a CState
     */
    private fun toState(build : Response) : CStatus.CState =
            if(build.success) CStatus.CState.SUCCESS
            else CStatus.CState.ERROR

    /*
        Modify to generate a description for the CStatus based on the build data
     */
    private fun generateDescription(data : Response) : String? {
        return null
    }
}