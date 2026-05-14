package com.palmer.billingstatementgenerator.db

import com.palmer.billingstatementgenerator.dao.CashAdvanceDao
import com.palmer.billingstatementgenerator.dao.MerchandiseDao
import com.palmer.billingstatementgenerator.dao.ServiceDao
import com.palmer.billingstatementgenerator.dao.ServicePackageDao
import com.palmer.billingstatementgenerator.dao.SpecialChargeDao
import spock.lang.Specification

class DatabaseSpec extends Specification {

    def "initializes and seeds catalog"() {
        given:
        TestDatabase.init()

        expect:
        new ServicePackageDao(Database.get()).findAll().size() == 7
        new ServiceDao(Database.get()).findAll().size() == 13
        new MerchandiseDao(Database.get()).findAll().size() == 13
        new SpecialChargeDao(Database.get()).findAll().size() == 8
        new CashAdvanceDao(Database.get()).findAll().size() == 20
    }
}
