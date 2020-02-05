package contint

import org.junit.Test

import org.junit.Assert.*
import java.io.File
import java.io.FileWriter

class TokenFileTest {

    @Test
    fun getOAuth2Token() {
        val t = TokenFile("thisPathDoesNotExist92938283")
        assertNull(t.getOAuth2Token())

        val s = "thisPathDoesExist92938283"

        try {
            val f = File(s)
            f.createNewFile()
            val fw = FileWriter(f)
            fw.write("Test")
            fw.flush()
            fw.close()

            val t2 = TokenFile(s)
            assertEquals("Test", t2.getOAuth2Token())
            f.delete()
        } catch (e: Exception) {
            val f = File(s)
            if(f.exists()) f.delete()
            fail()
        }

    }
}