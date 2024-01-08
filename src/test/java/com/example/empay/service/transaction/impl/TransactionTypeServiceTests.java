package com.example.empay.service.transaction.impl;

import com.example.empay.dto.transaction.TransactionTypeDto;
import com.example.empay.entity.transaction.TransactionType;
import com.example.empay.repository.transaction.TransactionTypeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionTypeServiceTests {

    @Mock
    TransactionTypeRepository transactionTypeRepository;

    @InjectMocks
    TransactionTypeServiceImpl transactionTypeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Find all")
    @Test
    public void findAll() {
        TransactionType transactionType = new TransactionType()
                .setId(TransactionType.TYPE.CHARGE.name())
                .setName("Charge");

        List<TransactionType> transactionTypeList = Collections.singletonList(transactionType);

        Mockito.when(transactionTypeRepository.findAll()).thenReturn(transactionTypeList);

        Collection<TransactionTypeDto> transactionTypeDtoList = transactionTypeService.findAll();

        Assertions.assertNotNull(transactionTypeDtoList);
        Assertions.assertEquals(1, transactionTypeDtoList.size());
        TransactionTypeDto firstResult = transactionTypeDtoList.iterator().next();
        Assertions.assertEquals(transactionType.getId(), firstResult.getId());
        Assertions.assertEquals(transactionType.getName(), firstResult.getName());
    }
}
