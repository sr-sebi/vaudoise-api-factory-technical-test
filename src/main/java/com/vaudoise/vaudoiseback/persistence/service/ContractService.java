package com.vaudoise.vaudoiseback.persistence.service;

import com.vaudoise.vaudoiseback.exception.CustomException;
import com.vaudoise.vaudoiseback.exception.ErrorEnum;
import com.vaudoise.vaudoiseback.persistence.entities.Client;
import com.vaudoise.vaudoiseback.persistence.entities.Contract;
import com.vaudoise.vaudoiseback.persistence.repositories.ClientRepository;
import com.vaudoise.vaudoiseback.persistence.repositories.ContractRepository;
import com.vaudoise.vaudoiseback.rest.dto.ContractRequest;
import com.vaudoise.vaudoiseback.rest.dto.ContractResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ContractService extends BaseJpaPersistence<ContractRepository, Contract, Long> {

    private final ContractRepository contractRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository, ClientRepository clientRepository) {
        super(contractRepository);
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
    }

    // ---------------- Browse ----------------
    @Transactional(readOnly = true)
    public Page<ContractResponse> browse(Long clientId, Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort());
        }

        List<Contract> contracts;
        if (clientId != null) {
            contracts = contractRepository.findByClientId(clientId);
        } else {
            contracts = contractRepository.findAll();
        }

        return new PageImpl<>(
                contracts.stream().map(ContractResponse::new).toList(),
                pageable,
                contracts.size()
        );
    }

    @Transactional(readOnly = true)
    public Page<ContractResponse> getActiveContractsByClientId(Long clientId, LocalDate updatedAfter, LocalDate updatedBefore, Pageable pageable) {
        Specification<Contract> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            predicates.getExpressions().add(
                    cb.or(
                            cb.isNull(root.get("endDate")),
                            cb.greaterThan(root.get("endDate"), LocalDate.now())
                    )
            );

            predicates.getExpressions().add(cb.equal(root.get("client").get("id"), clientId));

            if (updatedAfter != null) {
                predicates.getExpressions().add(cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter.atStartOfDay()));
            }

            if (updatedBefore != null) {
                predicates.getExpressions().add(cb.lessThanOrEqualTo(root.get("updatedAt"), updatedBefore.atTime(23, 59, 59)));
            }

            return predicates;
        };

        return contractRepository.findAll(spec, pageable).map(ContractResponse::new);
    }

    // ---------------- Read ----------------
    @Transactional(readOnly = true)
    public ContractResponse read(Long id) throws CustomException {
        Contract contract = findById(id);
        return new ContractResponse(contract);
    }

    // ---------------- Add ----------------
    @Transactional
    public ContractResponse add(ContractRequest request) throws CustomException {
        validateContract(request, false);

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new CustomException(ErrorEnum.CLIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));

        Contract contract = new Contract();
        contract.setClient(client);

        // Set defaults if not provided
        contract.setStartDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now());
        contract.setEndDate(request.getEndDate()); // null is fine
        contract.setCost(request.getCost());

        contract = contractRepository.save(contract);
        return new ContractResponse(contract);
    }

    // ---------------- Update ----------------
    @Transactional
    public ContractResponse update(Long id, ContractRequest request) throws CustomException {
        validateContract(request, true);

        Contract contract = findById(id);

        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new CustomException(ErrorEnum.CLIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
            contract.setClient(client);
        }

        // Update startDate only if provided; otherwise keep existing
        contract.setStartDate(request.getStartDate() != null ? request.getStartDate() : contract.getStartDate());
        // Update endDate (null allowed)
        contract.setEndDate(request.getEndDate());
        contract.setCost(request.getCost());

        contract = contractRepository.saveAndFlush(contract);
        return new ContractResponse(contract);
    }

    // ---------------- Delete ----------------
    @Transactional
    public ContractResponse delete(Long id) throws CustomException {
        Contract contract = findById(id);
        contractRepository.deleteById(contract.getId());
        return new ContractResponse(contract);
    }

    // ---------------- Helper ----------------
    @Transactional(readOnly = true)
    public Contract findById(Long id) throws CustomException {
        return contractRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorEnum.CONTRACT_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    private void validateContract(ContractRequest request, Boolean updating) throws CustomException {
        if (request.getStartDate() == null) {
            throw new CustomException(ErrorEnum.CONTRACT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new CustomException(ErrorEnum.CONTRACT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (request.getCost() == null || request.getCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ErrorEnum.CONTRACT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (request.getClientId() == null) {
            throw new CustomException(ErrorEnum.CONTRACT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(updating)) {
            if (request.getUuid() == null) {
                throw new CustomException(ErrorEnum.CONTRACT_VALIDATION, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
