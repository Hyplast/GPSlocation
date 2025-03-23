import assertk.assertThat
import assertk.assertions.isEqualTo
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import fi.infinitygrow.gpslocation.presentation.utils.altcalc
import fi.infinitygrow.gpslocation.presentation.utils.calcSeaLevelTemperature
import fi.infinitygrow.gpslocation.presentation.utils.calcTemperatureAtAltitude
import fi.infinitygrow.gpslocation.presentation.utils.calculateAltitude
import fi.infinitygrow.gpslocation.presentation.utils.calculateCloudBaseHeight
import fi.infinitygrow.gpslocation.presentation.utils.estimateMaxAltitudeFromGround
import fi.infinitygrow.gpslocation.presentation.utils.pressTempAlt
import fi.infinitygrow.gpslocation.presentation.utils.pressureFromAltitude
import fi.infinitygrow.gpslocation.presentation.utils.pressureTemperatureAltitude
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals

fun createSoundingTestData(): List<SoundingData> {
    val soundingTemperatures = listOf(-10.4, -8.5, -8.4, -8.3, -8.3, -8.3, -8.3, -8.3, -8.1, -8.1, -8.2, -8.2, -8.0, -7.9, -7.9, -8.0, -8.0, -8.0, -8.1, -8.1, -8.1, -8.1, -8.2, -8.2, -8.2, -8.2, -8.1, -8.1, -8.1, -8.0, -8.0, -8.0, -8.1, -8.2, -8.3, -8.4, -8.5, -8.6, -8.7, -8.6, -8.5, -8.3, -8.2, -8.1, -8.2, -8.2, -8.3, -8.3, -8.4, -8.5, -8.6, -8.7, -8.8, -8.9, -9.0, -9.0, -9.1, -9.2, -9.2, -9.3, -9.4, -9.4, -9.5, -9.5, -9.6, -9.7, -9.8, -9.9, -10.0, -10.1, -10.1, -10.2, -10.3, -10.3, -10.4, -10.4, -10.6, -10.6, -10.7, -10.8, -10.8, -10.9, -11.0, -11.1, -11.1, -11.2, -11.3, -11.4, -11.5, -11.6, -11.7, -11.8, -11.9, -11.9, -12.0, -12.0, -12.1, -12.2, -12.3, -12.4, -12.5, -12.6, -12.7, -12.7, -12.8, -12.9, -13.0, -13.1, -13.2, -13.3, -13.4, -13.5, -13.6, -13.7, -13.7, -13.8, -13.8, -13.8, -13.9, -13.9, -13.9, -13.9, -14.0, -14.1, -14.1, -14.2, -14.2, -14.3, -14.4, -14.5, -14.5, -14.7, -14.7, -14.8, -14.9, -15.0, -15.1, -15.2, -15.3, -15.5, -15.6, -15.7, -15.8, -15.9, -16.0, -16.2, -16.3, -16.4, -16.4, -16.5)
    val soundingDewPoints = listOf(-13.8, -12.9, -12.8, -12.9, -12.9, -12.8, -12.8, -12.9, -12.7, -12.7, -12.7, -12.7, -12.5, -12.5, -12.6, -12.6, -12.6, -12.5, -12.6, -12.5, -12.5, -12.5, -12.6, -12.6, -12.6, -12.6, -12.6, -12.7, -12.9, -13.0, -13.1, -13.1, -13.1, -13.1, -13.2, -13.2, -13.2, -13.2, -13.3, -13.6, -14.0, -14.4, -14.9, -15.3, -15.4, -15.4, -15.4, -15.5, -15.6, -15.7, -15.9, -16.1, -16.2, -16.5, -16.6, -16.8, -17.2, -17.2, -16.9, -16.6, -16.6, -16.6, -16.6, -16.8, -17.0, -17.0, -16.9, -17.0, -17.0, -17.2, -17.4, -17.6, -17.6, -17.5, -17.5, -17.6, -17.6, -17.8, -18.0, -18.1, -18.1, -18.2, -18.2, -18.3, -18.3, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.5, -18.8, -19.4, -19.9, -20.1, -20.1, -20.1, -20.1, -20.2, -20.2, -20.2, -20.2, -20.3, -20.4, -20.5, -20.6, -20.7, -20.8, -20.8, -20.9, -21.2, -21.4, -21.4, -21.5, -21.8, -22.2, -22.5, -22.7, -23.1, -23.4, -23.6, -23.6, -24.0, -24.1, -24.1, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.3, -24.2, -24.2, -24.3, -24.1, -24.2, -24.2, -24.2, -24.2, -24.3, -24.3, -24.4, -24.3, -24.3, -24.4)
    val SoundingAltitudes = listOf(180.3, 202.2, 210.8, 220.4, 232.2, 244.4, 255.4, 266.0, 276.6, 286.2, 295.4, 305.4, 316.6, 328.5, 341.4, 354.5, 367.8, 380.6, 391.8, 401.6, 411.9, 423.9, 436.1, 446.2, 456.6, 469.4, 478.7, 486.8, 495.3, 503.2, 512.5, 523.5, 534.8, 545.8, 554.9, 563.3, 573.2, 582.2, 592.8, 605.0, 615.6, 627.2, 637.1, 644.4, 652.9, 659.2, 662.7, 672.5, 683.0, 692.8, 702.2, 713.4, 725.0, 734.8, 743.2, 753.3, 765.0, 775.3, 783.2, 790.5, 797.9, 805.1, 813.1, 822.1, 832.4, 842.6, 852.7, 864.2, 875.1, 884.6, 893.4, 902.9, 912.2, 920.7, 929.6, 940.9, 951.9, 961.1, 969.3, 978.2, 987.5, 996.2, 1004.2, 1011.8, 1020.8, 1031.1, 1041.2, 1050.1, 1057.6, 1065.5, 1074.1, 1082.5, 1092.2, 1103.2, 1114.1, 1124.6, 1134.3, 1143.7, 1154.0, 1165.5, 1176.4, 1185.2, 1193.3, 1201.4, 1210.9, 1222.7, 1234.3, 1245.4, 1256.9, 1267.3, 1277.2, 1287.1, 1297.0, 1307.6, 1308.7, 1318.3, 1327.8, 1337.4, 1347.2, 1355.7, 1364.8, 1375.3, 1385.7, 1396.0, 1405.9, 1416.9, 1429.1, 1440.3, 1450.3, 1459.7, 1469.3, 1480.0, 1490.4, 1501.8, 1513.3, 1523.6, 1534.6, 1546.7, 1558.9, 1570.7, 1581.0, 1591.2, 1602.8, 1615.9, 1628.4, 1639.9, 1649.6, 1658.4, 1666.6, 1676.0)
    val soundingData = mutableListOf<SoundingData>()

    for (i in soundingTemperatures.indices) {
        soundingData.add(
            SoundingData(
                name = "Test",
                timeOfSounding = "00:00",
                coordinates = Location(latitude = 0.0, longitude = 0.0),
                longitude = 0.0,
                latitude = 0.0,
                altitude = SoundingAltitudes[i],
                unixTime = 0L,
                pressure = 1000.0,
                windSpeed = 0.0,
                windDirection = 0.0,
                temperature = soundingTemperatures[i],
                dewPoint = soundingDewPoints[i]
            )
        )
    }
    return soundingData
}
//assertThat(estimatedALtitude).isEqualTo(182.40)

