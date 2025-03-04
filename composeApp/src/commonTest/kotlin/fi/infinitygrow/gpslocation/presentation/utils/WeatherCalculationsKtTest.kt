import assertk.assertThat
import assertk.assertions.isEqualTo
import fi.infinitygrow.gpslocation.presentation.utils.altcalc
import fi.infinitygrow.gpslocation.presentation.utils.calcSeaLevelTemperature
import fi.infinitygrow.gpslocation.presentation.utils.calcTemperatureAtAltitude
import fi.infinitygrow.gpslocation.presentation.utils.calculateAltitude
import fi.infinitygrow.gpslocation.presentation.utils.calculateCloudBaseHeight
import fi.infinitygrow.gpslocation.presentation.utils.pressTempAlt
import fi.infinitygrow.gpslocation.presentation.utils.pressureFromAltitude
import fi.infinitygrow.gpslocation.presentation.utils.pressureTemperatureAltitude
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.test.Test
import kotlin.test.assertEquals

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

    companion object {
        // Constants for testing
        const val DELTA = 0.01 // Tolerance for floating-point comparisons
        const val P_DEFAULT = 101325.0
        const val T_DEFAULT = 288.15
    }

    @Test
    fun `calculateCloudBaseHeight should return correct value`() {
        // Arrange
        val temperatureC = 20.0
        val dewPointC = 10.0
        val heightStationM = 100.0
        val expectedCloudBaseHeight = 1347.0

        // Act
        val actualCloudBaseHeight = calculateCloudBaseHeight(temperatureC, dewPointC, heightStationM)

        // Assert
        assertEquals(expectedCloudBaseHeight, actualCloudBaseHeight, DELTA)
    }

    @Test
    fun `calculateCloudBaseHeight with zero difference should return station height`() {
        // Arrange
        val temperatureC = 20.0
        val dewPointC = 20.0
        val heightStationM = 100.0
        val expectedCloudBaseHeight = 100.0

        // Act
        val actualCloudBaseHeight = calculateCloudBaseHeight(temperatureC, dewPointC, heightStationM)

        // Assert
        assertEquals(expectedCloudBaseHeight, actualCloudBaseHeight, DELTA)
    }

    @Test
    fun `pressTempAlt should return correct value for troposphere`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val h = 5000.0
        val expectedPressure = 54048.33

        // Act
        val actualPressure = pressTempAlt(p, t, h)

        // Assert
        assertEquals(expectedPressure, actualPressure, DELTA)
    }

    @Test
    fun `pressTempAlt should return correct value for lower stratosphere`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val h = 15000.0
        val expectedPressure = 12045.08

        // Act
        val actualPressure = pressTempAlt(p, t, h)

        // Assert
        assertEquals(expectedPressure, actualPressure, DELTA)
    }

    @Test
    fun `pressTempAlt should return NaN for altitude above 20000`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val h = 25000.0

        // Act
        val actualPressure = pressTempAlt(p, t, h)

        // Assert
        assertEquals(Double.NaN, actualPressure, DELTA)
    }

    @Test
    fun `pressTempAlt should return correct value for sea level`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val h = 0.0
        val expectedPressure = P_DEFAULT

        // Act
        val actualPressure = pressTempAlt(p, t, h)

        // Assert
        assertEquals(expectedPressure, actualPressure, DELTA)
    }

    @Test
    fun `altcalc should return correct value for troposphere`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val pa = 54048.33
        val expectedAltitude = 5000.0

        // Act
        val actualAltitude = altcalc(p, t, pa)

        // Assert
        assertEquals(expectedAltitude, actualAltitude, DELTA)
    }

    @Test
    fun `altcalc should return correct value for lower stratosphere`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val pa = 12045.08
        val expectedAltitude = 15000.0

        // Act
        val actualAltitude = altcalc(p, t, pa)

        // Assert
        assertEquals(expectedAltitude, actualAltitude, DELTA)
    }

    @Test
    fun `altcalc should return NaN for pressure ratio outside supported range`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val pa = 100.0

        // Act
        val actualAltitude = altcalc(p, t, pa)

        // Assert
        assertEquals(Double.NaN, actualAltitude, DELTA)
    }

    @Test
    fun `altcalc should return correct value for sea level`() {
        // Arrange
        val p = P_DEFAULT
        val t = T_DEFAULT
        val pa = P_DEFAULT
        val expectedAltitude = 0.0

        // Act
        val actualAltitude = altcalc(p, t, pa)

        // Assert
        assertEquals(expectedAltitude, actualAltitude, DELTA)
    }
    @Test
    fun `pressureTemperatureAltitude should return correct value`() {
        // Arrange
        val p = 98700.0
        val t = 268.15
        val targetAltitude = 1981.20
        val expectedAltitude = 1646.38

        // Act
        val calulatedlAltitude = pressureTemperatureAltitude(p, t, targetAltitude)

        // Assert
        assertEquals(expectedAltitude, calulatedlAltitude, DELTA)
    }
    @Test
    fun `altcalc should return correct values`() {
        // Arrange
        val p = 98700.0
        val t = 268.15
        val pressure = 79680.95
        val expectedAltitude = 1648.24

        // Act
        val actualAltitude = altcalc(p, t, pressure)

        // Assert
        assertEquals(expectedAltitude, actualAltitude, DELTA)
    }

    @Test
    fun `pressTempAlt should return correct values`() {
        // Arrange
        val p = 101300.0
        val t = 288.15
        val altitude = 1981.20
        val expectedPressure = 79680.95

        // Act
        val actualPressure = pressTempAlt(p, t, altitude)

        // Assert
        assertEquals(expectedPressure, actualPressure, DELTA)
    }


    //fun calculateAltitude(
    // pressTempAlt

}

