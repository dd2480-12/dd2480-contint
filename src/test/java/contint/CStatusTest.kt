package contint

import org.junit.Test

import org.junit.Assert.*

class CStatusTest {

    /*
     * Test that the returned CommitStatus correctly represents the CStatus object
     */
    @Test
    fun toCommitStatus() {
        val url = "url"
        val commit = "sha"

        val cs = CStatus(CStatus.CState.SUCCESS, commit, url, null).toCommitStatus()

        // targetURL and description should be same as given to constructor

        assertEquals(url, cs.targetUrl)
        assertNull(cs.description)
        // SUCCESS enum should be mapped to "success"
        assertEquals(cs.state, "success")


        val cs2 = CStatus(CStatus.CState.ERROR, commit, url, "desc").toCommitStatus()

        // ERROR enum should be mapped to "error"
        assertEquals(cs2.state, "error")
        // description should be same as given to constructor
        assertEquals(cs2.description, "desc")

        val cs3 = CStatus(CStatus.CState.PENDING, commit, null, null).toCommitStatus()
        val cs4 = CStatus(CStatus.CState.FAILURE, commit, null, null).toCommitStatus()

        // targetURL should be same as given to constructor
        assertNull(cs3.targetUrl)

        assertEquals(cs3.state, "pending")
        assertEquals(cs4.state, "failure")

    }

    @Test
    fun assess() {
        assertTrue(true)
    }
}