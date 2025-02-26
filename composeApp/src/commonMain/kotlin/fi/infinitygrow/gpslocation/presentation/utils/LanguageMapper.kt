package fi.infinitygrow.gpslocation.presentation.utils

import androidx.compose.runtime.Composable
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.getAltitudeByName
import fi.infinitygrow.gpslocation.presentation.permission.Location
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.weather_code_0
import gpslocation.composeapp.generated.resources.weather_code_1
import gpslocation.composeapp.generated.resources.weather_code_10
import gpslocation.composeapp.generated.resources.weather_code_11
import gpslocation.composeapp.generated.resources.weather_code_12
import gpslocation.composeapp.generated.resources.weather_code_18
import gpslocation.composeapp.generated.resources.weather_code_2
import gpslocation.composeapp.generated.resources.weather_code_20
import gpslocation.composeapp.generated.resources.weather_code_21
import gpslocation.composeapp.generated.resources.weather_code_22
import gpslocation.composeapp.generated.resources.weather_code_23
import gpslocation.composeapp.generated.resources.weather_code_24
import gpslocation.composeapp.generated.resources.weather_code_25
import gpslocation.composeapp.generated.resources.weather_code_26
import gpslocation.composeapp.generated.resources.weather_code_27
import gpslocation.composeapp.generated.resources.weather_code_28
import gpslocation.composeapp.generated.resources.weather_code_29
import gpslocation.composeapp.generated.resources.weather_code_3
import gpslocation.composeapp.generated.resources.weather_code_30
import gpslocation.composeapp.generated.resources.weather_code_31
import gpslocation.composeapp.generated.resources.weather_code_32
import gpslocation.composeapp.generated.resources.weather_code_33
import gpslocation.composeapp.generated.resources.weather_code_34
import gpslocation.composeapp.generated.resources.weather_code_35
import gpslocation.composeapp.generated.resources.weather_code_37
import gpslocation.composeapp.generated.resources.weather_code_38
import gpslocation.composeapp.generated.resources.weather_code_4
import gpslocation.composeapp.generated.resources.weather_code_40
import gpslocation.composeapp.generated.resources.weather_code_41
import gpslocation.composeapp.generated.resources.weather_code_42
import gpslocation.composeapp.generated.resources.weather_code_43
import gpslocation.composeapp.generated.resources.weather_code_44
import gpslocation.composeapp.generated.resources.weather_code_45
import gpslocation.composeapp.generated.resources.weather_code_46
import gpslocation.composeapp.generated.resources.weather_code_47
import gpslocation.composeapp.generated.resources.weather_code_48
import gpslocation.composeapp.generated.resources.weather_code_5
import gpslocation.composeapp.generated.resources.weather_code_50
import gpslocation.composeapp.generated.resources.weather_code_51
import gpslocation.composeapp.generated.resources.weather_code_52
import gpslocation.composeapp.generated.resources.weather_code_53
import gpslocation.composeapp.generated.resources.weather_code_54
import gpslocation.composeapp.generated.resources.weather_code_55
import gpslocation.composeapp.generated.resources.weather_code_56
import gpslocation.composeapp.generated.resources.weather_code_57
import gpslocation.composeapp.generated.resources.weather_code_58
import gpslocation.composeapp.generated.resources.weather_code_60
import gpslocation.composeapp.generated.resources.weather_code_61
import gpslocation.composeapp.generated.resources.weather_code_62
import gpslocation.composeapp.generated.resources.weather_code_63
import gpslocation.composeapp.generated.resources.weather_code_64
import gpslocation.composeapp.generated.resources.weather_code_65
import gpslocation.composeapp.generated.resources.weather_code_66
import gpslocation.composeapp.generated.resources.weather_code_67
import gpslocation.composeapp.generated.resources.weather_code_68
import gpslocation.composeapp.generated.resources.weather_code_70
import gpslocation.composeapp.generated.resources.weather_code_71
import gpslocation.composeapp.generated.resources.weather_code_72
import gpslocation.composeapp.generated.resources.weather_code_73
import gpslocation.composeapp.generated.resources.weather_code_74
import gpslocation.composeapp.generated.resources.weather_code_75
import gpslocation.composeapp.generated.resources.weather_code_76
import gpslocation.composeapp.generated.resources.weather_code_77
import gpslocation.composeapp.generated.resources.weather_code_78
import gpslocation.composeapp.generated.resources.weather_code_80
import gpslocation.composeapp.generated.resources.weather_code_81
import gpslocation.composeapp.generated.resources.weather_code_82
import gpslocation.composeapp.generated.resources.weather_code_83
import gpslocation.composeapp.generated.resources.weather_code_84
import gpslocation.composeapp.generated.resources.weather_code_85
import gpslocation.composeapp.generated.resources.weather_code_86
import gpslocation.composeapp.generated.resources.weather_code_87
import gpslocation.composeapp.generated.resources.weather_code_89
import gpslocation.composeapp.generated.resources.weather_code_9
import gpslocation.composeapp.generated.resources.weather_code_90
import gpslocation.composeapp.generated.resources.weather_code_91
import gpslocation.composeapp.generated.resources.weather_code_92
import gpslocation.composeapp.generated.resources.weather_code_93
import gpslocation.composeapp.generated.resources.weather_code_94
import gpslocation.composeapp.generated.resources.weather_code_95
import gpslocation.composeapp.generated.resources.weather_code_96
import gpslocation.composeapp.generated.resources.weather_code_99
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun constructLanguageString(data: ObservationData?, location: Location): String? {
    if (data == null) return null

    val dist = getDistance(data.longitude, data.latitude, location.longitude, location.latitude)
        .takeIf { it.isFinite() }?.roundToInt()

    val bear = getBearing(location.longitude, location.latitude, data.longitude, data.latitude)
        .takeIf { it.isFinite() }?.let { bearingToDirection(it) }

    val parts = mutableListOf<String>()

    parts.add(data.name)
    dist?.let { parts.add("Etäisyys $it kilometriä. ") }
    bear?.let { parts.add(it) }

    data.precipitationIntensity.takeIf { it.isFinite() && it != 0.0 }?.let {
        parts.add("Sadetta $it millimetriä.")
    }

    data.windSpeed.takeIf { it.isFinite() }?.roundToInt()?.let {
        parts.add("Tuuli $it")
    }

    data.windGust.takeIf { it.isFinite() }?.roundToInt()?.let {
        parts.add("kautta $it metriä sekunnissa.")
    }

    data.windDirection.takeIf { it.isFinite() }?.roundToNearestFive()?.let {
        parts.add("Suunta $it astetta.")
    }

    calculateCloudBaseHeight(data.temperature, data.dewPoint, getAltitudeByName(data.name).toDouble())
        .takeIf { it.isFinite() }
        ?.roundToNearestHundred()
        ?.let { parts.add("Pilvenpohjat $it metriä.") }

    data.pressure.takeIf { it.isFinite() }?.let { pressure ->
        parts.add(
            "Lentopinnan 65 korkeus ${calculateAltitude(
            data.temperature + 273.15, 
            1981.2, 
            pressure * 100,
            getAltitudeByName(data.name).toDouble()
        ).toInt()} - ${pressureTemperatureAltitude(
            pressure * 100, 
            data.temperature + 273.15, 
            1981.20
        ).toInt()} metriä."
        )
        parts.add(
            "Lentopinnan 95 korkeus ${calculateAltitude(
            data.temperature + 273.15, 
            2895.6, 
            pressure * 100,
            getAltitudeByName(data.name).toDouble()
        ).toInt()} - ${pressureTemperatureAltitude(
            pressure * 100, 
            data.temperature + 273.15, 
            2895.60
        ).toInt()} metriä."
        )
    }

    data.presentWeather.takeIf { it.isFinite() }?.let {
        parts.add(getWeatherDescription(it.toInt()).first)
    }
    return parts.joinToString(" ")
}


