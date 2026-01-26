package com.larissa.contracts.domain.port;

import com.larissa.contracts.domain.model.Contract;

public interface ContractRepository {

    void save(Contract contract);
}
