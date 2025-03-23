package fi.infinitygrow.gpslocation.presentation.utils

import org.junit.Test

class WeatherCalculationsKtTest {


    val temps2 = listOf(-10.4, -8.5, -8.4, -8.3, -8.3, -8.3, -8.3, -8.3, -8.1, -8.1, -8.2, -8.2, -8.0, -7.9, -7.9, -8.0, -8.0, -8.0, -8.1, -8.1, -8.1, -8.1, -8.2, -8.2, -8.2, -8.2, -8.1, -8.1, -8.1, -8.0, -8.0, -8.0, -8.1, -8.2, -8.3, -8.4, -8.5, -8.6, -8.7, -8.6, -8.5, -8.3, -8.2, -8.1, -8.2, -8.2, -8.3, -8.3, -8.4, -8.5, -8.6, -8.7, -8.8, -8.9, -9.0, -9.0, -9.1, -9.2, -9.2, -9.3, -9.4, -9.4, -9.5, -9.5, -9.6, -9.7, -9.8, -9.9, -10.0, -10.1, -10.1, -10.2, -10.3, -10.3, -10.4, -10.4, -10.6, -10.6, -10.7, -10.8, -10.8, -10.9, -11.0, -11.1, -11.1, -11.2, -11.3, -11.4, -11.5, -11.6, -11.7, -11.8, -11.9, -11.9, -12.0, -12.0, -12.1, -12.2, -12.3, -12.4, -12.5, -12.6, -12.7, -12.7, -12.8, -12.9, -13.0, -13.1, -13.2, -13.3, -13.4, -13.5, -13.6, -13.7, -13.7, -13.8, -13.8, -13.8, -13.9, -13.9, -13.9, -13.9, -14.0, -14.1, -14.1, -14.2, -14.2, -14.3, -14.4, -14.5, -14.5, -14.7, -14.7, -14.8, -14.9, -15.0, -15.1, -15.2, -15.3, -15.5, -15.6, -15.7, -15.8, -15.9, -16.0, -16.2, -16.3, -16.4, -16.4, -16.5)
    val des2 = listOf(-13.8, -12.9, -12.8, -12.9, -12.9, -12.8, -12.8, -12.9, -12.7, -12.7, -12.7, -12.7, -12.5, -12.5, -12.6, -12.6, -12.6, -12.5, -12.6, -12.5, -12.5, -12.5, -12.6, -12.6, -12.6, -12.6, -12.6, -12.7, -12.9, -13.0, -13.1, -13.1, -13.1, -13.1, -13.2, -13.2, -13.2, -13.2, -13.3, -13.6, -14.0, -14.4, -14.9, -15.3, -15.4, -15.4, -15.4, -15.5, -15.6, -15.7, -15.9, -16.1, -16.2, -16.5, -16.6, -16.8, -17.2, -17.2, -16.9, -16.6, -16.6, -16.6, -16.6, -16.8, -17.0, -17.0, -16.9, -17.0, -17.0, -17.2, -17.4, -17.6, -17.6, -17.5, -17.5, -17.6, -17.6, -17.8, -18.0, -18.1, -18.1, -18.2, -18.2, -18.3, -18.3, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.5, -18.8, -19.4, -19.9, -20.1, -20.1, -20.1, -20.1, -20.2, -20.2, -20.2, -20.2, -20.3, -20.4, -20.5, -20.6, -20.7, -20.8, -20.8, -20.9, -21.2, -21.4, -21.4, -21.5, -21.8, -22.2, -22.5, -22.7, -23.1, -23.4, -23.6, -23.6, -24.0, -24.1, -24.1, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.3, -24.2, -24.2, -24.3, -24.1, -24.2, -24.2, -24.2, -24.2, -24.3, -24.3, -24.4, -24.3, -24.3, -24.4)
    val alt2 = listOf(180.3, 202.2, 210.8, 220.4, 232.2, 244.4, 255.4, 266.0, 276.6, 286.2, 295.4, 305.4, 316.6, 328.5, 341.4, 354.5, 367.8, 380.6, 391.8, 401.6, 411.9, 423.9, 436.1, 446.2, 456.6, 469.4, 478.7, 486.8, 495.3, 503.2, 512.5, 523.5, 534.8, 545.8, 554.9, 563.3, 573.2, 582.2, 592.8, 605.0, 615.6, 627.2, 637.1, 644.4, 652.9, 659.2, 662.7, 672.5, 683.0, 692.8, 702.2, 713.4, 725.0, 734.8, 743.2, 753.3, 765.0, 775.3, 783.2, 790.5, 797.9, 805.1, 813.1, 822.1, 832.4, 842.6, 852.7, 864.2, 875.1, 884.6, 893.4, 902.9, 912.2, 920.7, 929.6, 940.9, 951.9, 961.1, 969.3, 978.2, 987.5, 996.2, 1004.2, 1011.8, 1020.8, 1031.1, 1041.2, 1050.1, 1057.6, 1065.5, 1074.1, 1082.5, 1092.2, 1103.2, 1114.1, 1124.6, 1134.3, 1143.7, 1154.0, 1165.5, 1176.4, 1185.2, 1193.3, 1201.4, 1210.9, 1222.7, 1234.3, 1245.4, 1256.9, 1267.3, 1277.2, 1287.1, 1297.0, 1307.6, 1308.7, 1318.3, 1327.8, 1337.4, 1347.2, 1355.7, 1364.8, 1375.3, 1385.7, 1396.0, 1405.9, 1416.9, 1429.1, 1440.3, 1450.3, 1459.7, 1469.3, 1480.0, 1490.4, 1501.8, 1513.3, 1523.6, 1534.6, 1546.7, 1558.9, 1570.7, 1581.0, 1591.2, 1602.8, 1615.9, 1628.4, 1639.9, 1649.6, 1658.4, 1666.6, 1676.0)


