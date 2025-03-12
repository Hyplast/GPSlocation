package fi.infinitygrow.gpslocation.domain.model

data class ObservationLocation(
    val name: String,           // Name of the location (e.g., Alajärvi Möksy)
    val fmiId: Int,              // Location ID (e.g., 101533)
    val lpnnId: Int?,              // Location ID (e.g., 101533)
    val wmoId: Int?,              // Location ID (e.g., 101533)
    val latitude: Double,       // Latitude (e.g., 63.09)
    val longitude: Double,      // Longitude (e.g., 24.26)
    val altitude: Int,         // Altitude value (e.g., 3314)
    val type: String,           // Type of measurement (e.g., "sää")
    val year: Int?              // Year (e.g., 1957)
)

fun getAltitudeByName(name: String): Int {
    return locations.find { it.name.equals(name, ignoreCase = true) }?.altitude ?: 0
}

fun getFmiIdByObservation(observation: ObservationData): Int {
    return locations.find { it.name.equals(observation.name, ignoreCase = true) }?.fmiId ?: -1
}

fun getObservationLocation(observation: ObservationData): ObservationLocation? {
    return locations.find { it.name.equals(observation.name, ignoreCase = true) }
}

fun getObservationLocationByName(name: String): ObservationLocation? {
    return locations.find { it.name.equals(name, ignoreCase = true) }
}

fun getHolfoystations(): List<ObservationLocation> {
    return locations.filter { it.fmiId == 546 || it.fmiId == 764 }
}


