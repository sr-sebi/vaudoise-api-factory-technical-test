package com.vaudoise.vaudoiseback.rest;

import com.vaudoise.vaudoiseback.exception.CustomException;
import com.vaudoise.vaudoiseback.exception.ErrorEnum;
import com.vaudoise.vaudoiseback.persistence.service.ContractService;
import com.vaudoise.vaudoiseback.rest.dto.ContractRequest;
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

@RestController
@RequestMapping("/api/v1/contracts")
@Slf4j
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @Operation(
            summary = "List contracts",
            description = "Returns a page of contracts, optionally filtered by client ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A page of contracts"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If contracts cannot be retrieved due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ContractResponse>> browse(
            @RequestParam(value = "clientId", required = false) Long clientId,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) throws CustomException {
        try {
            return ResponseEntity.ok(contractService.browse(clientId, pageable));
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CONTRACT_LIST, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Fetch an existing contract",
            description = "Fetches a contract by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fetched contract",
                    content = {@Content(schema = @Schema(implementation = ContractResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If contract with given ID does not exist",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @GetMapping(value = "/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractResponse> read(@PathVariable Long contractId) throws CustomException {
        try {
            return ResponseEntity.ok(contractService.read(contractId));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CONTRACT_READ, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Create a new contract",
            description = "Creates a contract for a client"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Contract created successfully",
                    content = {@Content(schema = @Schema(implementation = ContractResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If contract cannot be created due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractResponse> add(@RequestBody ContractRequest request) throws CustomException {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(contractService.add(request));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CONTRACT_CREATE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Update an existing contract",
            description = "Updates a contract"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contract updated successfully",
                    content = {@Content(schema = @Schema(implementation = ContractResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If the contract is not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If the contract cannot be updated due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @PutMapping(value = "/{contractId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractResponse> update(@PathVariable Long contractId, @RequestBody ContractRequest request) throws CustomException {
        try {
            return ResponseEntity.ok(contractService.update(contractId, request));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CONTRACT_UPDATE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
            summary = "Delete a contract",
            description = "Deletes a contract by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contract deleted successfully",
                    content = {@Content(schema = @Schema(implementation = ContractResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code - If the contract is not found",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code - If the contract cannot be deleted due to an internal error",
                    content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE)})
    })
    @DeleteMapping(value = "/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContractResponse> delete(@PathVariable Long contractId) throws CustomException {
        try {
            return ResponseEntity.ok(contractService.delete(contractId));
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CustomException(ErrorEnum.CONTRACT_DELETE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