    @Test
    fun `Valid sounding data with equilibrium`() {
        // Test with valid sounding data where an equilibrium level is reached, 
        // resulting in a maximum altitude being returned.
        // TODO implement test
    }

    @Test
    fun `No equilibrium in sounding data`() {
        // Test with sounding data where the parcel temperature never becomes less than 
        // or equal to the environmental temperature, resulting in null.
        // TODO implement test
    }

    @Test
    fun `Empty sounding data list`() {
        // Test with an empty soundingData list, which should result in null.
        // TODO implement test
    }

    @Test
    fun `Single point sounding data list`() {
        // Test with a soundingData list containing only one point, which should result in 
        // checking the values and if true return the one and only point
        // TODO implement test
    }

    @Test
    fun `Unsorted sounding data`() {
        // Test with unsorted sounding data to verify the function correctly sorts it internally.
        // TODO implement test
    }

    @Test
    fun `Ground temperature equals ground dew point`() {
        // Test when groundTemperature equals groundDewPoint, LCL should be equal to 
        // the ground altitude resulting in no dry lapse rate calculation. 
        // TODO implement test
    }

    @Test
    fun `Ground temperature less than ground dew point`() {
        // Test when groundTemperature is less than groundDewPoint, resulting in invalid 
        // LCL and should still process to find an equilibrium level.
        // TODO implement test
    }

    @Test
    fun `LCL below ground altitude`() {
        // Test with parameters that result in an LCL altitude below the ground altitude. 
        // This should handle this properly as the altitude should never be below the ground
        // TODO implement test
    }

    @Test
    fun `Dry lapse rate zero`() {
        // Test with dryLapseRate equal to zero, simulating no temperature change 
        // below LCL. This would mean there is no change in temperature before LCL
        // TODO implement test
    }

    @Test
    fun `Moist lapse rate zero`() {
        // Test with moistLapseRate equal to zero, simulating no temperature change 
        // above LCL. This would mean there is no change in temperature after LCL
        // TODO implement test
    }

    @Test
    fun `Dry and moist lapse rate zero`() {
        // Test with both dryLapseRate and moistLapseRate equal to zero, simulating no 
        // temperature change at all. Will the code handle this.
        // TODO implement test
    }

    @Test
    fun `Large ground temperature`() {
        // Test with a very high groundTemperature, checking for overflow issues or 
        // unexpected behavior.
        // TODO implement test
    }

    @Test
    fun `Large ground dew point`() {
        // Test with a very high groundDewPoint, checking for overflow issues or 
        // unexpected behavior.
        // TODO implement test
    }

    @Test
    fun `Large ground altitude`() {
        // Test with a very high groundAltitude, checking if it causes any numerical issues.
        // TODO implement test
    }

    @Test
    fun `Negative ground temperature`() {
        // Test with a negative groundTemperature, ensuring proper handling of 
        // sub-zero temperatures.
        // TODO implement test
    }

    @Test
    fun `Negative ground dew point`() {
        // Test with a negative groundDewPoint, ensuring proper handling of 
        // sub-zero dew points.
        // TODO implement test
    }

    @Test
    fun `Negative ground altitude`() {
        // Test with a negative groundAltitude, checking if it causes any logical issues.
        // TODO implement test
    }

    @Test
    fun `Large lapse rates`() {
        // Test with extremely large dryLapseRate and moistLapseRate values, 
        // ensuring stability.
        // TODO implement test
    }

    @Test
    fun `Negative lapse rates`() {
        // Test with negative dryLapseRate and moistLapseRate values, ensuring 
        // the calculation are still being done correctly.
        // TODO implement test
    }

    @Test
    fun `Duplicate sounding data altitudes`() {
        // Test with duplicate altitudes within soundingData to ensure only the 
        // first instance is used.
        // TODO implement test
    }

    @Test
    fun `First sounding point at same altitude as ground`() {
        // Test when the first sounding point in list has the same altitude as 
        // the ground altitude. Will the code handle this.
        // TODO implement test
    }

    @Test
    fun `Sounding point that is at equilibrium `() {
        // Test when a sounding point is exactly at equilibrium, to ensure this 
        // edge case works correctly.
        // TODO implement test
    }

    @Test
    fun `Very small temperature differences in sounding data`() {
        // Test with very minor differences in temperature between each 
        // soundingData point.
        // TODO implement test
    }

}