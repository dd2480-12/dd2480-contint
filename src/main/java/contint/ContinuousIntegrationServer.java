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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

        Path tmp = Files.createTempDirectory("tmp_git");
        try {
            Git.cloneRepository()
                    .setURI("https://github.com/dd2480-12/dd2480-contint.git")
                    .setDirectory(new File(tmp.toString()))
                    .setBranchesToClone(Arrays.asList(payload.ref))
                    .setBranch(payload.ref)
                    .call();

            String command = tmp.toString() + "gradlew" + " " + "build";
            Process p = Runtime.getRuntime().exec(command);
            response.getWriter().println("Execute completely!");
            String line;
            BufferedReader input =
                    new BufferedReader
                            (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                response.getWriter().println(line);
            }
            input.close();


        } catch (Exception e) {
            System.out.println("Failed in compile stage");
        }

        Files.delete(tmp);
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