private fun Double?.roundToNearestFive(): Int? {
    val remainder = this?.rem(5)
    val roundedDegrees = if (remainder!! < 2.5) {
        this?.minus(remainder)
    } else {
        this?.plus((5 - remainder))
    }
    if (roundedDegrees != null) {
        return roundedDegrees.roundToInt()
    }
    return null
}

private fun Double?.roundToNearestHundred(): Int? {
    val remainder = this?.rem(100)
    val roundedDegrees = if (remainder!! < 50) {
        this?.minus(remainder)
    } else {
        this?.plus((100 - remainder))
    }
    if (roundedDegrees != null) {
        return roundedDegrees.roundToInt()
    }
    return null
}

@Composable
fun getWeatherDescription(code: Int): Pair<String, DrawableResource?> {
    // Remove first digit if it's 1 and the code is 3 digits
    val normalizedCode = if (code in 100..199) code % 100 else code


    return when (normalizedCode) {
        0 -> Pair(stringResource(Res.string.weather_code_0), null)
        1 -> Pair(stringResource(Res.string.weather_code_1), Res.drawable.weather_code_9)
        2 -> Pair(stringResource(Res.string.weather_code_3), Res.drawable.weather_code_4)
        4 -> Pair(stringResource(Res.string.weather_code_4), null)
        5 -> Pair(stringResource(Res.string.weather_code_5), null)
        10 -> Pair(stringResource(Res.string.weather_code_10), Res.drawable.weather_code_9)
        11 -> Pair(stringResource(Res.string.weather_code_11), Res.drawable.weather_code_9)
        12 -> Pair(stringResource(Res.string.weather_code_12), null)
        18 -> Pair(stringResource(Res.string.weather_code_18), null)
        20 -> Pair(stringResource(Res.string.weather_code_20), Res.drawable.weather_code_9)
        21 -> Pair(stringResource(Res.string.weather_code_21), Res.drawable.weather_code_37)
        22 -> Pair(stringResource(Res.string.weather_code_22), Res.drawable.weather_code_47)
        23 -> Pair(stringResource(Res.string.weather_code_23), Res.drawable.weather_code_38)
        24 -> Pair(stringResource(Res.string.weather_code_24), null)
        25 -> Pair(stringResource(Res.string.weather_code_25), null)
        26 -> Pair(stringResource(Res.string.weather_code_26), null)
        27 -> Pair(stringResource(Res.string.weather_code_27), null)
        else -> Pair("Unknown weather code", null)
    }
}
//        2 -> stringResource(Res.string.weather_code_2)
//        3 -> stringResource(Res.string.weather_code_3)
//        4 -> stringResource(Res.string.weather_code_4)
//        5 -> stringResource(Res.string.weather_code_5)
//        10 -> stringResource(Res.string.weather_code_10)
//        11 -> stringResource(Res.string.weather_code_11)
//        12 -> stringResource(Res.string.weather_code_12)
//        18 -> stringResource(Res.string.weather_code_18)
//        20 -> stringResource(Res.string.weather_code_20)
//        21 -> stringResource(Res.string.weather_code_21)
//        22 -> stringResource(Res.string.weather_code_22)
//        23 -> stringResource(Res.string.weather_code_23)
//        24 -> stringResource(Res.string.weather_code_24)
//        25 -> stringResource(Res.string.weather_code_25)
//        26 -> stringResource(Res.string.weather_code_26)
//        27 -> stringResource(Res.string.weather_code_27)
//        28 -> stringResource(Res.string.weather_code_28)
//        29 -> stringResource(Res.string.weather_code_29)
//        30 -> stringResource(Res.string.weather_code_30)
//        31 -> stringResource(Res.string.weather_code_31)
//        32 -> stringResource(Res.string.weather_code_32)
//        33 -> stringResource(Res.string.weather_code_33)
//        34 -> stringResource(Res.string.weather_code_34)
//        35 -> stringResource(Res.string.weather_code_35)
//        40 -> stringResource(Res.string.weather_code_40)
//        41 -> stringResource(Res.string.weather_code_41)
//        42 -> stringResource(Res.string.weather_code_42)
//        43 -> stringResource(Res.string.weather_code_43)
//        44 -> stringResource(Res.string.weather_code_44)
//        45 -> stringResource(Res.string.weather_code_45)
//        46 -> stringResource(Res.string.weather_code_46)
//        47 -> stringResource(Res.string.weather_code_47)
//        48 -> stringResource(Res.string.weather_code_48)
//        50 -> stringResource(Res.string.weather_code_50)
//        51 -> stringResource(Res.string.weather_code_51)
//        52 -> stringResource(Res.string.weather_code_52)
//        53 -> stringResource(Res.string.weather_code_53)
//        54 -> stringResource(Res.string.weather_code_54)
//        55 -> stringResource(Res.string.weather_code_55)
//        56 -> stringResource(Res.string.weather_code_56)
//        57 -> stringResource(Res.string.weather_code_57)
//        58 -> stringResource(Res.string.weather_code_58)
//        60 -> stringResource(Res.string.weather_code_60)
//        61 -> stringResource(Res.string.weather_code_61)
//        62 -> stringResource(Res.string.weather_code_62)
//        63 -> stringResource(Res.string.weather_code_63)
//        64 -> stringResource(Res.string.weather_code_64)
//        65 -> stringResource(Res.string.weather_code_65)
//        66 -> stringResource(Res.string.weather_code_66)
//        67 -> stringResource(Res.string.weather_code_67)
//        68 -> stringResource(Res.string.weather_code_68)
//        70 -> stringResource(Res.string.weather_code_70)
//        71 -> stringResource(Res.string.weather_code_71)
//        72 -> stringResource(Res.string.weather_code_72)
//        73 -> stringResource(Res.string.weather_code_73)
//        74 -> stringResource(Res.string.weather_code_74)
//        75 -> stringResource(Res.string.weather_code_75)
//        76 -> stringResource(Res.string.weather_code_76)
//        77 -> stringResource(Res.string.weather_code_77)
//        78 -> stringResource(Res.string.weather_code_78)
//        80 -> stringResource(Res.string.weather_code_80)
//        81 -> stringResource(Res.string.weather_code_81)
//        82 -> stringResource(Res.string.weather_code_82)
//        83 -> stringResource(Res.string.weather_code_83)
//        84 -> stringResource(Res.string.weather_code_84)
//        85 -> stringResource(Res.string.weather_code_85)
//        86 -> stringResource(Res.string.weather_code_86)
//        87 -> stringResource(Res.string.weather_code_87)
//        89 -> stringResource(Res.string.weather_code_89)
//        90 -> stringResource(Res.string.weather_code_90)
//        91 -> stringResource(Res.string.weather_code_91)
//        92 -> stringResource(Res.string.weather_code_92)
//        93 -> stringResource(Res.string.weather_code_93)
//        94 -> stringResource(Res.string.weather_code_94)
//        95 -> stringResource(Res.string.weather_code_95)
//        96 -> stringResource(Res.string.weather_code_96)
//        99 -> stringResource(Res.string.weather_code_99)
//        else -> "Unknown weather code"
//    }


