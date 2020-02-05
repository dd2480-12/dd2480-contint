package contint

import org.junit.Test

import org.junit.Assert.*

class CStatusTest {

    @Test
    fun toCommitStatus() {
        val r = Response()
        r.url = "url"
        r.success = false
        r.commit = "sha"

        val cs = CStatus(r, null)
        assertEquals(cs.logURL, r.url)
        assertEquals(cs.commitSHA, r.commit)
        assertEquals(cs.state, CStatus.CState.ERROR)
        assertNull(cs.description)

        r.success = true
        val cs2 = CStatus(r, "desc")
        assertEquals(cs2.logURL, r.url)
        assertEquals(cs2.commitSHA, r.commit)
        assertEquals(cs2.state, CStatus.CState.SUCCESS)
        assertEquals(cs2.description, "desc")
    }
}