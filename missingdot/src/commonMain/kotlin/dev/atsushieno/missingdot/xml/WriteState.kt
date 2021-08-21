package dev.atsushieno.missingdot.xml

enum class WriteState {
    Start,
    Prolog,
    Element,
    Attribute,
    Content,
    Closed,
    Error,
}