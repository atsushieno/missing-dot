package dev.atsushieno.missingdot.xml

/*

TODOs:

- Name character checks are not really done.
- Support for DoctypeDecl is incomplete.
- Support for XML Declaration is missing (if required; it is processed as PI).

CDATA and Whitespaces are treated as Text node. Instead, isCDATA property is introduced.

 */

abstract class XmlReader : IXmlLineInfo {
	companion object {
		fun create(text: String) : XmlReader =
			XmlTextReader(text)
	}

    abstract fun read() : Boolean

    abstract val eof : Boolean
    abstract val depth : Int
    abstract val localName: String
    abstract val namespaceUri: String
    abstract val prefix: String
    open val name
    	get() = if (prefix.isNotEmpty()) "$prefix:$localName" else localName
    abstract val nodeType: XmlNodeType
    abstract val value: String
    abstract val isEmptyElement: Boolean
	abstract val isCDATA: Boolean
    abstract val attributeCount: Int

    abstract fun close()

    open fun moveToContent() : Boolean {
		moveToElement()
    	while (true) {
			when (nodeType) {
				XmlNodeType.Element, XmlNodeType.EndElement -> return true
				XmlNodeType.Text -> if (isCDATA || value.all { " \t\r\n".indexOf(it) < 0 }) return true
				else -> {}
			}
			if (!read())
				return false
		}
    }

    fun readStartElement() = readStartElement(null)
    fun readStartElement(name: String?) = readStartElement(name, null)
    fun readStartElement(localName: String?, namespaceUri: String?) {
        moveToContent()
        if (nodeType != XmlNodeType.Element)
            throw IllegalStateException("XmlReader is not at element: $nodeType")
        if (localName != null && localName != this.localName || namespaceUri != null && namespaceUri != this.namespaceUri)
            throw IllegalStateException("Expecting XmlReader at '$localName' element in '$namespaceUri' namespace, got '${this.localName}' element in '${this.namespaceUri}' instead")
        read()
    }

    fun readElementContentAsString() : String {
        if (isEmptyElement) {
            read()
            return ""
        }
        var content = "" // somewhat inefficient but there is usually one single text node.
        val startDepth = depth
        readStartElement()
        while(startDepth < depth) {
			if (nodeType == XmlNodeType.Text)
				content += value
			read()
		}
		readEndElement()
        return content
    }

	open fun readEndElement() {
		if (nodeType != XmlNodeType.EndElement)
			throw XmlException("Unexpected call to readEndElement() at $nodeType node.")
		read()
	}

	abstract fun moveToElement()
	abstract fun moveToFirstAttribute() : Boolean
	abstract fun moveToNextAttribute() : Boolean
}

