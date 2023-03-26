package io.hackfest.web.error;

import java.util.List;

public record ErrorResponse(
        String errorId,
        List<Error> errors
) {
}
