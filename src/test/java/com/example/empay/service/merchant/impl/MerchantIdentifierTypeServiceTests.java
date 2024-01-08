package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.entity.merchant.MerchantIdentifierType;
import com.example.empay.repository.merchant.MerchantIdentifierTypeRepository;
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
public class MerchantIdentifierTypeServiceTests {

    @Mock
    MerchantIdentifierTypeRepository merchantIdentifierTypeRepository;

    @InjectMocks
    MerchantIdentifierTypeServiceImpl merchantIdentifierTypeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Find all")
    @Test
    public void findAll() {
        MerchantIdentifierType merchantIdentifierType = new MerchantIdentifierType()
                .setId("EIK_BG")
                .setName("EIK (BG)");

        List<MerchantIdentifierType> merchantIdentifierTypeList = Collections.singletonList(merchantIdentifierType);

        Mockito.when(merchantIdentifierTypeRepository.findAll()).thenReturn(merchantIdentifierTypeList);

        Collection<MerchantIdentifierTypeDto> resultList = merchantIdentifierTypeService.findAll();

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.size());
        MerchantIdentifierTypeDto firstResult = resultList.iterator().next();
        Assertions.assertEquals(merchantIdentifierType.getId(), firstResult.getId());
        Assertions.assertEquals(merchantIdentifierType.getName(), firstResult.getName());
    }
}