//0 -> Pair(stringResource(Res.string.weather_code_0), Res.drawable.weather_code_1)
//1 -> stringResource(Res.string.weather_code_1), Res.string.weather_code_1
//2 -> stringResource(Res.string.weather_code_2)
//3 -> stringResource(Res.string.weather_code_3)
//4 -> stringResource(Res.string.weather_code_4)
//5 -> stringResource(Res.string.weather_code_5)
//10 -> stringResource(Res.string.weather_code_10)
//11 -> stringResource(Res.string.weather_code_11)
//12 -> stringResource(Res.string.weather_code_12)
//18 -> stringResource(Res.string.weather_code_18)
//20 -> stringResource(Res.string.weather_code_20)
//21 -> stringResource(Res.string.weather_code_21)
//22 -> stringResource(Res.string.weather_code_22)
//23 -> stringResource(Res.string.weather_code_23)
//24 -> stringResource(Res.string.weather_code_24)
//25 -> stringResource(Res.string.weather_code_25)
//26 -> stringResource(Res.string.weather_code_26)
//27 -> stringResource(Res.string.weather_code_27)
//28 -> stringResource(Res.string.weather_code_28)
//29 -> stringResource(Res.string.weather_code_29)
//30 -> stringResource(Res.string.weather_code_30)
//31 -> stringResource(Res.string.weather_code_31)
//32 -> stringResource(Res.string.weather_code_32)
//33 -> stringResource(Res.string.weather_code_33)
//34 -> stringResource(Res.string.weather_code_34)
//35 -> stringResource(Res.string.weather_code_35)
//40 -> stringResource(Res.string.weather_code_40)
//41 -> stringResource(Res.string.weather_code_41)
//42 -> stringResource(Res.string.weather_code_42)
//43 -> stringResource(Res.string.weather_code_43)
//44 -> stringResource(Res.string.weather_code_44)
//45 -> stringResource(Res.string.weather_code_45)
//46 -> stringResource(Res.string.weather_code_46)
//47 -> stringResource(Res.string.weather_code_47)
//48 -> stringResource(Res.string.weather_code_48)
//50 -> stringResource(Res.string.weather_code_50)
//51 -> stringResource(Res.string.weather_code_51)
//52 -> stringResource(Res.string.weather_code_52)
//53 -> stringResource(Res.string.weather_code_53)
//54 -> stringResource(Res.string.weather_code_54)
//55 -> stringResource(Res.string.weather_code_55)
//56 -> stringResource(Res.string.weather_code_56)
//57 -> stringResource(Res.string.weather_code_57)
//58 -> stringResource(Res.string.weather_code_58)
//60 -> stringResource(Res.string.weather_code_60)
//61 -> stringResource(Res.string.weather_code_61)
//62 -> stringResource(Res.string.weather_code_62)
//63 -> stringResource(Res.string.weather_code_63)
//64 -> stringResource(Res.string.weather_code_64)
//65 -> stringResource(Res.string.weather_code_65)
//66 -> stringResource(Res.string.weather_code_66)
//67 -> stringResource(Res.string.weather_code_67)
//68 -> stringResource(Res.string.weather_code_68)
//70 -> stringResource(Res.string.weather_code_70)
//71 -> stringResource(Res.string.weather_code_71)
//72 -> stringResource(Res.string.weather_code_72)
//73 -> stringResource(Res.string.weather_code_73)
//74 -> stringResource(Res.string.weather_code_74)
//75 -> stringResource(Res.string.weather_code_75)
//76 -> stringResource(Res.string.weather_code_76)
//77 -> stringResource(Res.string.weather_code_77)
//78 -> stringResource(Res.string.weather_code_78)
//80 -> stringResource(Res.string.weather_code_80)
//81 -> stringResource(Res.string.weather_code_81)
//82 -> stringResource(Res.string.weather_code_82)
//83 -> stringResource(Res.string.weather_code_83)
//84 -> stringResource(Res.string.weather_code_84)
//85 -> stringResource(Res.string.weather_code_85)
//86 -> stringResource(Res.string.weather_code_86)
//87 -> stringResource(Res.string.weather_code_87)
//89 -> stringResource(Res.string.weather_code_89)
//90 -> stringResource(Res.string.weather_code_90)
//91 -> stringResource(Res.string.weather_code_91)
//92 -> stringResource(Res.string.weather_code_92)
//93 -> stringResource(Res.string.weather_code_93)
//94 -> stringResource(Res.string.weather_code_94)
//95 -> stringResource(Res.string.weather_code_95)
//96 -> stringResource(Res.string.weather_code_96)
//99 -> stringResource(Res.string.weather_code_99)
//else -> "Unknown weather code"