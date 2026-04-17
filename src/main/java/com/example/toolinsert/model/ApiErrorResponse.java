package com.example.toolinsert.model;

import java.time.Instant;

public record ApiErrorResponse(Instant timestamp, int status, String error, String message) {
}
