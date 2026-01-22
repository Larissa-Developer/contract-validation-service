package com.larissa.contracts.infrastructure.persistence.memory;

import com.larissa.contracts.domain.model.Contract;
import com.larissa.contracts.domain.port.ContractRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryContractRepository implements ContractRepository {

    private final List<Contract> database = new ArrayList<>();

    @Override
    public void save(Contract contract) {
        database.add(contract);
    }
}

