package com.projects.jezinka.conaobiad

import spock.lang.Specification

import java.text.DateFormat

/**
 * Created by jezinka on 03.03.17.
 */
class UnitTests extends Specification {

    def "GetSaturdayDate"() {

        setup:
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("pl", "pl"))
        MealListHelper mealListHelper = new MealListHelper()

        when:
        Date resultDate = mealListHelper.getSaturdayDate(date).getTime()

        then:
        df.format(resultDate) == df.format(expectedDate)

        where:
        date                | expectedDate
        new Date(117, 2, 3) | new Date(117, 1, 25)
        new Date(117, 2, 4) | new Date(117, 2, 4)
        new Date(117, 2, 9) | new Date(117, 2, 4)
        new Date(117, 0, 1) | new Date(116, 11, 31)

    }
}
