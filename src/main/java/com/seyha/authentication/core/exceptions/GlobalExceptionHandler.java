package com.seyha.authentication.core.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * Developed by ChhornSeyha
 * Date: 16/02/2026
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Login Failures (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
        problem.setTitle("Authentication Failure");
        return problem;
    }

    // 2. Handle Locked/Disabled Accounts (403)
    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatus(AccountStatusException e){

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "The account is locked or disabled."
        );
        problem.setTitle("Account Status Error");
        return problem;
    }

    // 3. Handle Permission Denied (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource."
        );
        problem.setTitle("Access Denied");
        return problem;

    }

    // 4. Handle JWT Signature Errors (403)
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "The JWT signature is invalid."
        );
        problem.setTitle("Security Error");
        return problem;
    }

    // 5. Handle Validation Errors (400) - The most important one!

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more validation errors occurred."
        );
        problem.setTitle("Validation Failed");

        // Collect all field errors into a Map
        Map<String, String> fieldError = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
                err->fieldError.put(err.getField(), err.getDefaultMessage())
        );
    // Add the map to the "properties" field of the JSON
        problem.setProperty("errors", fieldError);

        return problem;
    }

    // 6. Handle "Catch-All" (500)
    // NEVER return the stack trace to the client in production!
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception e) {
        // Log the full error here using Slf4j (not shown for brevity)
        e.printStackTrace();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support."
        );
        problem.setTitle("Internal Server Error");
        return problem;
    }
}
