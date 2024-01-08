package com.example.empay.service.transaction.impl;

import com.example.empay.dto.transaction.TransactionStatusTypeDto;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.repository.transaction.TransactionStatusTypeRepository;
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
public class TransactionStatusTypeServiceTests {

    @Mock
    TransactionStatusTypeRepository transactionStatusTypeRepository;

    @InjectMocks
    TransactionStatusTypeServiceImpl transactionStatusTypeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Find all")
    @Test
    public void findAll() {
        TransactionStatusType transactionStatusType = new TransactionStatusType()
                .setId(TransactionStatusType.TYPE.APPROVED.name())
                .setName("Approved");

        List<TransactionStatusType> transactionStatusTypeList = Collections.singletonList(transactionStatusType);

        Mockito.when(transactionStatusTypeRepository.findAll()).thenReturn(transactionStatusTypeList);

        Collection<TransactionStatusTypeDto> transactionStatusTypeDtoList = transactionStatusTypeService.findAll();

        Assertions.assertNotNull(transactionStatusTypeDtoList);
        Assertions.assertEquals(1, transactionStatusTypeDtoList.size());
        TransactionStatusTypeDto firstResult = transactionStatusTypeDtoList.iterator().next();
        Assertions.assertEquals(transactionStatusType.getId(), firstResult.getId());
        Assertions.assertEquals(transactionStatusType.getName(), firstResult.getName());
    }
}
