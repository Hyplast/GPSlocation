package fi.infinitygrow.gpslocation.presentation.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test


class CalculationsKtTest {

    @Test
    fun testgetDistance() {

        assertThat(getDistance(60.0,24.0,50.0,25.0)).isEqualTo(1113.7437598354588)
    }

    @Test
    fun testgetDistance999Error() {
        assertThat(getDistance(60.0,24.0,999.9,999.9)).isEqualTo(16783.243124217133)
    }

    @Test
    fun testgetDistance999Errorx2() {
        assertThat(getDistance(999.9,999.9,999.9,999.9)).isEqualTo(0.0)
    }

    @Test
    fun testgetBearing() {
        assertThat(getBearing(60.0,24.0,999.9,999.9)).isEqualTo(200.07495347933263)
    }

}

