package com.oa.poll.service;

import com.oa.poll.entity.Veggie;
import com.oa.poll.exceptions.DbConfigException;
import com.oa.poll.repository.VeggieRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class DbLoadConfigTest {
    @Mock
    private VeggieRepo veggieRepo;

    DbLoadConfig dbLoadConfig;

    private static List<Veggie> veggiesAfterConstruction;
    private static List<Integer> veggieKeysAfterConstruction;

    @BeforeEach
    void init() {
        Veggie veggie1 = Veggie.builder().id(1).name_et("Porgand").name_en("Carrot").likeCount(0).dislikeCount(0).build();
        Veggie veggie2 = Veggie.builder().id(2).name_et("Kartul").name_en("Potato").likeCount(0).dislikeCount(0).build();
        List<Veggie> veggiesInDB = List.of(veggie1, veggie2);
        given(veggieRepo.findAll()).willReturn(veggiesInDB);
        dbLoadConfig = new DbLoadConfig(veggieRepo);
        verify(veggieRepo, times(1)).findAll();
        veggiesAfterConstruction = List.of(veggie1, veggie2);
        veggieKeysAfterConstruction = List.of(1, 2);
    }

    @Test
    void checkVeggiesAfterConstruction() {
        assertEquals(veggiesAfterConstruction, dbLoadConfig.getVeggies());
    }

    @Test
    void veggiesAreLoaded() {
        Veggie veggie1 = Veggie.builder().id(1).name_et("Tomat").name_en("Tomato").likeCount(0).dislikeCount(0).build();
        Veggie veggie2 = Veggie.builder().id(2).name_et("Kartul").name_en("Potato").likeCount(0).dislikeCount(0).build();
        Veggie veggie3 = Veggie.builder().id(3).name_et("Porrulauk").name_en("Leek").likeCount(0).dislikeCount(0).build();
        Veggie veggie4 = Veggie.builder().id(4).name_et("Porgand").name_en("Carrot").likeCount(0).dislikeCount(0).build();
        Veggie veggie5 = Veggie.builder().id(5).name_et("Pastinaak").name_en("Parsnip").likeCount(0).dislikeCount(0).build();
        Veggie veggie6 = Veggie.builder().id(6).name_et("Fenkol").name_en("Fennel").likeCount(0).dislikeCount(0).build();
        List<Veggie> veggiesInDB = List.of(veggie1, veggie2, veggie3, veggie4, veggie5, veggie6);
        given(veggieRepo.findAll()).willReturn(veggiesInDB);
        List<Veggie> loadedVeggies = dbLoadConfig.loadVeggies();
        assertEquals(veggiesInDB, loadedVeggies);
    }

    @Test
    void loadVeggiesFailsWhenNoVeggiesInDB() {
        List<Veggie> veggiesInDB = new ArrayList<>();
        given(veggieRepo.findAll()).willReturn(veggiesInDB);
        String exMessage = assertThrows(DbConfigException.class, () -> dbLoadConfig.loadVeggies()).getMessage();
        assertEquals("DbLoadConfig.loadVeggies(): No veggies defined. Probably a bad DB configuration. " +
                "Check data.sql", exMessage);
    }

    @Test
    void veggieKeysAreLoadedAfterConstruction() {
        List<Integer> loadedKeys = dbLoadConfig.loadVeggieKeys();
        assertEquals(veggieKeysAfterConstruction, loadedKeys);
    }
}
