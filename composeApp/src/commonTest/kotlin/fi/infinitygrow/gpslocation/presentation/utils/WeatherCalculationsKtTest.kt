import assertk.assertThat
import assertk.assertions.isEqualTo
import fi.infinitygrow.gpslocation.presentation.utils.altcalc
import fi.infinitygrow.gpslocation.presentation.utils.calcSeaLevelTemperature
import fi.infinitygrow.gpslocation.presentation.utils.calcTemperatureAtAltitude
import fi.infinitygrow.gpslocation.presentation.utils.calculateAltitude
import fi.infinitygrow.gpslocation.presentation.utils.pressTempAlt
import fi.infinitygrow.gpslocation.presentation.utils.pressureFromAltitude
import fi.infinitygrow.gpslocation.presentation.utils.pressureTemperatureAltitude
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.test.Test

class WeatherCalculationsKtTest {

    @Test
    fun testFL65altitude() {


        assertThat(calculateAltitude(25+273.15, 6500.0, 101315.0)).isEqualTo(1111)
    }


    @Test
    fun test0mtoPressure() {

        assertThat(pressureFromAltitude(0.0, 273.15+25)).isEqualTo(101325.0)
    }

    @Test
    fun test1000mtoPressure() {

        assertThat(pressureFromAltitude(3000.0, (273.15+25)).toInt()).isEqualTo(70142) // Actual   :67645.04438249687
    }

    @Test
    fun test1981mtoPressure() {

        assertThat(pressureFromAltitude(1981.20, (273.15+15)).toInt()).isEqualTo(79680.95) // Actual   :67645.04438249687
                                                                                       //  79680.95
    }

    @Test
    fun testpressTempAlt() {

        assertThat(pressTempAlt(101325.0, 273.15+15.0, 1981.20).roundToInt()).isEqualTo(79681)

    }
    @Test
    fun testaltcalc() {

        assertThat(altcalc(101325.0, 273.15+15.0, 79680.95).roundToInt()).isEqualTo(1981)

    }

    @Test
    fun testalpressureTemperatureAltitude() {

        assertThat(pressureTemperatureAltitude(101325.0, 273.15+15.0, 1981.20)).isEqualTo(1981)

    }


//    Expected :79680.95
//    Actual   :79237
//
    @Test
    fun testSeaLevelTemperature() {
        assertThat(calcSeaLevelTemperature(17.0, 900.0)).isEqualTo(22.85)
    }

    @Test
    fun testSeaLevelTemperatureSame() {
        assertThat(calcSeaLevelTemperature(17.0, 0.0)).isEqualTo(17.0)
    }

    @Test
    fun testCalcTemperatureAtAltitude() {
        assertThat(calcTemperatureAtAltitude(0.0,0.0,25.0)).isEqualTo(25.0)
    }

    @Test
    fun testCalcTemperatureAtAltitude100m() {
        assertThat(calcTemperatureAtAltitude(900.0,0.0,22.85)).isEqualTo(17.0)
    }

    @Test
    fun testCalcTemperatureAtAltitude900m2() {
        assertThat(calcTemperatureAtAltitude(900.0,450.0,20.0)).isEqualTo(17.075)
    }

    @Test
    fun testCalcTemperatureAtAltitude450m2() {
        assertThat(calcTemperatureAtAltitude(450.0,0.0,20.0)).isEqualTo(17.075)
    }


    // 79680.95 Pa at Calculate Air Pressure at Altitude 101325 Pa - 15 C - 6500 ft
    // 71044.33 Pa at Calculate Air Pressure at Altitude 101325 Pa - 15 C - 9500 ft
    @Test
    fun testcalculateAltitude() {
        assertThat(calculateAltitude(15+273.15, 1981.20, 101325.0)).isEqualTo(1981.20)
    }
}

