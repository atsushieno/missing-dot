package dev.atsushieno.missingdot.xml

enum class XmlNodeType {
	Document,
	Doctype,
	Element,
	EndElement,
	Attribute,
	Text,
	Comment,
	ProcessingInstruction
}