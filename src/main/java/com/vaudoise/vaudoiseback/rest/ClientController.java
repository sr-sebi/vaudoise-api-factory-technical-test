package com.vaudoise.vaudoiseback.rest;

import com.vaudoise.vaudoiseback.exception.CustomException;
import com.vaudoise.vaudoiseback.exception.ErrorEnum;
import com.vaudoise.vaudoiseback.persistence.service.ClientService;
import com.vaudoise.vaudoiseback.rest.dto.ClientRequest;
import com.vaudoise.vaudoiseback.rest.dto.ClientResponse;
import com.vaudoise.vaudoiseback.rest.dto.ContractResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(
            summary = "List clients",
            description = "Returns a page of clients, filtered by query if provided"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of clients"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If clients cannot be retrieved due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClientResponse>> browse(
            @RequestParam(value = "query", required = false) String query,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) throws CustomException {
        try {
            return ResponseEntity.ok(clientService.browse(query, pageable));
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_LIST, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "List active contracts for a client",
            description = "Returns a page of active contracts for a specific client, optionally filtered by update date range"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A page of active contracts for the client"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If contracts cannot be retrieved due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping(value = "/{clientId}/contracts/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ContractResponse>> getActiveContracts(
            @PathVariable Long clientId,
            @RequestParam(value = "updatedAfter", required = false) LocalDate updatedAfter,
            @RequestParam(value = "updatedBefore", required = false) LocalDate updatedBefore,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) throws CustomException {
        try {
            return ResponseEntity.ok(clientService.getActiveContracts(clientId, updatedAfter, updatedBefore, pageable));
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_CONTRACT_LIST, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Fetch an existing client",
            description = "Fetches an existing client and returns it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetched client",
                    content = {@Content(schema = @Schema(implementation = ClientResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If the client is not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping(value = "/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> read(@PathVariable Long clientId) throws CustomException {
        try {
            return ResponseEntity.ok(clientService.read(clientId));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_READ, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Create a new client",
            description = "Creates a new client and returns it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Client created successfully",
                    content = {@Content(schema = @Schema(implementation = ClientResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If the client cannot be created due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> add(@RequestBody ClientRequest request) throws CustomException {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(clientService.add(request));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_CREATE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Update an existing client",
            description = "Updates an existing client and returns it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Client updated successfully",
                    content = {@Content(schema = @Schema(implementation = ClientResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If the client is not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If the client cannot be updated due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @PutMapping(value = "/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> update(@PathVariable Long clientId, @RequestBody ClientRequest request) throws CustomException {
        try {
            return ResponseEntity.ok(clientService.update(clientId, request));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_UPDATE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Delete a client",
            description = "Deletes an existing client and returns it"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Client deleted successfully",
                    content = {@Content(schema = @Schema(implementation = ClientResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If the client is not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If the client cannot be deleted due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @DeleteMapping(value = "/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> delete(@PathVariable Long clientId) throws CustomException {
        try {
            return ResponseEntity.ok(clientService.delete(clientId));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CLIENT_DELETE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
