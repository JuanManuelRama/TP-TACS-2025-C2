package com.g7.exception

class MissingParameterException(parameterName: String) :
    Exception("Missing parameter: $parameterName")

class InvalidIdException(id: String) : Exception("Invalid ID format: $id")

class InvalidCredentialsException(message: String = "Invalid username or password") : RuntimeException(message)

class InvalidConstructorException(message: String) : RuntimeException(message)