// Sample list of locations
val locations = listOf(
    ObservationLocation("Alajärvi Möksy", 101533, 3314, 2787, 63.09, 24.26, 171, "sää", 1957),
    ObservationLocation("Asikkala Pulkkilanharju", 101185, 434, 2727, 61.27, 25.52, 79, "sää", 1991),
    ObservationLocation("Enontekiö Kilpisjärvi Saana", 102017, 9004, 2701, 69.04, 20.85, 1001, "sää", 1991),
    ObservationLocation("Enontekiö Kilpisjärvi kyläkeskus", 102016, 9003, 2801, 69.04, 20.81, 482, "sää", 1951),
    ObservationLocation("Enontekiö Näkkälä", 102019, 9201, 2726, 68.6, 23.58, 375, "sää", 1960),
    ObservationLocation("Enontekiö lentoasema", 101976, 8208, 2802, 68.36, 23.43, 308, "sää", 1999),
    ObservationLocation("Espoo Nuuksio", 852678, 344, 2986, 60.29, 24.57, 90, "sää", 2014),
    ObservationLocation("Espoo Tapiola", 874863, 342, 2985, 60.18, 24.79, 6, 	"sää", 2013),
    ObservationLocation("Haapavesi Mustikkamäki",101695, 4402, 2797, 64.14, 25.42, 112, "sää, sade", 1951),
    ObservationLocation("Hailuoto Keskikylä",101776, 5301,2874,65.02,24.73,6, "sää",1950),
    ObservationLocation("Hailuoto Marjaniemi",101784, 5310,2873, 65.04,24.56,7, "sää, ilmanlaatu (IL)", 1881),
    ObservationLocation("Halsua Purola",101528,3309,2725,63.45,24.44,153,	"sää, sade",1958),
    ObservationLocation("Hammarland Märket", 100919, 14, 2993, 60.3, 19.13, 3, "sää", 1885),
    ObservationLocation("Hanko Russarö", 100932, 101, 2982, 59.77, 22.95, 6, "sää", 1881),
    ObservationLocation("Hanko Tulliniemi", 100946, 115, 2746, 59.81, 22.91, 10, "sää", 1990),
    ObservationLocation("Hanko Tvärminne", 100953, 202, 2750, 59.84, 23.25, 3, "sää", 1962),
    ObservationLocation("Hattula Lepaa", 101151, 1323, 2704, 61.11, 24.32, 87, "sää", 1927),
    ObservationLocation("Heinola Asemantaus",101196,1506,2768,61.2, 26.05, 93, "sää", 1908),
    ObservationLocation("Helsinki Harmaja", 100996, 330, 2795, 60.11, 24.98, 6, "sää", 1989),
    ObservationLocation("Helsinki Helsingin majakka",101003,337,2989,59.95,24.93, 0,"sää",2003),
    ObservationLocation("Helsinki Kaisaniemi", 100971, 304, 2978, 60.18, 24.94, 3, "sää", 1844),
    ObservationLocation("Helsinki Kumpula",101004,339,2998,60.2,24.96,24,"sää, ilmanlaatu (IL)",2005),
    ObservationLocation("Helsinki Malmi lentokenttä",101009,401,null, 60.25,25.05,15,"sää",1937),
    ObservationLocation("Helsinki Vuosaari Käärmeniementie",103943,null,null,60.22,25.17,9,"sää",2016),
    ObservationLocation("Helsinki Vuosaari satama",151028,411,null,60.21,25.2,2,"sää",2012),
    ObservationLocation("Hyvinkää Hyvinkäänkylä",101130,1302,2829,60.6,24.8,86,"sää",1930),
    ObservationLocation("Hämeenlinna Katinen",101150,1322,2754,61.0,24.49, 87,"sää",2001),
    ObservationLocation("Hämeenlinna Lammi Pappila",101154,1403,2767,61.05,25.04,125,"sää",1963),
    ObservationLocation("Ilmajoki Seinäjoki lentoasema",137188,null,null,62.69,22.84,87,"sää",2012),
    ObservationLocation("Ilomantsi Mekrijärvi",101651,3919,2939,62.77,30.97,156,"sää",1999),
    ObservationLocation("Ilomantsi Pötsönvaara",101649,3917,2766,63.14,31.04,237,"sää, ilmanlaatu (IL)",1997),
    ObservationLocation("Inari Angeli Lintupuoliselkä",102026,9405,2857,68.9,25.74,285,"sää",2008),
    ObservationLocation("Inari Ivalo lentoasema",102033,9601,2807,68.61,27.42,140,"sää",1957),
    ObservationLocation("Inari Kaamanen",102047,9616,2702,69.14,27.27,156,"sää",2008),
    ObservationLocation("Inari Kirakkajärvi",102055,9708,2858,69.58,28.89,106,"sää",2009),
    ObservationLocation("Inari Nellim",102052,9705,2835,68.85,28.3,121,"sää",1951),
    ObservationLocation("Inari Raja-Jooseppi",102009,8704,2818,68.48,28.3,253,"sää, ilmanlaatu (IL)",1992),
    ObservationLocation("Inari Raja-Jooseppi Kontiojärvi",102008,null,null,68.47,28.32,185,"sää",1960),
    ObservationLocation("Inari Saariselkä Kaunispää",102006,8607,2817,68.43,27.45,437,"sää",1995),
    ObservationLocation("Inari Saariselkä matkailukeskus",102005,8606,2722,68.42,27.41,304,"sää",1976),
    ObservationLocation("Inari Seitalaassa",129963,9617,2888,69.05,27.76,121,"sää",2011),
    ObservationLocation("Inari Väylä",102042,9611,2827,69.07,27.49,123,"sää",1990),
    ObservationLocation("Inkoo Bågaskär", 100969, 302, 2984, 59.93, 24.01, 13, "sää", 1954),
    ObservationLocation("Inkoo Jakobramsjö",108020,null,null,59.99,24.0,0,"sää",2023),
    ObservationLocation("Joensuu Linnunlahti",101632,3825,2928,62.6,29.73,79,"sää",2008),
    ObservationLocation("Jokioinen Ilmala",101104,1201,2963,60.81,23.5,104,"sää, luotaus",1928),
    ObservationLocation("Jomala Jomalaby", 100917, 11, 2741, 60.18, 19.99, 11, "sää", 1971),
    ObservationLocation("Jomala Maarianhamina lentoasema",100907,1,2970,60.13,19.9,2,"sää",1950),
    ObservationLocation("Joutsa Savenaho",101367,2501,2771,61.88,26.09,145,"sää",1946),
    ObservationLocation("Juuka Niemelä",101609,3802,2791,63.23,29.23,115,"sää",1914),
    ObservationLocation("Juupajoki Hyytiälä",101317,2303,2770,61.85,24.29,154,"sää, sade, ilmanlaatu (IL), ilmanlaatu (kaupungit)",1956),
    ObservationLocation("Juva Partala",101418,2622,2736,61.89,27.89,110,"sää",1992),
    ObservationLocation("Jyväskylä lentoasema",101339,2401,2935,62.4,25.67,139,"sää",1945),
    ObservationLocation("Jyväskylä lentoasema AWOS",137208,null,null,62.39,25.69,124,"sää",1999),
    ObservationLocation("Jämsä Halli Lentoasemantie",101338,2324,2946,61.86,24.81,144, "sää",1964),
    ObservationLocation("Jämsä Halli lentoasema",101315,2301,2945,61.86,24.8,144,"sää",1964),
    ObservationLocation("Järvenpää Sorto",103786,null,null,60.5,25.07,66,"sää",2015),
    ObservationLocation("Kaarina Yltöinen",100934,103,2828,60.39,22.55,6,"sää",1927),
    ObservationLocation("Kajaani Petäisenniska",126736,4620,2883,64.22,27.75,160,"sää",2010),
    ObservationLocation("Kajaani lentoasema",101725,4601,2897,64.28,27.67,132,"sää",1956),
    ObservationLocation("Kalajoki Ulkokalla",101673,4212,2907,64.33,23.45,3,"sää",1876),
    ObservationLocation("Kankaanpää Niinisalo lentokenttä",101291,2123,2753,61.84,22.46,123,"sää",1940),
    ObservationLocation("Karvia Alkkia",101272,2103,2708,62.18,22.8,162,"sää",1966),
    ObservationLocation("Kaskinen Sälgrund",101256,2002,2931,62.33,21.19,5,"sää",1876),
    ObservationLocation("Kauhajoki Kuja-Kokko",101289,2121,2769,62.41,22.18,93,"sää",1986),
    ObservationLocation("Kauhava lentokenttä",101503,3201,2913,63.12,23.04,42,"sää",1931),
    ObservationLocation("Kemi Ajos", 101846, 6307, 2862, 65.67, 24.52, 3, "sää",1973),
    ObservationLocation("Kemi I majakka",101783,5309,2863,65.39,24.1,0, "sää",1978),
    ObservationLocation("Kemi Kemi-Tornio lentoasema",101840,6301,2864,65.79,24.58,11,"sää",1945),
    ObservationLocation("Kemijärvi lentokenttä",101950,7607,2814,66.72,27.16,208,"sää",2006),
    ObservationLocation("Kemiönsaari Kemiö",100951,120,2906,60.17,22.76,12,"sää",2009),
    ObservationLocation("Kemiönsaari Vänö",100945,114,2743, 59.87,22.19,10,"sää",1990),
    ObservationLocation("Kirkkonummi Mäkiluoto",100997,331,2794,59.92,24.35,4,"sää",1989),
    ObservationLocation("Kittilä Kenttärova",101987,8312,2824,67.99,24.24,347,"sää",2002),
    ObservationLocation("Kittilä Lompolonvuoma",778135,8303,2764,68.0,24.21,270,"sää, ilmanlaatu (IL)",2013),
    ObservationLocation("Kittilä Matorova",101985,8310,2882,68.0,24.24,339,"sää, ilmanlaatu (IL)",1995),
    ObservationLocation("Kittilä Pakatti",101990,8315,2860,67.67,24.93,179,"sää",2009),
    ObservationLocation("Kittilä Pokka",101994,8404,2717,68.17,25.78,275,"sää",1971),
    ObservationLocation("Kittilä lentoasema",101986,8311,2720,67.69,24.86,195,"sää",1999),
    ObservationLocation("Kokemäki Tulkkila",101103,1140,2937,61.25, 22.35,38,"sää",1912),
    ObservationLocation("Kokkola Santahaka",101675,4214,2852,63.84,23.1,5,"sää",2007),
    ObservationLocation("Kokkola Tankar",101661,4109,2721,63.95,22.85,5,"sää",1889),
    ObservationLocation("Korsnäs Bredskäret",101479,3018,2780,62.93,21.18,5,"sää",1991),
    ObservationLocation("Kotka Haapasaari",101042,601,2967,60.29,27.18,4,"sää",1986),
    ObservationLocation("Kotka Rankki",101030,501,2976,60.38,26.96,11,"sää",1933),
    ObservationLocation("Kouvola Anjala",101194,1504,2830,60.7,26.81,31,"sää",1941),
    ObservationLocation("Kouvola Utti Lentoportintie",101219,1529,2956,60.89,26.93,96,"sää",1944),
    ObservationLocation("Kouvola Utti lentoasema",101191,1501,2966,60.9,26.95,102,"sää",1944),
    ObservationLocation("Kristiinankaupunki Majakka",101268,2014,2752,62.2,21.17,0,"sää",1997),
    ObservationLocation("Kruunupyy Kokkola-Pietarsaari lentoasema",101662,4201,2903,63.73,23.14,25,"sää",1960),
    ObservationLocation("Kuhmo Kalliojoki",101773,4901,2799,64.3,30.17,196,"sää",1997),
    ObservationLocation("Kumlinge kirkonkylä",100928,23,2790,60.26,20.75,23,"sää",2000),
    ObservationLocation("Kuopio Maaninka",101572,3603,2788,63.14,27.31,91,"sää", 1930),
    ObservationLocation("Kuopio Ritoniemi",101580,3611,2732,62.8,27.9,86,"sää",1980),
    ObservationLocation("Kuopio Savilahti",101586,3617,2955,62.89,27.63,87,"sää",2005),
    ObservationLocation("Kustavi Isokari",101059,1016,2964,60.72,21.03,5,"sää",1974),
    ObservationLocation("Kuusamo Juuma",101899,6814,2749,66.32,29.4,298,"sää, ilmanlaatu (IL)",1989),
    ObservationLocation("Kuusamo Kiutaköngäs",101887,6802,2811,66.37,29.31,167,"sää",1966),
    ObservationLocation("Kuusamo Ruka Talvijärvi",806428,6817,2760,66.17,29.14,306,"sää",2013),
    ObservationLocation("Kuusamo Rukatunturi",101897,6812,2868,66.17,29.15,497,"sää",1990),
    ObservationLocation("Kuusamo Välikangas",107081,null,null,66.0,29.23,268,"sää",2019),
    ObservationLocation("Kuusamo lentoasema",101886,6801,2869,65.99,29.23,265,"sää",1908),
    ObservationLocation("Kökar Bogskär", 100921, 16, 2979, 59.5, 20.35, 4, "sää", 1982),
    ObservationLocation("Lahti Sopenkorpi",104796,1441,null,60.97,25.62,100,"sää",2016),
    ObservationLocation("Lappeenranta Hiekkapakka",101252,1716,2919,61.2,28.47,77,"sää",2009),
    ObservationLocation("Lappeenranta Konnunsuo",101246,1710,2733,61.04,28.56,46,"sää",1991),
    ObservationLocation("Lappeenranta lentoasema",101237,1701,2958,61.04,28.13,104,"sää",1950),
    //ObservationLocation("Leivonmäki Höystö", 546,null,null,61.89,26.18,100,"sää",2019),
    ObservationLocation("Lemland Nyhamn",100909,3,2980,59.96,19.95,9,"sää",1958),
    ObservationLocation("Lieksa Lampela",101636,3904,2796,63.32,30.05,99,"sää",1946),
    ObservationLocation("Liperi Joensuu lentoasema",101608,3801,2929,62.66,29.64,112,"sää",1955),
    ObservationLocation("Liperi Tuiskavanluoto",101628,3821,2793,62.55,29.67,78,"sää",1997),
    //ObservationLocation("Lohja Pottenperi",764,null,null,60.25,23.99,3,"sää",2012),
    ObservationLocation("Lohja Porla",100974,307,2706,60.24,24.05,35,"sää",1949),
    ObservationLocation("Loviisa Orrengrund",101039,510,2992,60.27,26.45,1,"sää",1974),
    ObservationLocation("Luhanka Judinsalo",101362,2426,2765,61.7,25.51,80,"sää",1990),
    ObservationLocation("Lumparland Långnäs satama",151048,12,2724,60.12,20.3,10,"sää",2012),
    ObservationLocation("Maalahti Strömmingsbådan",101481,3020,2781,62.98,20.74,2,"sää",1997),
    ObservationLocation("Maarianhamina Lotsberget",107383,null,null,60.09,19.94,40,"sää",2021),
    ObservationLocation("Maarianhamina Länsisatama",151029,26,2997,60.09,19.93,2,"sää",2012),
    ObservationLocation("Mikkeli Lentoasema AWOS",855522,null,null,61.69,27.21,101,"sää",1959),
    ObservationLocation("Mikkeli lentoasema",101398,2602,2947,61.69,27.2,99,"sää",1951),
    ObservationLocation("Multia Karhila",101536,3317,2927,62.51,24.81,227,"sää",2008),
    ObservationLocation("Muonio Laukukero",101982,8307,2820,68.06,24.03,760,"sää",1996),
    ObservationLocation("Muonio Oustajärvi",106435,8211,2823,67.95,23.71,240,"sää",2013),
    ObservationLocation("Muonio Sammaltunturi",101983,8308,2821,67.97,24.12,555,"sää,ilmanlaatu(IL)",1991),
    ObservationLocation("Mustasaari Valassaaret",101464,3003,2910,63.44,21.07,4,"sää",1895),
    ObservationLocation("Mäntsälä Hirvihaara",103794,1440,2774,60.63,25.19,83,"sää",2015),
    ObservationLocation("Nurmes Valtimo",101743,4701,2798,63.67,28.83,114,"sää",1969),
    ObservationLocation("Nurmijärvi Röykkä",101149,1321,2983,60.51,24.65,110,"sää",1952),
    ObservationLocation("Oulu Kaukovainio",108040,null,null,65.0,25.52,13,"sää",2023),
    ObservationLocation("Oulu Vihreäsaarisatama",101794,5409,2876,65.01,25.39,3,"sää",1996),
    ObservationLocation("Oulu lentoasema",101786,5401,2875,64.94,25.34,13,"sää",1953),
    ObservationLocation("Parainen Fagerholm",100924,19,2950,60.11,21.7,3,"sää",1989),
    ObservationLocation("Parainen Utö",100908,2,2981,59.78,21.37,6,"sää,sade,ilmanlaatu(IL)",1881),
    ObservationLocation("Parikkala Koitsanlahti",101254,1802,2734,61.44,29.46,74,"sää",1991),
    ObservationLocation("Pelkosenniemi Pyhätunturi",101958,7708,2705,67.02,27.22,489,"sää",1995),
    ObservationLocation("Pello kirkonkylä",101914,7307,2844,66.77,23.96,86,"sää",1970),
    ObservationLocation("Pietarsaari Kallan",101660,4108,2920,63.75,22.52,2,"sää",1995),
    ObservationLocation("Pirkkala Tampere-Pirkkala lentoasema",101118,1215,2944,61.42,23.62,112,"sää",1979),
    ObservationLocation("Pori Tahkoluoto satama",101267,2013,2751,61.63,21.38,3,"sää",1996),
    ObservationLocation("Pori lentoasema",101044,1001,2952,61.46,21.81,11,"sää",1945),
    ObservationLocation("Pori rautatieasema",101064,1021,2926,61.48,21.78,9,"sää",2008),
    ObservationLocation("Porvoo Emäsalo",101023,417,2991,60.2,25.63,19,"sää",1974),
    ObservationLocation("Porvoo Harabacka",101028,422,2759,60.39,25.61,22,"sää",2006),
    ObservationLocation("Porvoo Kalbådagrund",101022,416,2987,59.99,25.6,0,"sää",1977),
    ObservationLocation("Porvoo Kilpilahti satama",100683,424,2994,60.3,25.55,2,"sää",2014),
    ObservationLocation("Pudasjärvi lentokenttä",101805,5507,2866,65.4,26.96,120,"sää",1999),
    ObservationLocation("Puolanka Paljakka",101831,5716,2859,64.66,28.06,341,"sää",2009),
    ObservationLocation("Puumala kirkonkylä",150168,2708,2718,61.52,28.18,98,"sää",2012),
    ObservationLocation("Pyhtää lentokenttä",107029,513,null,60.49,26.59,21,"sää",2019),
    ObservationLocation("Pyhäjärvi Ojakylä",101705,4412,2738,63.74,25.71,152,"sää",1990),
    ObservationLocation("Raahe Lapaluoto satama",101785,5311,2872,64.67,24.41,2,"sää",1990),
    ObservationLocation("Raahe Nahkiainen",101775,5201,2800,64.61,23.9,0,"sää",1997),
    ObservationLocation("Raasepori Jussarö",100965,215,2757,59.82,23.57,17,"sää",1990),
    ObservationLocation("Rantasalmi Rukkasluoto",101436,2716,2772,62.06,28.57,79,"sää",1997),
    ObservationLocation("Ranua Aho",108133,null,null,66.15,26.11,223,"sää",2024),
    ObservationLocation("Ranua lentokenttä",101873,6514,2881,65.98,26.37,161,"sää",2008),
    ObservationLocation("Rauma Kylmäpihlaja",101061,1018,2761,61.14,21.3,4,"sää",1990),
    ObservationLocation("Rauma Pyynpää",105427,1023,null,61.14,21.52,15,"sää",2018),
    ObservationLocation("Rautavaara Ylä-Luosta",101603,3716,2789,63.38,28.66,164,"sää,sade",1976),
    ObservationLocation("Rovaniemi Apukka",101933,7502,2813,66.58,26.01,106,"sää",1939),
    ObservationLocation("Rovaniemi lentoasema",101920,7401,2845,66.56,25.84,189,"sää",1951),
    ObservationLocation("Rovaniemi lentoasema AWOS",137190,null,null,66.57,25.85,187,"sää",1999),
    ObservationLocation("Rovaniemi rautatieasema",101928,7409,2847,66.5,25.71,85,"sää",1997),
    ObservationLocation("Salla Naruska",101966,7804,2745,67.16,29.18,213,"sää",1999),
    ObservationLocation("Salla Värriötunturi",102012,8803,2819,67.75,29.61,360,"sää,sade,ilmanlaatu(IL),ilmanlaatu(kaupungit)",1971),
    ObservationLocation("Salla kirkonkylä",101959,7709,2849,66.82,28.69,218,"sää",1960),
    ObservationLocation("Salo Kiikala lentokenttä",100967,217,2777,60.46,23.65,117,"sää",2002),
    ObservationLocation("Salo Kärkkä",100955,205,2756,60.37,23.11,3,"sää",1936),
    ObservationLocation("Savonlinna Punkaharju Laukansaari",101441,2801,2778,61.8,29.32,79,"sää",1904),
    ObservationLocation("Savonlinna lentoasema",101430,2710,2948,61.95,28.93,92,"sää",1974),
    ObservationLocation("Savukoski Tulppio",107565,null,null,67.77,29.21,217,"sää",2022),
    ObservationLocation("Savukoski kirkonkylä",101952,7702,2815,67.28,28.18,174,"sää",1934),
    ObservationLocation("Seinäjoki Pelmaa",101486,3101,2833,62.94,22.49,26,"sää",1927),
    ObservationLocation("Siikajoki Ruukki",101787,5402,2803,64.68,25.09,48,"sää",1911),
    ObservationLocation("Siilinjärvi Kuopio lentoasema",101570,3601,2917,63.0,27.81,94,"sää",1945),
    ObservationLocation("Sipoo Itätoukki",105392,425,null,60.1,25.19,8,"sää",2017),
    ObservationLocation("Sodankylä Lokka",102000,8601,2719,67.82,27.75,240,"sää",1958),
    ObservationLocation("Sodankylä Tähtelä",101932,7501,2836,67.37,26.63,179,"sää,luotaus,ilmanlaatu(IL)",1891),
    ObservationLocation("Sodankylä Vuotso",102001,8602,2816,68.08,27.19,247,"sää",1958),
    ObservationLocation("Somero Salkola",101128,1227,2949,60.65,23.81,142,"sää",2010),
    ObservationLocation("Sotkamo Kuolaniemi",101756,4714,2739,64.11,28.34,160,"sää",1989),
    ObservationLocation("Sotkamo Tuhkakylä",107113,null,null,64.0,28.06,211,"sää",2019),
    ObservationLocation("Suomussalmi Pesiö",101826,5711,2889,64.93,28.75,222,"sää",1981),
    ObservationLocation("Taivalkoski kirkonkylä",101885,6707,2804,65.57,28.22,197,"sää",2002),
    ObservationLocation("Tampere Härmälä",101124,1222,2763,61.47,23.75,85,"sää",1948),
    ObservationLocation("Tampere Siilinkari",101311,2219,2943,61.52,23.75,96,"sää",1989),
    ObservationLocation("Tervola Loue",107201,6313,null,66.15,24.99,38,"sää",2020),
    ObservationLocation("Tohmajärvi Kemie",101459,2902,2832,62.24,30.35,91,"sää",1925),
    ObservationLocation("Toholampi Laitala",101689,4314,2737,63.82,24.16,84,"sää",1988),
    ObservationLocation("Tornio Torppi",101851,6312,2880,65.85,24.17,8,"sää",2008),
    ObservationLocation("Turku Artukainen",100949,118,2773,60.45,22.18,8,"sää",2003),
    ObservationLocation("Turku Rajakari",100947,116,2747,60.38,22.1,3,"sää",1991),
    ObservationLocation("Turku lentoasema",101065,1101,2972,60.52,22.28,45,"sää",1955),
    ObservationLocation("Utsjoki Kevo",102035,9603,2805,69.76,27.01,107,"sää,ilmanlaatu(IL)",1961),
    ObservationLocation("Utsjoki Kevo Kevojärvi",126737,9618,2890,69.76,27.01,76,"sää",2010),
    ObservationLocation("UtsjokiNuorgam",102036,9604,2825,70.08,27.9,22,"sää",1929),
    ObservationLocation("Vaala Pelso",101800,5502,2714,64.5,26.42,113,"sää",1943),
    ObservationLocation("Vaasa Klemettilä",101485,3024,2957,63.1,21.64,7,"sää",2010),
    ObservationLocation("Vaasa lentoasema",101462,3001,2911,63.06,21.75,4,"sää",1946),
    ObservationLocation("Vantaa Helsinki-Vantaan lentoasema",100968,301,2974,60.33,24.97,47,"sää",1952),
    ObservationLocation("Varkaus Kosulanniemi",101421,2625,2850,62.32,27.91,83,"sää",2007),
    ObservationLocation("Vesanto kirkonkylä",101555,3502,2710,62.92,26.42,121,"sää",1914),
    ObservationLocation("Vieremä Kaarakkala",101726,4602,2834,63.84,27.22,206,"sää",1937),
    ObservationLocation("Vihti Maasoja",100976,309,2758,60.42,24.4,39,"sää",1938),
    ObservationLocation("Viitasaari Haapaniemi",101537,3401,2915,63.08,25.86,130,"sää",1969),
    ObservationLocation("Virolahti Koivuniemi",101231,1612,2831,60.53,27.67,5,"sää",1976),
    ObservationLocation("Virrat Äijänneva",101310,2218,2735,62.33,23.54,138,"sää",1986),
    ObservationLocation("Ylitornio Meltosjärvi",101908,7301,2812,66.53,24.65,93,"sää",1935),
    ObservationLocation("Ylivieska lentokenttä",101690,4315,2755,64.05,24.72,76,"sää",2000),
    ObservationLocation("Ähtäri Inha",101520,3301,2924,62.55,24.14,161,"sää",1912),
)
        /*

ObservationLocation("Kittilä Lompolonvuoma	778135	8303	2764	68	24.21	270	sää, ilmanlaatu (IL)	2013
ObservationLocation("Kittilä Matorova	101985	8310	2882	68	24.24	339	sää, ilmanlaatu (IL)	1995
ObservationLocation("Kittilä Pakatti	101990	8315	2860	67.67	24.93	179	sää	2009
ObservationLocation("Kittilä Pokka	101994	8404	2717	68.17	25.78	275	sää	1971
ObservationLocation("Kittilä lentoasema	101986	8311	2720	67.69	24.86	195	sää	1999
ObservationLocation("Kokemäki Tulkkila	101103	1140	2937	61.25	22.35	38	sää	1912
ObservationLocation("Kokkola Santahaka	101675	4214	2852	63.84	23.1	5	sää	2007
ObservationLocation("Kokkola Tankar	101661	4109	2721	63.95	22.85	5	sää	1889
ObservationLocation("Korsnäs Bredskäret	101479	3018	2780	62.93	21.18	5	sää	1991
ObservationLocation("Kotka Haapasaari	101042	601	2967	60.29	27.18	4	sää	1986
ObservationLocation("Kotka Rankki	101030	501	2976	60.38	26.96	11	sää	1933
ObservationLocation("Kouvola Anjala	101194	1504	2830	60.7	26.81	31	sää	1941
ObservationLocation("Kouvola Utti Lentoportintie	101219	1529	2956	60.89	26.93	96	sää	1944
ObservationLocation("Kouvola Utti lentoasema	101191	1501	2966	60.9	26.95	102	sää	1944
ObservationLocation("Kristiinankaupunki Majakka	101268	2014	2752	62.2	21.17	0	sää	1997
ObservationLocation("Kruunupyy Kokkola-Pietarsaari lentoasema	101662	4201	2903	63.73	23.14	25	sää	1960
ObservationLocation("Kuhmo Kalliojoki	101773	4901	2799	64.3	30.17	196	sää	1997
ObservationLocation("Kumlinge kirkonkylä	100928	23	2790	60.26	20.75	23	sää	2000
ObservationLocation("Kuopio Maaninka	101572	3603	2788	63.14	27.31	91	sää	1930
ObservationLocation("Kuopio Ritoniemi	101580	3611	2732	62.8	27.9	86	sää	1980
ObservationLocation("Kuopio Savilahti	101586	3617	2955	62.89	27.63	87	sää	2005
ObservationLocation("Kustavi Isokari	101059	1016	2964	60.72	21.03	5	sää	1974
ObservationLocation("Kuusamo Juuma	101899	6814	2749	66.32	29.4	298	sää, ilmanlaatu (IL)	1989
ObservationLocation("Kuusamo Kiutaköngäs	101887	6802	2811	66.37	29.31	167	sää	1966
ObservationLocation("Kuusamo Ruka Talvijärvi	806428	6817	2760	66.17	29.14	306	sää	2013
ObservationLocation("Kuusamo Rukatunturi	101897	6812	2868	66.17	29.15	497	sää	1990
ObservationLocation("Kuusamo Välikangas	107081			66	29.23	268	sää	2019
ObservationLocation("Kuusamo lentoasema	101886	6801	2869	65.99	29.23	265	sää	1908
ObservationLocation("Kökar Bogskär	100921	16	2979	59.5	20.35	4	sää	1982
ObservationLocation("Lahti Sopenkorpi	104796	1441		60.97	25.62	100	sää	2016
ObservationLocation("Lappeenranta Hiekkapakka	101252	1716	2919	61.2	28.47	77	sää	2009
ObservationLocation("Lappeenranta Konnunsuo	101246	1710	2733	61.04	28.56	46	sää	1991
ObservationLocation("Lappeenranta lentoasema	101237	1701	2958	61.04	28.13	104	sää	1950
ObservationLocation("Lemland Nyhamn	100909	3	2980	59.96	19.95	9	sää	1958
ObservationLocation("Lieksa Lampela	101636	3904	2796	63.32	30.05	99	sää	1946
ObservationLocation("Liperi Joensuu lentoasema	101608	3801	2929	62.66	29.64	112	sää	1955
ObservationLocation("Liperi Tuiskavanluoto	101628	3821	2793	62.55	29.67	78	sää	1997
ObservationLocation("Lohja Porla	100974	307	2706	60.24	24.05	35	sää	1949
ObservationLocation("Loviisa Orrengrund	101039	510	2992	60.27	26.45	1	sää	1974
ObservationLocation("Luhanka Judinsalo	101362	2426	2765	61.7	25.51	80	sää	1990
ObservationLocation("Lumparland Långnäs satama	151048	12	2724	60.12	20.3	10	sää	2012
ObservationLocation("Maalahti Strömmingsbådan	101481	3020	2781	62.98	20.74	2	sää	1997
ObservationLocation("Maarianhamina Lotsberget	107383			60.09	19.94	40	sää	2021
ObservationLocation("Maarianhamina Länsisatama	151029	26	2997	60.09	19.93	2	sää	2012
ObservationLocation("Mikkeli Lentoasema AWOS	855522			61.69	27.21	101	sää	1959
ObservationLocation("Mikkeli lentoasema	101398	2602	2947	61.69	27.2	99	sää	1951
ObservationLocation("Multia Karhila	101536	3317	2927	62.51	24.81	227	sää	2008
ObservationLocation("Muonio Laukukero	101982	8307	2820	68.06	24.03	760	sää	1996
ObservationLocation("Muonio Oustajärvi	106435	8211	2823	67.95	23.71	240	sää	2013
ObservationLocation("Muonio Sammaltunturi	101983	8308	2821	67.97	24.12	555	sää, ilmanlaatu (IL)	1991
ObservationLocation("Mustasaari Valassaaret	101464	3003	2910	63.44	21.07	4	sää	1895
ObservationLocation("Mäntsälä Hirvihaara	103794	1440	2774	60.63	25.19	83	sää	2015
ObservationLocation("Nurmes Valtimo	101743	4701	2798	63.67	28.83	114	sää	1969
ObservationLocation("Nurmijärvi Röykkä	101149	1321	2983	60.51	24.65	110	sää	1952
ObservationLocation("Oulu Kaukovainio	108040			65	25.52	13	sää	2023
ObservationLocation("Oulu Vihreäsaari satama	101794	5409	2876	65.01	25.39	3	sää	1996
ObservationLocation("Oulu lentoasema	101786	5401	2875	64.94	25.34	13	sää	1953
ObservationLocation("Parainen Fagerholm	100924	19	2950	60.11	21.7	3	sää	1989
ObservationLocation("Parainen Utö	100908	2	2981	59.78	21.37	6	sää, sade, ilmanlaatu (IL)	1881
ObservationLocation("Parikkala Koitsanlahti	101254	1802	2734	61.44	29.46	74	sää	1991
ObservationLocation("Pelkosenniemi Pyhätunturi	101958	7708	2705	67.02	27.22	489	sää	1995
ObservationLocation("Pello kirkonkylä	101914	7307	2844	66.77	23.96	86	sää	1970
ObservationLocation("Pietarsaari Kallan	101660	4108	2920	63.75	22.52	2	sää	1995
ObservationLocation("Pirkkala Tampere-Pirkkala lentoasema	101118	1215	2944	61.42	23.62	112	sää	1979
ObservationLocation("Pori Tahkoluoto satama	101267	2013	2751	61.63	21.38	3	sää	1996
ObservationLocation("Pori lentoasema	101044	1001	2952	61.46	21.81	11	sää	1945
ObservationLocation("Pori rautatieasema	101064	1021	2926	61.48	21.78	9	sää	2008
ObservationLocation("Porvoo Emäsalo	101023	417	2991	60.2	25.63	19	sää	1974
ObservationLocation("Porvoo Harabacka	101028	422	2759	60.39	25.61	22	sää	2006
ObservationLocation("Porvoo Kalbådagrund	101022	416	2987	59.99	25.6	0	sää	1977
ObservationLocation("Porvoo Kilpilahti satama	100683	424	2994	60.3	25.55	2	sää	2014
ObservationLocation("Pudasjärvi lentokenttä	101805	5507	2866	65.4	26.96	120	sää	1999
ObservationLocation("Puolanka Paljakka	101831	5716	2859	64.66	28.06	341	sää	2009
ObservationLocation("Puumala kirkonkylä	150168	2708	2718	61.52	28.18	98	sää	2012
ObservationLocation("Pyhtää lentokenttä	107029	513		60.49	26.59	21	sää	2019
ObservationLocation("Pyhäjärvi Ojakylä	101705	4412	2738	63.74	25.71	152	sää	1990
ObservationLocation("Raahe Lapaluoto satama	101785	5311	2872	64.67	24.41	2	sää	1990
ObservationLocation("Raahe Nahkiainen	101775	5201	2800	64.61	23.9	0	sää	1997
ObservationLocation("Raasepori Jussarö	100965	215	2757	59.82	23.57	17	sää	1990
ObservationLocation("Rantasalmi Rukkasluoto	101436	2716	2772	62.06	28.57	79	sää	1997
ObservationLocation("Ranua Aho	108133			66.15	26.11	223	sää	2024
ObservationLocation("Ranua lentokenttä	101873	6514	2881	65.98	26.37	161	sää	2008
ObservationLocation("Rauma Kylmäpihlaja	101061	1018	2761	61.14	21.3	4	sää	1990
ObservationLocation("Rauma Pyynpää	105427	1023		61.14	21.52	15	sää	2018
ObservationLocation("Rautavaara Ylä-Luosta	101603	3716	2789	63.38	28.66	164	sää, sade	1976
ObservationLocation("Rovaniemi Apukka	101933	7502	2813	66.58	26.01	106	sää	1939
ObservationLocation("Rovaniemi lentoasema	101920	7401	2845	66.56	25.84	189	sää	1951
ObservationLocation("Rovaniemi lentoasema AWOS	137190			66.57	25.85	187	sää	1999
ObservationLocation("Rovaniemi rautatieasema	101928	7409	2847	66.5	25.71	85	sää	1997
ObservationLocation("Salla Naruska	101966	7804	2745	67.16	29.18	213	sää	1999
ObservationLocation("Salla Värriötunturi	102012	8803	2819	67.75	29.61	360	sää, sade, ilmanlaatu (IL), ilmanlaatu (kaupungit)	1971
ObservationLocation("Salla kirkonkylä	101959	7709	2849	66.82	28.69	218	sää	1960
ObservationLocation("Salo Kiikala lentokenttä	100967	217	2777	60.46	23.65	117	sää	2002
ObservationLocation("Salo Kärkkä	100955	205	2756	60.37	23.11	3	sää	1936
ObservationLocation("Savonlinna Punkaharju Laukansaari	101441	2801	2778	61.8	29.32	79	sää	1904
ObservationLocation("Savonlinna lentoasema	101430	2710	2948	61.95	28.93	92	sää	1974
ObservationLocation("Savukoski Tulppio	107565			67.77	29.21	217	sää	2022
ObservationLocation("Savukoski kirkonkylä	101952	7702	2815	67.28	28.18	174	sää	1934
ObservationLocation("Seinäjoki Pelmaa	101486	3101	2833	62.94	22.49	26	sää	1927
ObservationLocation("Siikajoki Ruukki	101787	5402	2803	64.68	25.09	48	sää	1911
ObservationLocation("Siilinjärvi Kuopio lentoasema	101570	3601	2917	63	27.81	94	sää	1945
ObservationLocation("Sipoo Itätoukki	105392	425		60.1	25.19	8	sää	2017
ObservationLocation("Sodankylä Lokka	102000	8601	2719	67.82	27.75	240	sää	1958
ObservationLocation("Sodankylä Tähtelä	101932	7501	2836	67.37	26.63	179	sää, luotaus, ilmanlaatu (IL)	1891
ObservationLocation("Sodankylä Vuotso	102001	8602	2816	68.08	27.19	247	sää	1958
ObservationLocation("Somero Salkola	101128	1227	2949	60.65	23.81	142	sää	2010
ObservationLocation("Sotkamo Kuolaniemi	101756	4714	2739	64.11	28.34	160	sää	1989
ObservationLocation("Sotkamo Tuhkakylä	107113			64	28.06	211	sää	2019
ObservationLocation("Suomussalmi Pesiö	101826	5711	2889	64.93	28.75	222	sää	1981
ObservationLocation("Taivalkoski kirkonkylä	101885	6707	2804	65.57	28.22	197	sää	2002
ObservationLocation("Tampere Härmälä	101124	1222	2763	61.47	23.75	85	sää	1948
ObservationLocation("Tampere Siilinkari	101311	2219	2943	61.52	23.75	96	sää	1989
ObservationLocation("Tervola Loue	107201	6313		66.15	24.99	38	sää	2020
ObservationLocation("Tohmajärvi Kemie	101459	2902	2832	62.24	30.35	91	sää	1925
ObservationLocation("Toholampi Laitala	101689	4314	2737	63.82	24.16	84	sää	1988
ObservationLocation("Tornio Torppi	101851	6312	2880	65.85	24.17	8	sää	2008
ObservationLocation("Turku Artukainen	100949	118	2773	60.45	22.18	8	sää	2003
ObservationLocation("Turku Rajakari	100947	116	2747	60.38	22.1	3	sää	1991
ObservationLocation("Turku lentoasema	101065	1101	2972	60.52	22.28	45	sää	1955
ObservationLocation("Utsjoki Kevo	102035	9603	2805	69.76	27.01	107	sää, ilmanlaatu (IL)	1961
ObservationLocation("Utsjoki Kevo Kevojärvi	126737	9618	2890	69.76	27.01	76	sää	2010
ObservationLocation("Utsjoki Nuorgam	102036	9604	2825	70.08	27.9	22	sää	1929
ObservationLocation("Vaala Pelso	101800	5502	2714	64.5	26.42	113	sää	1943
ObservationLocation("Vaasa Klemettilä	101485	3024	2957	63.1	21.64	7	sää	2010
ObservationLocation("Vaasa lentoasema	101462	3001	2911	63.06	21.75	4	sää	1946
ObservationLocation("Vantaa Helsinki-Vantaan lentoasema	100968	301	2974	60.33	24.97	47	sää	1952
ObservationLocation("Varkaus Kosulanniemi	101421	2625	2850	62.32	27.91	83	sää	2007
ObservationLocation("Vesanto kirkonkylä	101555	3502	2710	62.92	26.42	121	sää	1914
ObservationLocation("Vieremä Kaarakkala	101726	4602	2834	63.84	27.22	206	sää	1937
ObservationLocation("Vihti Maasoja	100976	309	2758	60.42	24.4	39	sää	1938
ObservationLocation("Viitasaari Haapaniemi	101537	3401	2915	63.08	25.86	130	sää	1969
ObservationLocation("Virolahti Koivuniemi	101231	1612	2831	60.53	27.67	5	sää	1976
ObservationLocation("Virrat Äijänneva	101310	2218	2735	62.33	23.54	138	sää	1986
ObservationLocation("Ylitornio Meltosjärvi	101908	7301	2812	66.53	24.65	93	sää	1935
ObservationLocation("Ylivieska lentokenttä	101690	4315	2755	64.05	24.72	76	sää	2000
ObservationLocation("Ähtäri Inha	101520	3301	2924	62.55	24.14	161	sää	1912

         */