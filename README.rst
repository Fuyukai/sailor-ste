Sailor STE
----------

Sailor STE (Standard Extensions) is a library for extensions that are missing from the Java or
Kotlin standard libraries, or more convenient ways to use certain built-in APIs.

Consider this to be my personal "Guava for Kotlin" or "Apache Commons for Kotlin".

Features
--------

Collections
===========

STE provides ``MultiValuedMap`` and ``MutableMultiValuedMap`` interfaces alongside a default
implementation that uses a HashSet to store the multiple values.

Crypto
======

STE provides more ergonomic cryptographic operations than vanilla ``java.security`` wrapping
libsodium with sensible defaults via the usage of the ``Crypto`` object. Extensions also exist for:

 - Symmetric encryption via ``ByteArray.encrypt()`` and ``MessageNoncePair.decrypt()``
 - Integrity hashing via ``ByteArray.(verify)integrityHash()``
 - Password hashing via ``ByteArray.(verify)passwordHash()``

Extensions also exist on ``String`` types which will encode the String in UTF-8 first.

Additionally, the libsodium CSPRNG can be accessed via the ``secureRandomBytes`` function.

Paths
=====

STE adds the ``Path.div()`` operator overload for convenient and natural ``Path`` chaining.

URI
===

STE adds several conveniences around URIs:

 - The ``uri()`` function which allows creating ``java.net.URI`` instances via named parameters
 - A ``URI.copy()`` method similar to a data class copy method
 - A ``URI.parseQueryParams()`` method that will turn the query part of a URI into a MultiValuedMap

Strings
=======

STE adds some String convenience extensions:

 - ``String.mixedToAny()`` converts a String from mixedCase to another case separated by a
   delimiter.
 - ``String.mixedTo(Snake|Kebab)Case`` exist as aliases with fixed delimiters.

Codec
=====

STE adds some codec operations:

 - ``ByteArray.toBase64()`` as a shortcut for base64 encoding
 - ``String.decodeBase64()`` as a shortcut for base64 decoding
 - ``String/ByteArray.hexlify()`` and ``String.dehexlify()`` for hex encoding/decoding
