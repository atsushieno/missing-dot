package dev.atsushieno.missingdot.xml

abstract class XmlWriter {
    companion object {
        fun create(output: StringBuilder) = XmlTextWriter(output)
    }

    abstract val writeState : WriteState

    open val xmlLang : String? = null

    open val xmlSpace = XmlSpace.None

    abstract fun close()

    abstract fun lookupPrefix(ns: String) : String?

    abstract fun writeStartDocument(encoding: String? = null, standalone: Boolean? = null)

    abstract fun writeEndDocument()

    abstract fun writeDoctype(name: String, publicId: String?, systemId: String?, internalSubset: String?)

    fun writeStartElement(name: String) = writeStartElement("", name, "")
    fun writeStartElement(localName: String, namespaceUri: String) = writeStartElement(null, localName, namespaceUri)
    abstract fun writeStartElement(prefix: String?, localName: String, namespaceUri: String)

    abstract fun writeEndElement()

    abstract fun writeFullEndElement() // awkward name...

    fun writeStartAttribute(name: String) = writeStartAttribute("", name, "")
    abstract fun writeStartAttribute(prefix: String?, localName: String, namespaceUri: String)

    abstract fun writeEndAttribute()

    abstract fun writeCData(text: String)

    abstract fun writeEntityRef(name: String) // awkward name...

    abstract fun writeComment(comment: String)

    abstract fun writeProcessingInstruction(name: String, value: String?)

    open fun writeQualifiedName(localName: String, namespaceUri: String) {
        val prefix = lookupPrefix(namespaceUri)
        writeString(if (prefix != null && !prefix.isEmpty()) "$prefix:$localName" else localName)
    }

    abstract fun writeRaw(text: String)

    abstract fun writeString(text: String)

    abstract fun writeWhitespace(text: String)

    open fun writeAttributes(reader: XmlReader, writeDefaultAttributes: Boolean) {
        TODO("Implement")
    }

    fun writeAttributeString(name: String, value: String) = writeAttributeString("", name, "", value)
    fun writeAttributeString(localName: String, namespaceUri: String, value: String) = writeAttributeString(null, localName, namespaceUri, value)
    fun writeAttributeString(prefix: String?, localName: String, namespaceUri: String, value: String) {
        writeStartAttribute(prefix, localName, namespaceUri)
        writeString(value)
        writeEndAttribute()
    }

    fun writeElementString(name: String, value: String) = writeElementString("", name, "", value)
    fun writeElementString(localName: String, namespaceUri: String, value: String) = writeElementString(null, localName, namespaceUri, value)
    fun writeElementString(prefix: String?, localName: String, namespaceUri: String, value: String) {
        writeStartElement(prefix, localName, namespaceUri)
        writeString(value)
        writeEndElement()
    }


}

