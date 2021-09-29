package dev.atsushieno.missingdot.xml

class XmlException(message: String? = "XML error", innerException: Exception? = null, val lineNumber: Int = 0, val linePosition: Int = 0)
    : Exception(if (lineNumber > 0) "$message (at $lineNumber${if (linePosition > 0) ", $linePosition" else ""})" else message,  innerException) {
}