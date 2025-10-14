package com.vaudoise.vaudoiseback.persistence.service;

import com.vaudoise.vaudoiseback.exception.CustomException;
import com.vaudoise.vaudoiseback.exception.ErrorEnum;
import com.vaudoise.vaudoiseback.persistence.entities.Client;
import com.vaudoise.vaudoiseback.persistence.entities.CompanyClient;
import com.vaudoise.vaudoiseback.persistence.entities.PersonClient;
import com.vaudoise.vaudoiseback.persistence.entities.enums.ClientType;
import com.vaudoise.vaudoiseback.persistence.repositories.ClientRepository;
import com.vaudoise.vaudoiseback.rest.dto.ClientRequest;
import com.vaudoise.vaudoiseback.rest.dto.ClientResponse;
import com.vaudoise.vaudoiseback.rest.dto.ContractResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class ClientService extends BaseJpaPersistence<ClientRepository, Client, Long> {

    private final ClientRepository clientRepository;
    private final ContractService contractService;

    @Autowired
    public ClientService(ClientRepository clientRepository, ContractService contractService) {
        super(clientRepository);
        this.clientRepository = clientRepository;
        this.contractService = contractService;
    }

    // ---------------- Browse ----------------
    @Transactional(readOnly = true)
    public Page<ClientResponse> browse(String searchFilter, Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort());
        }

        if (!StringUtils.hasText(searchFilter)) {
            return clientRepository.findAll(pageable).map(ClientResponse::new);
        }

        Specification<Client> spec = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + searchFilter.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("email")), "%" + searchFilter.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("phone")), "%" + searchFilter.toLowerCase() + "%")
        );

        return clientRepository.findAll(spec, pageable).map(ClientResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<ContractResponse> getActiveContracts(Long clientId, LocalDate updatedAfter, LocalDate updatedBefore, Pageable pageable) throws CustomException {
        Client client = findById(clientId);

        return contractService.getActiveContractsByClientId(client.getId(), updatedAfter, updatedBefore, pageable);
    }

    @Transactional(readOnly = true)
    public BigDecimal getSumOfActiveContracts(Long clientId) throws CustomException {
        Client client = findById(clientId);

        return contractService.getSumOfActiveContractsByClientId(client.getId());
    }

    // ---------------- Read ----------------
    @Transactional(readOnly = true)
    public ClientResponse read(Long id) throws CustomException {
        Client client = findById(id);
        return new ClientResponse(client);
    }

    // ---------------- Add ----------------
    @Transactional
    public ClientResponse add(ClientRequest request) throws CustomException {
        validateClient(request, false);

        Client client;
        if (request.getType().equals(ClientType.PERSON)) {
            PersonClient person = new PersonClient();
            person.setBirthDate(request.getBirthDate());
            client = person;
        } else if (request.getType().equals(ClientType.COMPANY)) {
            CompanyClient company = new CompanyClient();
            company.setCompanyId(request.getCompanyId());
            client = company;
        } else {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        client = clientRepository.save(client);
        return new ClientResponse(client);
    }

    // ---------------- Update ----------------
    @Transactional
    public ClientResponse update(Long id, ClientRequest request) throws CustomException {
        validateClient(request, true);

        Client client = findById(id);
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        if (client instanceof PersonClient person && request.getType().equals(ClientType.PERSON)) {
            person.setBirthDate(request.getBirthDate());
        } else if (client instanceof CompanyClient company && request.getType().equals(ClientType.COMPANY)) {
            company.setCompanyId(request.getCompanyId());
        } else {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        client = clientRepository.saveAndFlush(client);
        return new ClientResponse(client);
    }

    // ---------------- Delete ----------------
    @Transactional
    public ClientResponse delete(Long id) throws CustomException {
        Client client = findById(id);
        clientRepository.delete(client.getId());
        return new ClientResponse(client);
    }

    // ---------------- Helper ----------------
    @Transactional(readOnly = true)
    public Client findById(Long id) throws CustomException {
        return clientRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorEnum.CLIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    private void validateClient(ClientRequest request, boolean updating) throws CustomException {
        if (!StringUtils.hasText(request.getName()) ||
                !StringUtils.hasText(request.getEmail()) ||
                !StringUtils.hasText(request.getPhone())) {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!request.getEmail().matches(emailRegex)) {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        String phoneRegex = "^\\+?[0-9]{7,15}$";
        if (!request.getPhone().matches(phoneRegex)) {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (request.getType().equals(ClientType.PERSON)) {
            if (request.getBirthDate() == null) {
                throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
            }
            if (request.getBirthDate().isAfter(LocalDate.now())) {
                throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getType().equals(ClientType.COMPANY) && !StringUtils.hasText(request.getCompanyId())) {
            throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
        }

        if (Boolean.TRUE.equals(updating)) {
            if (request.getUuid() == null) {
                throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
            }
        } else {
            if (clientRepository.existsByEmail(request.getEmail())) {
                throw new CustomException(ErrorEnum.CLIENT_VALIDATION, HttpStatus.BAD_REQUEST);
            }
        }
    }
}
