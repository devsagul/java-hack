package lambda.javahack.backend

import java.math.BigInteger
import java.security.MessageDigest

class Util {
    companion object {
        fun getSHA256String(text: String): String {
            val bytes = text.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val sha256 = md.digest(bytes)
            val bigNum = BigInteger(1,sha256)
            val sb = StringBuilder(bigNum.toString(16))
            while (sb.length < 32) {
                sb.insert(0,'0')
            }
            return sb.toString()
        }
    }
}