package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.repository.merchant.MerchantStatusTypeRepository;
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
public class MerchantStatusTypeServiceTests {

    @Mock
    MerchantStatusTypeRepository merchantStatusTypeRepository;

    @InjectMocks
    MerchantStatusTypeServiceImpl merchantStatusTypeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Find all")
    @Test
    public void findAll() {
        MerchantStatusType merchantStatusType = new MerchantStatusType()
                .setId(MerchantStatusType.STATUS.ACTIVE.name())
                .setName("Active");

        List<MerchantStatusType> merchantStatusTypeList = Collections.singletonList(merchantStatusType);

        Mockito.when(merchantStatusTypeRepository.findAll()).thenReturn(merchantStatusTypeList);

        Collection<MerchantStatusTypeDto> resultList = merchantStatusTypeService.findAll();

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.size());
        MerchantStatusTypeDto firstResult = resultList.iterator().next();
        Assertions.assertEquals(merchantStatusType.getId(), firstResult.getId());
        Assertions.assertEquals(merchantStatusType.getName(), firstResult.getName());
    }
}
