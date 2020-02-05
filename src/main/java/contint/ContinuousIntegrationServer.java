package contint;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/*import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;*/


/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook See the Jetty
 * documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler {

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        String body = CharStreams.toString(request.getReader());

        Gson gson = new Gson();
        Payload payload = gson.fromJson(body, Payload.class);

        Path tmp_path = Files.createTempDirectory("tmp_git");

        try {
            Git.cloneRepository()
                    .setURI("https://github.com/dd2480-12/dd2480-contint.git")
                    .setDirectory(new File(tmp_path.toString()))
                    .setBranchesToClone(Arrays.asList(payload.ref))
                    .setBranch(payload.ref)
                    .call();

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

        } catch (Exception e) {
            System.out.println("Failed in compile stage");
            e.printStackTrace();
        }
        //Files.delete(tmp_path);
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
