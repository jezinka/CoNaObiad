package com.projects.jezinka.conaobiad

import com.projects.jezinka.conaobiad.activity.MainActivity
import org.spockframework.util.Assert
import spock.lang.Specification

class UnitTests extends Specification {

    def "GetSaturdayDate"() {

        setup:
        MainActivity.firstDayOfWeek = 7
        MainActivity.planLength = 7

        when:
        Date resultDate = TimeUtils.getWeekStartDate(date)

        then:
        Assert.notNull(resultDate)
        resultDate == expectedDate

        where:
        date                            | expectedDate
        new Date(117, 2, 4)             | new Date(117, 2, 4)
        new Date(117, 2, 13)            | new Date(117, 2, 11)
        new Date(117, 2, 11)            | new Date(117, 2, 11)
        new Date(117, 2, 12)            | new Date(117, 2, 11)
        new Date(117, 2, 14)            | new Date(117, 2, 11)
        new Date(117, 2, 15)            | new Date(117, 2, 11)
        new Date(117, 2, 16)            | new Date(117, 2, 11)
        new Date(117, 2, 17)            | new Date(117, 2, 11)

        new Date(117, 2, 3)             | new Date(117, 1, 25)
        new Date(117, 0, 1)             | new Date(116, 11, 31)

        new Date(117, 3, 8, 11, 12, 32) | new Date(117, 3, 8, 0, 0, 0)
    }

    def "GetSaturdayDate no exception thrown for null parameter"() {

        when:
        TimeUtils.getWeekStartDate()

        then:
        noExceptionThrown()
    }

    def "GetSaturdayDate time always cleared"() {
        when:
        def result = TimeUtils.getWeekStartDate()
        Calendar calendarInstance = Calendar.getInstance()
        calendarInstance.setTime(result)

        then:
        noExceptionThrown()
        assert calendarInstance.get(Calendar.HOUR) == 0
        assert calendarInstance.get(Calendar.MINUTE) == 0
        assert calendarInstance.get(Calendar.SECOND) == 0
    }
}
