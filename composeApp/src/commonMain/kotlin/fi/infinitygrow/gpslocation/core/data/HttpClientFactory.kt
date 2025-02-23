package fi.infinitygrow.gpslocation.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.xml.xml
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML

object HttpClientFactory {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                xml(format = XML {
                    xmlDeclMode = XmlDeclMode.Charset
                })
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Xml)
            }
        }
    }
}

/*
public final class DefaultRequest

Sets default request parameters. Used to add common headers and URL for a request. Note that trailing slash in base URL and leading slash in request URL are important. The rules to calculate a final URL:
Request URL doesn't start with slash
Base URL ends with slash -> concat strings. Example: base = https:// example. com/ dir/, request = file. html, result = https:// example. com/ dir/ file. html
Base URL doesn't end with slash -> remove last path segment of base URL and concat strings. Example: base = https:// example. com/ dir/ deafult_file. html, request = file. html, result = https:// example. com/ dir/ file. html
Request URL starts with slash -> use request path as is. Example: base = https:// example. com/ dir/ deafult_file. html, request = / root/ file. html, result = https:// example. com/ root/ file. html
Headers of the builder will be pre-populated with request headers. You can use HeadersBuilder. contains, HeadersBuilder. appendIfNameAbsent and HeadersBuilder. appendIfNameAndValueAbsent to avoid appending some header twice.
Usage:
val client = HttpClient {   defaultRequest {     url("https:// base. url/ dir/")     headers. appendIfNameAbsent(HttpHeaders. ContentType, ContentType. Application. Json)   } } client. get("file")   // <- requests "https:// base. url/ dir/ file", ContentType = Application. Json client. get("/ other_root/ file")   // <- requests "https:// base. url/ other_root/ file", ContentType = Application. Json client. get("// other. host/ path")   // <- requests "https:// other. host/ path", ContentType = Application. Json client. get("https:// some. url") { HttpHeaders. ContentType = ContentType. Application. Xml }   // <- requests "https:// some. url/", ContentType = Application. Xml
  io. ktor. client. plugins   DefaultRequest. kt
 */