class WeatherCalculationsKtTest {

    companion object {
        // Constants for testing
        const val DELTA = 0.01 // Tolerance for floating-point comparisons
        const val P_DEFAULT = 101325.0
        const val T_DEFAULT = 288.15
    }

    @Test
    fun `Valid sounding data with equilibrium`() {
        // Test with valid sounding data where an equilibrium level is reached,
        // resulting in a maximum altitude being returned.
        // TODO implement test
        val soundingData = createSoundingTestData()

        val estimatedAltitude = estimateMaxAltitudeFromGround(soundingData, -10.0, -20.0, 180.0)

        if (estimatedAltitude != null) {
            assertEquals(estimatedAltitude, 182.40, DELTA)
        }
    }

    @Test
    fun `No equilibrium in sounding data`() {
        // Test with sounding data where the parcel temperature never becomes less than
        // or equal to the environmental temperature, resulting in null.
        val soundingData = createSoundingTestData()

        val estimatedAltitude = estimateMaxAltitudeFromGround(soundingData, 99.0, 98.0, 180.0)

        assertThat(estimatedAltitude).isEqualTo(null)
    }

    @Test
    fun `Empty sounding data list`() {
        // Test with an empty soundingData list, which should result in null.
        // TODO implement test
        val estimatedAltitude = estimateMaxAltitudeFromGround(soundingData = emptyList(), -11.0, -13.0, 180.0)

        assertThat(estimatedAltitude).isEqualTo(null)
    }

    @Test
    fun `Ground temperature equals ground dew point`() {
        // Test when groundTemperature equals groundDewPoint, LCL should be equal to
        // the ground altitude resulting in no dry lapse rate calculation.
        val soundingData = createSoundingTestData()

        val estimatedAltitude = estimateMaxAltitudeFromGround(soundingData, -8.0, -8.0, 180.0)

        if (estimatedAltitude != null) {
            assertEquals(estimatedAltitude, 180.00, DELTA)
        }
    }

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

