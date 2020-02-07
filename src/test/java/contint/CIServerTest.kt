package contint

import org.eclipse.jetty.server.Request
import org.junit.Test
import org.mockito.Mockito.*

import java.io.BufferedReader
import java.io.StringReader
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CIServerTest {

    /*
        Test that data is propagated properly.
        GitHandler creates temp folder and returns path which is used by GradleHandler.
        StatusGenerator reads gradle output and returns a CStatus used by the notifier to post a commit.
     */
    @Test
    fun handleWith() {
        val request = mock(HttpServletRequest::class.java)
        val response = mock(HttpServletResponse::class.java)
        val mockBaseReq = mock(Request::class.java)

        val mockGitHandler = mock(GitHandlerInterface::class.java)
        val mockGradleHandler = mock(GradleHandlerInterface::class.java)

        val mockNotifier = mock(CSNotifierInterface::class.java)
        val mockStatusGenerator = mock(CommitStatusGeneratorInterface::class.java)

        val mockPayload = "{\n  \"ref\": \"refs/heads/master\", " +
                "\"repository\": {" +
                "\"clone_url\": \"https://github.com/dd2480-12/dd2480-contint.git\"" +
                "\n}\n}"
        val mockTmpPath = "tmp_git333"

        val testUrl = "https://github.com/dd2480-12/dd2480-contint.git"

        val sr = BufferedReader(StringReader(mockPayload))
        val token = mock(APITokenProvider::class.java)

        `when`(request.reader).thenReturn(sr)
        `when`(mockBaseReq.toString()).thenReturn("Mock base request")

        `when`(mockGitHandler.cloneRepoToTmp(
                testUrl, "refs/heads/master"))
                .thenReturn(mockTmpPath)

        `when`(mockGitHandler.deleteTemp()).thenReturn(true)

        `when`(mockGradleHandler.runGradle(mockTmpPath)).thenReturn(0)

        val mockCStatus = CStatus(CStatus.CState.SUCCESS, "aeaeea12312ea", null, null)
        `when`(mockStatusGenerator.generateCStatus("$mockTmpPath/build.json")).thenReturn(mockCStatus)

        `when`(mockNotifier.postCommitStatus(mockCStatus,token))
                .thenReturn(true)

        val mockHandlers = CIHandlers(mockGradleHandler, mockGitHandler, mockNotifier, mockStatusGenerator)
        val ciServer = CIServer()
        ciServer.handleWith("TestTarget", mockBaseReq, request, response, mockHandlers, token)

        verify(request).reader
        verify(mockGitHandler).cloneRepoToTmp(testUrl, "refs/heads/master")
        verify(mockGitHandler).deleteTemp()
        verifyNoMoreInteractions(mockGitHandler)

        verify(mockGradleHandler, only()).runGradle(mockTmpPath)
        verify(mockStatusGenerator, only()).generateCStatus("$mockTmpPath/build.json")
        verify(mockNotifier, only()).postCommitStatus(mockCStatus, token)
    }
}