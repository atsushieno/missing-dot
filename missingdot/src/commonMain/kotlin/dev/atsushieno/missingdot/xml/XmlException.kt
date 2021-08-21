package dev.atsushieno.missingdot.xml

class XmlException(message: String? = "XML error", innerException: Exception? = null, val lineNumber: Int = 0, val linePosition: Int = 0) : Exception(message, innerException) {
}