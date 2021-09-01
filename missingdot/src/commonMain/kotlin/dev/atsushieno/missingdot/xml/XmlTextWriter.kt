package dev.atsushieno.missingdot.xml

class XmlTextWriter(private val output: StringBuilder) : XmlWriter() {

    private var state = WriteState.Start

    private var openAttribute = false
    private var openElement = false
    private val openTags = mutableListOf<String>()

    var namespaces = true
    var quoteChar = '"'
    var indent = false
    var newLineChars = "\n"

    val nsmgr = XmlNamespaceManager()

    override val writeState: WriteState
        get() = state

    override fun close() {
        if (state != WriteState.Closed && state != WriteState.Error) {
            while (openTags.any())
                writeEndElement()
            state = WriteState.Closed
        }
    }

    override fun lookupPrefix(ns: String): String? =
        if (!namespaces) null else nsmgr.lookupPrefix(ns)

    private fun writeIndentLine() {
        if (indent)
            output.append(newLineChars)
    }

    private fun writeIndent() {
        if (indent)
            for (i in 0 until openTags.size)
                output.append("  ")
    }

    override fun writeStartDocument(encoding: String?, standalone: Boolean?) {
        if (state != WriteState.Start)
            throw XmlException("XmlTextWriter is not at Start state ($state)")
        state = WriteState.Prolog

        output.append("<?xml")
        if (encoding != null)
            output.append(" encoding").append(quoteChar).append(encoding).append(quoteChar)
        if (standalone != null)
            output.append(" standalone").append(quoteChar).append(if (standalone) "yes" else "no").append(quoteChar)
        output.append(" ?>")
        writeIndentLine()
    }

    private fun checkState() {
        if (state == WriteState.Error)
            throw XmlException("XmlTextWriter is at Error state")
        if (state == WriteState.Closed)
            throw XmlException("XmlTextWriter is already at Closed state")
    }

    override fun writeEndDocument() {
        checkState()

        while (openTags.any())
            writeEndElement()

        state = WriteState.Closed
    }

    override fun writeDoctype(name: String, publicId: String?, systemId: String?, internalSubset: String?) {
        checkState()
        if (state != WriteState.Start && state != WriteState.Prolog)
            throw XmlException("XmlTextWriter is already at $state state")
        state = WriteState.Element

        output.append("<!DOCTYPE")
        if (publicId != null)
            output.append(" PUBLIC ").append(quoteChar).append(publicId).append(quoteChar)
        if (systemId != null)
            output.append(" SYSTEM ").append(quoteChar).append(systemId).append(quoteChar)
        if (internalSubset != null)
            output.append(quoteChar).append(internalSubset).append(quoteChar)
        output.append(">")
        writeIndentLine()
    }

    private fun checkAndCloseStartTagIfOpen(skipTagClosing: Boolean = false) {
        checkState()
        if (openAttribute)
            writeEndAttribute()
        if (state == WriteState.Attribute && !skipTagClosing)
            output.append('>')
        state = WriteState.Content
        openElement = false
    }

    override fun writeStartElement(prefix: String?, localName: String, namespaceUri: String) {
        val wasOpenElement = openElement
        checkAndCloseStartTagIfOpen()
        if (wasOpenElement)
            writeIndentLine()

        if (namespaces && prefix != null && namespaceUri == XmlNamespaceManager.Xmlns2000)
            nsmgr.addNamespace(prefix, namespaceUri)

        val actualPrefix = prefix ?: if (namespaces) lookupPrefix(namespaceUri) ?: throw XmlException("No namespace prefix for \"$namespaceUri\" is declared in this XmlTextWriter.") else ""
        val tag = if (actualPrefix.isNotEmpty()) "$actualPrefix:$localName" else localName
        writeIndent()
        output.append("<").append(tag)
        openTags.add(tag)

        openElement = true
        state = WriteState.Attribute
    }

    override fun writeEndElement() {
        if (openElement) {
            checkAndCloseStartTagIfOpen(true)
            if (openTags.isEmpty())
                throw XmlException("Element is not started in this XmlTextWriter.")

            output.append(" />")
            openTags.removeLast()
            writeIndentLine()
        }
        else
            writeFullEndElement()
    }

    override fun writeFullEndElement() {
        checkAndCloseStartTagIfOpen()
        if (openTags.isEmpty())
            throw XmlException("Element is not started in this XmlTextWriter.")

        output.append("</").append(openTags.last()).append('>')
        openTags.removeLast()
        writeIndentLine()
    }

    override fun writeStartAttribute(prefix: String?, localName: String, namespaceUri: String) {
        checkState()
        if (openAttribute)
            throw XmlException("Attempt to write another XML attribute whilte writing an attribute.")
        state = WriteState.Attribute

        if (namespaces && prefix != null && namespaceUri == XmlNamespaceManager.Xmlns2000)
            nsmgr.addNamespace(prefix, namespaceUri)

        val actualPrefix = prefix ?: if (namespaces) lookupPrefix(namespaceUri) ?: throw XmlException("No namespace prefix for \"$namespaceUri\" is declared in this XmlTextWriter.") else ""
        val name = if (actualPrefix.isNotEmpty()) "$actualPrefix:$localName" else localName
        output.append(' ').append(name).append('=').append(quoteChar)

        openAttribute = true
    }

    override fun writeEndAttribute() {
        checkState()
        if (!openAttribute)
            throw XmlException("Attribute is not started in this XmlTextWriter.")
        openAttribute = false

        output.append(quoteChar)
    }

    override fun writeCData(text: String) {
        checkAndCloseStartTagIfOpen()

        output.append("<![CDATA[").append(text.replace("]]>", "]]&gt;")).append("]]>")
    }

    override fun writeComment(comment: String) {
        checkAndCloseStartTagIfOpen()

        output.append("<!--").append(comment.replace("--", "-&#x2D;")).append("-->")
    }

    override fun writeProcessingInstruction(name: String, value: String?) {
        checkState() // do not call checkAndCloseStartTagOfOpen() as it can stay at Start/Prolog/Element state.
        if (state == WriteState.Attribute)
            writeEndAttribute()

        output.append("<?").append(name)
        if (value != null)
            output.append(' ').append(quoteChar).append(escapeCharacterEntities(value)).append(quoteChar)
        output.append(" ?>")
    }

    override fun writeRaw(text: String) {
        if (openAttribute)
            checkState()
        else
            checkAndCloseStartTagIfOpen()

        output.append(text)

        openElement = openElement && state == WriteState.Attribute
    }

    private fun escapeCharacterEntities(s: String) =
        s.replace("&", "&amp")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")

    override fun writeString(text: String) {
        writeRaw(escapeCharacterEntities(text))
    }

    override fun writeWhitespace(text: String) {
        if (text.any { " \t\r\n".indexOf(it) >= 0  })
            throw XmlException("Attempt to write non-whitespace string as whitespaces.")
        writeRaw(text)
    }

    override fun writeEntityRef(name: String) {
        // FIXME: check XML NameChars
        writeRaw("&$name;")
    }
}