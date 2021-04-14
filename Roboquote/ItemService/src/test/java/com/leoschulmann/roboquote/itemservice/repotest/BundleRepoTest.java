package com.leoschulmann.roboquote.itemservice.repotest;

import com.leoschulmann.roboquote.itemservice.config.TestJpaConfig;
import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.Item;
import com.leoschulmann.roboquote.itemservice.entities.projections.BundleWithoutPositions;
import com.leoschulmann.roboquote.itemservice.repositories.BundleRepository;
import com.leoschulmann.roboquote.itemservice.repositories.ItemRepository;
import com.leoschulmann.roboquote.itemservice.util.TestFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TestJpaConfig.class})
public class BundleRepoTest {

    @Autowired
    BundleRepository bundleRepository;
    @Autowired
    ItemRepository itemRepository;


    @BeforeAll
    void prepare() {
        List<Item> items = TestFactory.itemFactory("coffee", "beer", "cocoa", "cola", "juice");
        itemRepository.saveAll(items);

        List<Bundle> bundles = List.of(
                TestFactory.bundleFactory("bundle1", items.get(0), items.get(1), items.get(2)),
                TestFactory.bundleFactory("bundle2", items.get(3), items.get(4)),
                TestFactory.bundleFactory("bundle3", items.get(0), items.get(2), items.get(4))
        );
        bundleRepository.saveAll(bundles);
    }

    @Test
    void sanityCheck() {
        assertNotNull(bundleRepository);
        assertNotNull(itemRepository);
    }

    @Test
    void testGetOnlyIdsAndNames() {
        List<BundleWithoutPositions> dtos = bundleRepository.getAllBundlesNamesAndIds();
        assertNotNull(dtos.get(0).getNameRus());
        assertNotNull(dtos.get(0).getNameEng());
        assertNotNull(dtos.get(0).getId());
    }
}
