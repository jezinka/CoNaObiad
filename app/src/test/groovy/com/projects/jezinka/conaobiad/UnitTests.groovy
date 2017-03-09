package com.projects.jezinka.conaobiad

import org.spockframework.util.Assert
import spock.lang.Specification

class UnitTests extends Specification {

    def "GetSaturdayDate"() {

        setup:
        DinnerListHelper dinnerListHelper = new DinnerListHelper()

        when:
        Date resultDate = dinnerListHelper.getSaturdayDate(date)

        then:
        Assert.notNull(resultDate)
        resultDate == expectedDate

        where:
        date                 | expectedDate
        new Date(117, 2, 4)  | new Date(117, 2, 4)
        new Date(117, 2, 13) | new Date(117, 2, 11)
        new Date(117, 2, 11) | new Date(117, 2, 11)
        new Date(117, 2, 12) | new Date(117, 2, 11)
        new Date(117, 2, 14) | new Date(117, 2, 11)
        new Date(117, 2, 15) | new Date(117, 2, 11)
        new Date(117, 2, 16) | new Date(117, 2, 11)
        new Date(117, 2, 17) | new Date(117, 2, 11)

        new Date(117, 2, 3)  | new Date(117, 1, 25)
        new Date(117, 0, 1)  | new Date(116, 11, 31)
    }

    def "GetSaturdayDate no exception thrown for null parameter"() {

        setup:
        DinnerListHelper dinnerListHelper = new DinnerListHelper()

        when:
        dinnerListHelper.getSaturdayDate()

        then:
        noExceptionThrown()
    }
}
