package contint

import com.google.common.io.CharStreams
import com.google.gson.Gson
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.File
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/*import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;*/
class CIServer : AbstractHandler() {

    /**
     * The handler of the CI server. The handler will clone the branch committed to,
     * build and test using Gradle, and then send the results to Github. A Github
     * auth token is required to be set in a file called `github.token`.
     *
     * @param target      The target of the request - either a URI or a name.
     * @param baseRequest The original unwrapped request object.
     * @param request     The request either as the Request object or a wrapper of
     * that request. The
     * HttpConnection.getCurrentConnection().getHttpChannel().getRequest()
     * method can be used access the Request object if required.
     * @param response    The response as the Response object or a wrapper of that
     * request. The
     * HttpConnection.getCurrentConnection().getHttpChannel().getResponse()
     * method can be used access the Response object if required.
     * @throws IOException      if unable to handle the request or response
     * processing
     * @throws ServletException if unable to handle the request or response due to
     * underlying servlet issue
     */
    @Throws(IOException::class, ServletException::class)
    override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) =
        handleWith(target, baseRequest, request, response,
                CIHandlers.getDefaultHandlers(),
                TokenFile("github.token"))


    /**
     * Handle the request with the provided handlers that control the interaction with Git and Gradle.
     * See handle.
     */
    fun handleWith(target: String, baseRequest: Request,
                   request: HttpServletRequest, response: HttpServletResponse,
                   handlers : CIHandlers, token : APITokenProvider)
    {
        // Unpack data class
        val (gradleHandler, gitHandler, notifier, statusGenerator) = handlers

        try {
            response.contentType = "text/html;charset=utf-8"
            response.status = HttpServletResponse.SC_OK
            baseRequest.isHandled = true

            println(target)

            val body = CharStreams.toString(request.reader)
            val gson = Gson()
            val payload = gson.fromJson(body, Payload::class.java)

            println("Cloning ${payload.ref}")

            val tmpPath = gitHandler.cloneRepoToTmp(payload.repository.clone_url, payload.ref)


            println("Running gradle")
            val retCode = gradleHandler.runGradle(tmpPath)

            println("Generating commit status")
            val cs = statusGenerator.generateCStatus("$tmpPath/build.json")
            println("Posting commit status")
            System.out.flush()
            notifier.postCommitStatus(cs, token)

        }
        catch (e : java.lang.Exception) {
            println("Build failed")
            e.printStackTrace()
        }
        finally {
            gitHandler.deleteTemp()
        }
    }

    companion object {
        // used to start the CI server in command line
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val server = Server(8080)
            server.handler = CIServer()
            server.start()
            server.join()
        }
    }
}