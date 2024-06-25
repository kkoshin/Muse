package io.github.kkoshin.elevenlabs.error

import io.github.kkoshin.elevenlabs.model.HttpValidationError

class UnprocessableEntityError(val error: HttpValidationError) : ElevenLabsError(
    message = "UnprocessableEntityError",
    statusCode = 422,
)