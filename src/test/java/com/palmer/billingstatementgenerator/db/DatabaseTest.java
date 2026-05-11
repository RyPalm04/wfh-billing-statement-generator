package com.palmer.billingstatementgenerator.db;

import com.palmer.billingstatementgenerator.dao.CashAdvanceDao;
import com.palmer.billingstatementgenerator.dao.MerchandiseDao;
import com.palmer.billingstatementgenerator.dao.ServiceDao;
import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.dao.SpecialChargeDao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseTest {
    @Test
    void initializesAndSeedsCatalog() {
        Database.init();
        assertEquals(7,  new ServicePackageDao(Database.get()).findAll().size());
        assertEquals(13, new ServiceDao(Database.get()).findAll().size());
        assertEquals(13, new MerchandiseDao(Database.get()).findAll().size());
        assertEquals(8,  new SpecialChargeDao(Database.get()).findAll().size());
        assertEquals(20, new CashAdvanceDao(Database.get()).findAll().size());
    }
}