/*
<wfs:FeatureCollection xmlns:wfs="http://www.opengis.net/wfs/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:om="http://www.opengis.net/om/2.0" xmlns:ompr="http://inspire.ec.europa.eu/schemas/ompr/3.0" xmlns:omso="http://inspire.ec.europa.eu/schemas/omso/3.0" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:gmlcov="http://www.opengis.net/gmlcov/1.0" xmlns:sam="http://www.opengis.net/sampling/2.0" xmlns:sams="http://www.opengis.net/samplingSpatial/2.0" xmlns:target="http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1" timeStamp="2025-02-21T21:23:46Z" numberMatched="1" numberReturned="1" xsi:schemaLocation="http://www.opengis.net/wfs/2.0 http://schemas.opengis.net/wfs/2.0/wfs.xsd http://www.opengis.net/gmlcov/1.0 http://schemas.opengis.net/gmlcov/1.0/gmlcovAll.xsd http://www.opengis.net/sampling/2.0 http://schemas.opengis.net/sampling/2.0/samplingFeature.xsd http://www.opengis.net/samplingSpatial/2.0 http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd http://www.opengis.net/swe/2.0 http://schemas.opengis.net/sweCommon/2.0/swe.xsd http://inspire.ec.europa.eu/schemas/ompr/3.0 https://inspire.ec.europa.eu/schemas/ompr/3.0/Processes.xsd http://inspire.ec.europa.eu/schemas/omso/3.0 https://inspire.ec.europa.eu/schemas/omso/3.0/SpecialisedObservations.xsd http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1 https://xml.fmi.fi/schema/om/atmosphericfeatures/1.1/atmosphericfeatures.xsd">
<wfs:member>
<omso:GridSeriesObservation gml:id="WFS-KqY3BrBiMKP0qqj4uQpxJHVRO1GJTowroWbbpdOt.Lnl5dsPTTv3c3Trvlw9NGXk6dbeuzpp4b9O7pj39svLDnywtLFlz6d1TTty2qf4UTV0_ghNbFv67smndnhb_GMboGTkDHCBYBOQDGx8udakWhTjunTRk1cM7LuyVNO3Lap_hRNXSWuIzteXz338slTo15fPffyyX9_bLy78tPTDi2ZYmZsw9MvPpEzNm_Hh2Za1M2m_GkruvTM4a23D4iaefTDux5aVq6EBpbcPiLw349HOcGOZvbcvTLvoYeWHbl6ZeXOsboy21od.d9iw26d1aHfPfYsNundWh3yX2LDbp3VlcL_PLha23Tz56d2epl8dKxp2Gc2t3XbPzU.mHpp37uc4TW49cOzT08yd2bfE1ufTD00791Tzwy1ob.GXdkw9MLc59N_LLk49cvLzf05K3Qs23S6db8XPLy7Yemnfu5unXfLh6aMvJ0629dnTTw36d3THv7ZeWHPlaHTTty0.mXhM0Omnbltb92WsarUhgA--">
<om:phenomenonTime>
<gml:TimePeriod gml:id="time1-1-1">
<gml:beginPosition>2025-02-21T09:23:00Z</gml:beginPosition>
<gml:endPosition>2025-02-21T21:23:00Z</gml:endPosition>
</gml:TimePeriod>
</om:phenomenonTime>
<om:resultTime>
<gml:TimeInstant gml:id="time2-1-1">
<gml:timePosition>2025-02-21T21:23:00Z</gml:timePosition>
</gml:TimeInstant>
</om:resultTime>
<om:procedure xlink:href="http://xml.fmi.fi/inspire/process/opendata"/>
<om:parameter>
<om:NamedValue>...</om:NamedValue>
</om:parameter>
<om:observedProperty xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=t2m,ws_10min,wg_10min,wd_10min,p_sea&language=eng"/>
<om:featureOfInterest>
<sams:SF_SpatialSamplingFeature gml:id="sampling-feature-1-1-fmisid">
<sam:sampledFeature>
<target:LocationCollection gml:id="sampled-target-1-1">
<target:member>
<target:Location gml:id="obsloc-fmisid-100955-pos">
<gml:identifier codeSpace="http://xml.fmi.fi/namespace/stationcode/fmisid">100955</gml:identifier>
<gml:name codeSpace="http://xml.fmi.fi/namespace/locationcode/name">Salo Kärkkä</gml:name>
<gml:name codeSpace="http://xml.fmi.fi/namespace/locationcode/geoid">-16000012</gml:name>
<gml:name codeSpace="http://xml.fmi.fi/namespace/locationcode/wmo">2756</gml:name>
<target:representativePoint xlink:href="#point-100955"/>
<target:region codeSpace="http://xml.fmi.fi/namespace/location/region">Salo</target:region>
</target:Location>
</target:member>
<target:member>...</target:member>
<target:member>...</target:member>
<target:member>...</target:member>
</target:LocationCollection>
</sam:sampledFeature>
<sams:shape>
<gml:MultiPoint gml:id="mp-1-1-fmisid">
<gml:pointMember>
<gml:Point gml:id="point-100955" srsName="http://www.opengis.net/def/crs/EPSG/0/4258" srsDimension="2">
<gml:name>Salo Kärkkä</gml:name>
<gml:pos>60.37392 23.11292</gml:pos>
</gml:Point>
</gml:pointMember>
<gml:pointMember>...</gml:pointMember>
<gml:pointMember>...</gml:pointMember>
<gml:pointMember>...</gml:pointMember>
</gml:MultiPoint>
</sams:shape>
</sams:SF_SpatialSamplingFeature>
</om:featureOfInterest>
<om:result>
<gmlcov:MultiPointCoverage gml:id="mpcv1-1-1">
<gml:domainSet>
<gmlcov:SimpleMultiPoint gml:id="mp1-1-1" srsName="http://xml.fmi.fi/gml/crs/compoundCRS.php?crs=4258&time=unixtime" srsDimension="3">
<gmlcov:positions>
60.37392 23.11292 1740130200 60.37392 23.11292 1740130800 60.37392 23.11292 1740131400 60.37392 23.11292 1740132000 60.37392 23.11292 1740132600 60.37392 23.11292 1740133200 60.37392 23.11292 1740133800 60.37392 23.11292 1740134400 60.37392 23.11292 1740135000 60.37392 23.11292 1740135600 60.37392 23.11292 1740136200 60.37392 23.11292 1740136800 60.37392 23.11292 1740137400 60.37392 23.11292 1740138000 60.37392 23.11292 1740138600 60.37392 23.11292 1740139200 60.37392 23.11292 1740139800 60.37392 23.11292 1740140400 60.37392 23.11292 1740141000 60.37392 23.11292 1740141600 60.37392 23.11292 1740142200 60.37392 23.11292 1740142800 60.37392 23.11292 1740143400 60.37392 23.11292 1740144000 60.37392 23.11292 1740144600 60.37392 23.11292 1740145200 60.37392 23.11292 1740145800 60.37392 23.11292 1740146400 60.37392 23.11292 1740147000 60.37392 23.11292 1740147600 60.37392 23.11292 1740148200 60.37392 23.11292 1740148800 60.37392 23.11292 1740149400 60.37392 23.11292 1740150000 60.37392 23.11292 1740150600 60.37392 23.11292 1740151200 60.37392 23.11292 1740151800 60.37392 23.11292 1740152400 60.37392 23.11292 1740153000 60.37392 23.11292 1740153600 60.37392 23.11292 1740154200 60.37392 23.11292 1740154800 60.37392 23.11292 1740155400 60.37392 23.11292 1740156000 60.37392 23.11292 1740156600 60.37392 23.11292 1740157200 60.37392 23.11292 1740157800 60.37392 23.11292 1740158400 60.37392 23.11292 1740159000 60.37392 23.11292 1740159600 60.37392 23.11292 1740160200 60.37392 23.11292 1740160800 60.37392 23.11292 1740161400 60.37392 23.11292 1740162000 60.37392 23.11292 1740162600 60.37392 23.11292 1740163200 60.37392 23.11292 1740163800 60.37392 23.11292 1740164400 60.37392 23.11292 1740165000 60.37392 23.11292 1740165600 60.37392 23.11292 1740166200 60.37392 23.11292 1740166800 60.37392 23.11292 1740167400 60.37392 23.11292 1740168000 60.37392 23.11292 1740168600 60.37392 23.11292 1740169200 60.37392 23.11292 1740169800 60.37392 23.11292 1740170400 60.37392 23.11292 1740171000 60.37392 23.11292 1740171600 60.37392 23.11292 1740172200 60.37392 23.11292 1740172800 60.46415 23.64976 1740130200 60.46415 23.64976 1740130800 60.46415 23.64976 1740131400 60.46415 23.64976 1740132000 60.46415 23.64976 1740132600 60.46415 23.64976 1740133200 60.46415 23.64976 1740133800 60.46415 23.64976 1740134400 60.46415 23.64976 1740135000 60.46415 23.64976 1740135600 60.46415 23.64976 1740136200 60.46415 23.64976 1740136800 60.46415 23.64976 1740137400 60.46415 23.64976 1740138000 60.46415 23.64976 1740138600 60.46415 23.64976 1740139200 60.46415 23.64976 1740139800 60.46415 23.64976 1740140400 60.46415 23.64976 1740141000 60.46415 23.64976 1740141600 60.46415 23.64976 1740142200 60.46415 23.64976 1740142800 60.46415 23.64976 1740143400 60.46415 23.64976 1740144000 60.46415 23.64976 1740144600 60.46415 23.64976 1740145200 60.46415 23.64976 1740145800 60.46415 23.64976 1740146400 60.46415 23.64976 1740147000 60.46415 23.64976 1740147600 60.46415 23.64976 1740148200 60.46415 23.64976 1740148800 60.46415 23.64976 1740149400 60.46415 23.64976 1740150000 60.46415 23.64976 1740150600 60.46415 23.64976 1740151200 60.46415 23.64976 1740151800 60.46415 23.64976 1740152400 60.46415 23.64976 1740153000 60.46415 23.64976 1740153600 60.46415 23.64976 1740154200 60.46415 23.64976 1740154800 60.46415 23.64976 1740155400 60.46415 23.64976 1740156000 60.46415 23.64976 1740156600 60.46415 23.64976 1740157200 60.46415 23.64976 1740157800 60.46415 23.64976 1740158400 60.46415 23.64976 1740159000 60.46415 23.64976 1740159600 60.46415 23.64976 1740160200 60.46415 23.64976 1740160800 60.46415 23.64976 1740161400 60.46415 23.64976 1740162000 60.46415 23.64976 1740162600 60.46415 23.64976 1740163200 60.46415 23.64976 1740163800 60.46415 23.64976 1740164400 60.46415 23.64976 1740165000 60.46415 23.64976 1740165600 60.46415 23.64976 1740166200 60.46415 23.64976 1740166800 60.46415 23.64976 1740167400 60.46415 23.64976 1740168000 60.46415 23.64976 1740168600 60.46415 23.64976 1740169200 60.46415 23.64976 1740169800 60.46415 23.64976 1740170400 60.46415 23.64976 1740171000 60.46415 23.64976 1740171600 60.46415 23.64976 1740172200 60.46415 23.64976 1740172800 60.81397 23.49825 1740130200 60.81397 23.49825 1740130800 60.81397 23.49825 1740131400 60.81397 23.49825 1740132000 60.81397 23.49825 1740132600 60.81397 23.49825 1740133200 60.81397 23.49825 1740133800 60.81397 23.49825 1740134400 60.81397 23.49825 1740135000 60.81397 23.49825 1740135600 60.81397 23.49825 1740136200 60.81397 23.49825 1740136800 60.81397 23.49825 1740137400 60.81397 23.49825 1740138000 60.81397 23.49825 1740138600 60.81397 23.49825 1740139200 60.81397 23.49825 1740139800 60.81397 23.49825 1740140400 60.81397 23.49825 1740141000 60.81397 23.49825 1740141600 60.81397 23.49825 1740142200 60.81397 23.49825 1740142800 60.81397 23.49825 1740143400 60.81397 23.49825 1740144000 60.81397 23.49825 1740144600 60.81397 23.49825 1740145200 60.81397 23.49825 1740145800 60.81397 23.49825 1740146400 60.81397 23.49825 1740147000 60.81397 23.49825 1740147600 60.81397 23.49825 1740148200 60.81397 23.49825 1740148800 60.81397 23.49825 1740149400 60.81397 23.49825 1740150000 60.81397 23.49825 1740150600 60.81397 23.49825 1740151200 60.81397 23.49825 1740151800 60.81397 23.49825 1740152400 60.81397 23.49825 1740153000 60.81397 23.49825 1740153600 60.81397 23.49825 1740154200 60.81397 23.49825 1740154800 60.81397 23.49825 1740155400 60.81397 23.49825 1740156000 60.81397 23.49825 1740156600 60.81397 23.49825 1740157200 60.81397 23.49825 1740157800 60.81397 23.49825 1740158400 60.81397 23.49825 1740159000 60.81397 23.49825 1740159600 60.81397 23.49825 1740160200 60.81397 23.49825 1740160800 60.81397 23.49825 1740161400 60.81397 23.49825 1740162000 60.81397 23.49825 1740162600 60.81397 23.49825 1740163200 60.81397 23.49825 1740163800 60.81397 23.49825 1740164400 60.81397 23.49825 1740165000 60.81397 23.49825 1740165600 60.81397 23.49825 1740166200 60.81397 23.49825 1740166800 60.81397 23.49825 1740167400 60.81397 23.49825 1740168000 60.81397 23.49825 1740168600 60.81397 23.49825 1740169200 60.81397 23.49825 1740169800 60.81397 23.49825 1740170400 60.81397 23.49825 1740171000 60.81397 23.49825 1740171600 60.81397 23.49825 1740172200 60.81397 23.49825 1740172800 60.64668 23.80559 1740130200 60.64668 23.80559 1740130800 60.64668 23.80559 1740131400 60.64668 23.80559 1740132000 60.64668 23.80559 1740132600 60.64668 23.80559 1740133200 60.64668 23.80559 1740133800 60.64668 23.80559 1740134400 60.64668 23.80559 1740135000 60.64668 23.80559 1740135600 60.64668 23.80559 1740136200 60.64668 23.80559 1740136800 60.64668 23.80559 1740137400 60.64668 23.80559 1740138000 60.64668 23.80559 1740138600 60.64668 23.80559 1740139200 60.64668 23.80559 1740139800 60.64668 23.80559 1740140400 60.64668 23.80559 1740141000 60.64668 23.80559 1740141600 60.64668 23.80559 1740142200 60.64668 23.80559 1740142800 60.64668 23.80559 1740143400 60.64668 23.80559 1740144000 60.64668 23.80559 1740144600 60.64668 23.80559 1740145200 60.64668 23.80559 1740145800 60.64668 23.80559 1740146400 60.64668 23.80559 1740147000 60.64668 23.80559 1740147600 60.64668 23.80559 1740148200 60.64668 23.80559 1740148800 60.64668 23.80559 1740149400 60.64668 23.80559 1740150000 60.64668 23.80559 1740150600 60.64668 23.80559 1740151200 60.64668 23.80559 1740151800 60.64668 23.80559 1740152400 60.64668 23.80559 1740153000 60.64668 23.80559 1740153600 60.64668 23.80559 1740154200 60.64668 23.80559 1740154800 60.64668 23.80559 1740155400 60.64668 23.80559 1740156000 60.64668 23.80559 1740156600 60.64668 23.80559 1740157200 60.64668 23.80559 1740157800 60.64668 23.80559 1740158400 60.64668 23.80559 1740159000 60.64668 23.80559 1740159600 60.64668 23.80559 1740160200 60.64668 23.80559 1740160800 60.64668 23.80559 1740161400 60.64668 23.80559 1740162000 60.64668 23.80559 1740162600 60.64668 23.80559 1740163200 60.64668 23.80559 1740163800 60.64668 23.80559 1740164400 60.64668 23.80559 1740165000 60.64668 23.80559 1740165600 60.64668 23.80559 1740166200 60.64668 23.80559 1740166800 60.64668 23.80559 1740167400 60.64668 23.80559 1740168000 60.64668 23.80559 1740168600 60.64668 23.80559 1740169200 60.64668 23.80559 1740169800 60.64668 23.80559 1740170400 60.64668 23.80559 1740171000 60.64668 23.80559 1740171600 60.64668 23.80559 1740172200 60.64668 23.80559 1740172800
</gmlcov:positions>
</gmlcov:SimpleMultiPoint>
</gml:domainSet>
<gml:rangeSet>
<gml:DataBlock>
<gml:rangeParameters/>
<gml:doubleOrNilReasonTupleList>
0.4 NaN NaN NaN NaN 0.4 NaN NaN NaN NaN 0.4 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.4 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.5 NaN NaN NaN NaN 0.2 NaN NaN NaN NaN -0.3 NaN NaN NaN NaN -0.4 NaN NaN NaN NaN -0.4 NaN NaN NaN NaN -0.1 NaN NaN NaN NaN -0.1 NaN NaN NaN NaN 0.0 NaN NaN NaN NaN 0.1 NaN NaN NaN NaN 0.0 NaN NaN NaN NaN 0.1 NaN NaN NaN NaN 0.4 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.6 NaN NaN NaN NaN 0.7 NaN NaN NaN NaN 0.8 NaN NaN NaN NaN 1.0 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.1 NaN NaN NaN NaN 1.1 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.3 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.3 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.2 NaN NaN NaN NaN 1.3 NaN NaN NaN NaN 1.3 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.4 NaN NaN NaN NaN 1.5 NaN NaN NaN NaN 1.5 NaN NaN NaN NaN 1.5 NaN NaN NaN NaN 1.5 NaN NaN NaN NaN 1.5 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.6 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.6 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.9 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.9 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.7 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN 1.8 NaN NaN NaN NaN -0.7 5.3 10.7 194.0 1025.2 -0.6 6.1 12.2 196.0 1025.2 -0.6 6.1 10.6 195.0 1025.2 -0.6 7.4 11.9 193.0 1025.3 -0.6 5.6 9.6 193.0 1025.2 -0.6 5.6 9.9 197.0 1025.2 -0.6 6.2 9.5 194.0 1025.1 -0.6 5.9 10.4 194.0 1025.0 -0.6 4.9 7.8 188.0 1024.9 -0.6 5.5 8.7 185.0 1024.7 -0.7 5.1 8.3 192.0 1024.8 -0.7 5.2 8.5 191.0 1024.9 -0.7 5.2 8.8 190.0 1024.9 -0.9 5.8 9.1 192.0 1024.8 -1.2 5.4 8.8 189.0 1024.6 -1.3 4.9 8.0 190.0 1024.7 -1.3 5.6 9.1 184.0 1024.6 -1.1 6.2 10.6 185.0 1024.5 -1.0 6.3 11.1 187.0 1024.4 -0.9 6.6 10.0 183.0 1024.3 -0.9 6.3 10.5 182.0 1024.3 -1.0 5.8 9.6 190.0 1024.4 -0.9 5.7 11.4 189.0 1024.3 -0.9 6.3 9.8 190.0 1024.2 -0.8 5.8 9.3 192.0 1024.1 -0.8 6.5 11.6 199.0 1024.1 -0.8 6.4 9.8 198.0 1024.1 -0.8 6.6 10.7 198.0 1024.2 -0.7 6.1 11.4 203.0 1024.3 -0.7 6.0 9.6 195.0 1024.2 -0.7 6.2 10.6 195.0 1024.1 -0.7 5.6 9.8 190.0 1024.1 -0.7 5.8 9.2 194.0 1024.1 -0.6 6.3 10.1 195.0 1023.9 -0.6 6.4 11.7 193.0 1023.9 -0.6 5.4 9.6 190.0 1023.8 -0.6 6.0 9.3 188.0 1023.8 -0.6 5.8 10.0 190.0 1023.9 -0.6 6.5 9.7 195.0 1024.0 -0.6 5.4 8.5 196.0 1023.9 -0.5 5.3 9.0 193.0 1023.9 -0.5 5.1 7.8 194.0 1023.8 -0.4 5.8 9.3 194.0 1023.7 -0.3 5.9 10.6 191.0 1023.5 -0.3 4.7 9.5 197.0 1023.6 -0.2 5.3 8.4 201.0 1023.7 -0.2 5.1 8.8 202.0 1023.8 -0.3 5.2 9.1 194.0 1023.8 -0.3 5.6 9.2 198.0 1023.9 -0.3 5.3 9.4 203.0 1023.9 -0.3 5.9 10.8 200.0 1023.9 -0.2 5.5 9.8 194.0 1024.0 -0.2 5.4 10.0 200.0 1024.1 -0.1 5.0 10.4 203.0 1024.1 -0.2 5.3 9.1 198.0 1024.1 -0.2 5.1 8.7 201.0 1024.1 -0.2 4.8 8.7 200.0 1024.1 -0.1 4.6 7.9 191.0 1024.1 0.0 4.8 8.2 195.0 1023.9 0.0 4.5 7.2 197.0 1023.7 0.1 4.4 7.8 193.0 1023.8 0.0 4.7 7.5 195.0 1023.8 0.0 4.6 8.0 182.0 1024.0 0.1 4.8 7.4 185.0 1023.8 0.1 4.2 6.8 193.0 1023.9 0.1 4.3 8.6 191.0 1023.7 0.1 4.4 6.5 199.0 1023.8 0.1 4.4 7.3 197.0 1023.7 0.1 4.3 6.5 193.0 1023.6 0.2 4.6 6.7 185.0 1023.5 0.2 4.1 6.6 190.0 1023.3 0.3 4.5 7.0 187.0 1023.4 -0.2 8.9 14.6 203.0 1023.9 -0.2 7.6 12.3 200.0 1023.9 -0.2 8.6 13.1 199.0 1023.9 -0.2 8.0 13.7 202.0 1024.0 -0.1 8.0 12.7 203.0 1024.0 -0.1 8.1 14.4 202.0 1023.9 0.0 7.6 12.1 200.0 1023.6 0.0 8.4 12.9 199.0 1023.7 0.0 7.6 11.3 202.0 1023.5 0.0 7.4 11.4 201.0 1023.5 0.0 7.5 11.1 200.0 1023.5 -0.3 7.8 12.6 201.0 1023.5 -0.6 6.9 11.1 199.0 1023.5 -0.8 6.6 10.2 195.0 1023.3 -0.8 7.0 11.5 191.0 1023.2 -0.8 7.1 10.5 191.0 1023.2 -0.7 7.1 10.9 193.0 1023.1 -0.5 7.5 12.9 197.0 1023.2 -0.5 8.0 12.7 192.0 1023.1 -0.6 6.4 10.3 193.0 1022.8 -0.5 6.9 10.7 194.0 1022.8 -0.5 6.6 10.1 194.0 1022.8 -0.5 7.9 13.8 200.0 1022.8 -0.4 7.4 11.9 201.0 1022.9 -0.2 7.8 12.8 205.0 1023.0 -0.1 7.0 11.4 203.0 1023.1 0.0 6.5 9.2 205.0 1023.0 0.0 6.1 9.7 208.0 1023.1 0.1 6.1 9.0 208.0 1023.0 0.2 5.8 9.6 210.0 1022.8 0.4 6.8 9.7 208.0 1022.7 0.4 6.3 9.6 210.0 1022.8 0.5 6.1 11.5 209.0 1022.8 0.5 7.0 10.7 210.0 1022.7 0.5 7.8 11.4 208.0 1022.5 0.5 7.0 10.4 207.0 1022.4 0.5 6.6 11.1 209.0 1022.6 0.5 7.7 12.2 208.0 1022.6 0.5 7.4 12.4 207.0 1022.6 0.5 7.1 11.3 206.0 1022.5 0.6 7.8 13.2 209.0 1022.5 0.6 7.8 11.3 210.0 1022.5 0.6 7.7 11.9 208.0 1022.4 0.6 6.8 11.0 208.0 1022.4 0.6 6.7 10.7 209.0 1022.6 0.6 5.3 8.1 211.0 1022.5 0.6 6.6 10.2 209.0 1022.5 0.6 6.2 10.6 208.0 1022.6 0.7 6.2 10.2 212.0 1022.9 0.8 5.9 9.7 210.0 1023.0 0.8 5.2 9.2 214.0 1023.0 0.9 5.1 7.9 211.0 1023.1 0.9 4.8 8.0 210.0 1023.0 1.0 5.6 9.6 207.0 1023.0 1.0 5.7 9.7 205.0 1022.9 1.0 5.3 7.9 210.0 1022.9 1.0 5.3 7.7 205.0 1022.8 1.0 6.1 10.0 206.0 1022.6 1.1 6.0 10.3 209.0 1022.6 1.1 5.8 14.2 210.0 1022.7 1.1 6.2 9.5 207.0 1022.7 1.1 5.7 8.6 206.0 1022.5 1.1 5.7 9.2 208.0 1022.5 1.1 6.4 10.2 205.0 1022.6 1.1 5.0 8.2 208.0 1022.7 1.0 6.3 9.2 209.0 1022.6 1.0 6.3 9.8 205.0 1022.5 1.0 6.8 11.8 211.0 1022.5 1.0 5.9 9.6 209.0 1022.5 1.0 6.0 9.8 206.0 1022.4 1.0 5.8 8.7 206.0 1022.4 1.0 5.5 8.3 208.0 1022.5 -0.9 4.0 11.4 196.0 NaN -0.9 4.3 10.6 198.0 NaN -0.8 4.7 12.2 200.0 NaN -0.8 4.4 9.9 204.0 NaN -0.8 4.3 11.1 207.0 NaN -0.8 4.1 11.7 205.0 NaN -0.8 4.2 9.6 205.0 NaN -0.9 4.7 10.6 207.0 NaN -0.7 3.7 10.3 205.0 NaN -0.8 4.0 10.7 200.0 NaN -0.8 3.6 9.1 204.0 NaN -0.8 3.9 8.2 200.0 NaN -0.9 3.5 9.1 204.0 NaN -0.9 4.0 10.5 202.0 NaN -1.1 3.6 8.4 198.0 NaN -1.4 3.6 10.0 194.0 NaN -1.5 3.1 7.9 190.0 NaN -1.5 3.8 8.8 190.0 NaN -1.4 3.7 9.1 190.0 NaN -1.3 3.6 8.7 191.0 NaN -1.2 3.7 8.9 196.0 NaN -1.2 4.2 10.9 194.0 NaN -1.2 4.0 9.5 195.0 NaN -1.2 3.4 8.5 193.0 NaN -1.1 4.1 9.4 200.0 NaN -1.0 5.2 11.8 207.0 NaN -0.9 4.2 11.1 208.0 NaN -0.9 5.0 12.5 209.0 NaN -0.9 5.0 12.0 207.0 NaN -0.9 4.6 10.0 204.0 NaN -0.9 3.7 9.5 205.0 NaN -0.9 3.8 9.5 205.0 NaN -0.8 3.4 7.8 206.0 NaN -0.9 3.2 8.2 205.0 NaN -0.8 3.5 10.9 197.0 NaN -0.8 3.2 8.4 204.0 NaN -0.7 3.8 8.9 201.0 NaN -0.7 4.2 9.2 205.0 NaN -0.7 2.8 7.9 206.0 NaN -0.7 4.0 10.2 207.0 NaN -0.6 3.9 9.4 204.0 NaN -0.6 4.1 10.1 204.0 NaN -0.6 4.6 9.9 207.0 NaN -0.6 4.2 9.9 209.0 NaN -0.6 4.0 8.8 213.0 NaN -0.5 3.6 8.4 211.0 NaN -0.4 3.7 10.6 207.0 NaN -0.3 4.0 9.2 206.0 NaN -0.2 4.0 9.7 205.0 NaN -0.1 3.5 7.8 209.0 NaN -0.1 3.2 7.4 208.0 NaN -0.1 3.3 8.4 207.0 NaN -0.1 3.8 9.4 214.0 NaN -0.1 3.5 7.9 205.0 NaN -0.1 4.0 8.7 205.0 NaN -0.1 3.8 8.0 210.0 NaN -0.1 3.5 9.0 210.0 NaN -0.1 3.1 9.1 203.0 NaN -0.1 3.2 9.4 199.0 NaN -0.1 3.4 7.5 206.0 NaN -0.1 2.6 7.8 202.0 NaN -0.1 2.7 6.8 205.0 NaN -0.1 3.7 8.6 207.0 NaN 0.0 3.7 8.8 211.0 NaN 0.0 3.1 7.3 209.0 NaN 0.0 3.0 7.0 204.0 NaN 0.0 3.4 7.3 208.0 NaN 0.0 3.4 7.9 209.0 NaN 0.0 3.1 7.8 206.0 NaN 0.0 2.7 6.5 204.0 NaN 0.0 3.2 6.4 197.0 NaN 0.0 3.0 6.8 200.0 NaN
</gml:doubleOrNilReasonTupleList>
</gml:DataBlock>
</gml:rangeSet>
<gml:coverageFunction>
<gml:CoverageMappingRule>
<gml:ruleDefinition>Linear</gml:ruleDefinition>
</gml:CoverageMappingRule>
</gml:coverageFunction>
<gmlcov:rangeType>
<swe:DataRecord>
<swe:field name="t2m" xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=t2m&language=eng"/>
<swe:field name="ws_10min" xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=ws_10min&language=eng"/>
<swe:field name="wg_10min" xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=wg_10min&language=eng"/>
<swe:field name="wd_10min" xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=wd_10min&language=eng"/>
<swe:field name="p_sea" xlink:href="https://opendata.fmi.fi/meta?observableProperty=observation&param=p_sea&language=eng"/>
</swe:DataRecord>
</gmlcov:rangeType>
</gmlcov:MultiPointCoverage>
</om:result>
</omso:GridSeriesObservation>
</wfs:member>
</wfs:FeatureCollection>

 */