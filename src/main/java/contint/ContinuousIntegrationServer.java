package contint;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/*import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;*/

/**
 * Represents the server handler to a Server object. 
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    /**
     * The handler of the CI server. The handler will clone the branch committed to,
     * build and test using Gradle, and then send the results to Github. A Github
     * auth token is required to be set in a file called `github.token`.
     * 
     * @param target      The target of the request - either a URI or a name.
     * @param baseRequest The original unwrapped request object.
     * @param request     The request either as the Request object or a wrapper of
     *                    that request. The
     *                    HttpConnection.getCurrentConnection().getHttpChannel().getRequest()
     *                    method can be used access the Request object if required.
     * @param response    The response as the Response object or a wrapper of that
     *                    request. The
     *                    HttpConnection.getCurrentConnection().getHttpChannel().getResponse()
     *                    method can be used access the Response object if required.
     * @throws IOException      if unable to handle the request or response
     *                          processing
     * @throws ServletException if unable to handle the request or response due to
     *                          underlying servlet issue
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        String body = CharStreams.toString(request.getReader());

        Gson gson1 = new Gson();
        Payload payload = gson1.fromJson(body, Payload.class);

        Path tmp_path = Files.createTempDirectory("tmp_git");

        try {
            Git.cloneRepository().setURI("https://github.com/dd2480-12/dd2480-contint.git")
                    .setDirectory(new File(tmp_path.toString())).setBranchesToClone(Arrays.asList(payload.ref))
                    .setBranch(payload.ref).call();

            File file_Gradle = new File(tmp_path.toString() + "/gradlew.bat");
            boolean exists = file_Gradle.exists();
            if (exists) {
                file_Gradle.setExecutable(true);
                file_Gradle.setReadable(true);
                file_Gradle.setWritable(true);
            } else {
                System.out.println("File not found");
            }

            String[] cmd = { "/bin/sh", "-c", "cd " + tmp_path.toString() + "; gradle build --scan;" };
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            Integer result = p.exitValue();
            response.getWriter().println("Process result: " + result);

            File file_json = new File(tmp_path.toString() + "/build.json");
            Gson gson2 = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file_json));
            Response data = gson2.fromJson(reader, Response.class);
            CINotifier ciNotifier = new CINotifier(new Repo());
            CStatus cStatus = new CStatus(data, "");
            ciNotifier.postCommitStatus(cStatus, new TokenFile("github.token"));

        } catch (Exception e) {
            System.out.println("Failed in compile stage");
            e.printStackTrace();
        }
        FileUtils.deleteDirectory(new File(tmp_path.toString()));
        response.getWriter().println("CI job done!");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
