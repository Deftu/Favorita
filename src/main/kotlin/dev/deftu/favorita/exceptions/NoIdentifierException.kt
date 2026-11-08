package dev.deftu.favorita.exceptions

/** Thrown by [dev.deftu.favorita.api.FavoritedSlots] when no world or server is loaded, or a place is blank */
class NoIdentifierException(message: String) : RuntimeException(message)
