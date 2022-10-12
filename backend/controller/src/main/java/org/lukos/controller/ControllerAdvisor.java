package org.lukos.controller;

import org.lukos.controller.response.ErrorResponse;
import org.lukos.model.exceptions.NoPermissionException;
import org.lukos.model.exceptions.instances.GameAlreadyStartedException;
import org.lukos.model.exceptions.instances.NoSuchInstanceException;
import org.lukos.model.exceptions.instances.NotEnoughPlayersException;
import org.lukos.model.exceptions.location.BridgeDoesNotExistException;
import org.lukos.model.exceptions.location.HouseDoesNotExistException;
import org.lukos.model.exceptions.user.AlreadyInGameException;
import org.lukos.model.exceptions.user.NoSuchPlayerException;
import org.lukos.model.exceptions.user.NoSuchUserException;
import org.lukos.model.exceptions.voting.NoSuchVoteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

/**
 * The controller that handles the errors that are thrown by the application.
 *
 * @author Xander Smeets (1325523)
 * @author Rick van der Heijden (1461923)
 * @author Marco Pleket (1295713)
 * @since 14-03-2022
 */
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    /**
     * ExceptionHandler for the {@link NoSuchUserException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NoSuchUserException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NoSuchUserException}.
     */
    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchUserException(NoSuchUserException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link NoSuchPlayerException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NoSuchPlayerException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NoSuchPlayerException}.
     */
    @ExceptionHandler(NoSuchPlayerException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchPlayerException(NoSuchPlayerException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link NoPermissionException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NoPermissionException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NoPermissionException}.
     */
    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchUserException(NoPermissionException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link NullPointerException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NullPointerException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NullPointerException}.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link AlreadyInGameException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code AlreadyInGameException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * AlreadyInGameException}.
     */
    @ExceptionHandler(AlreadyInGameException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyInGameException(AlreadyInGameException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link NoSuchInstanceException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NoSuchInstanceException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NoSuchInstanceException}.
     */
    @ExceptionHandler(NoSuchInstanceException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchInstanceException(NoSuchInstanceException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ExceptionHandler for the {@link NotEnoughPlayersException}.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NotEnoughPlayersException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code
     * NotEnoughPlayersException}.
     */
    @ExceptionHandler(NotEnoughPlayersException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughPlayersException(NotEnoughPlayersException e,
                                                                         WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for all SQLExceptions that we get by communicating with the database.
     *
     * @param e       the exception
     * @param request the request that caused the {@code SQLException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code SQLException}.
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for all SQLExceptions that we get by communicating with the database.
     *
     * @param e       the exception
     * @param request the request that caused the {@code NoSuchVoteException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code NoSuchVoteException}.
     */
    @ExceptionHandler(NoSuchVoteException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(NoSuchVoteException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for all GameAlreadyStartedExceptions that we get by communicating with the database.
     *
     * @param e       the exception
     * @param request the request that caused the {@code GameAlreadyStartedException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code GameAlreadyStartedException}.
     */
    @ExceptionHandler(GameAlreadyStartedException.class)
    public ResponseEntity<ErrorResponse> handleGameStartedException(GameAlreadyStartedException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for all HouseDoesNotExistException that we get by communicating with the database.
     *
     * @param e       the exception
     * @param request the request that caused the {@code HouseDoesNotExistException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code HouseDoesNotExistException}.
     */
    @ExceptionHandler(HouseDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleHouseException(HouseDoesNotExistException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for all BridgeDoesNotExistExceptions that we get by communicating with the database.
     *
     * @param e       the exception
     * @param request the request that caused the {@code BridgeDoesNotExistException}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code BridgeDoesNotExistException}.
     */
    @ExceptionHandler(BridgeDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleBridgeException(BridgeDoesNotExistException e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<ErrorResponse> handleStackOverFlowException(StackOverflowError e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * {@code ExceptionHandler} for the all exceptions that are not specifically defined to be handled.
     *
     * @param e       the exception
     * @param request the request that caused the {@code Exception}
     * @return a {@code ResponseEntity} with the {@code ErrorResponse} in accordance with the {@code Exception}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
