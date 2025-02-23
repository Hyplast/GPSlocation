package fi.infinitygrow.gpslocation.data.model.observation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName


@Serializable
@XmlSerialName("FeatureCollection", "http://www.opengis.net/wfs/2.0","wfs")
data class ObservationDTO(
    @XmlElement(false)
    @SerialName("timeStamp")
    val timeStamp: String,
    //@XmlSerialName("member", "","wfs")
    @SerialName("wfs:member")
    val data: WfsMember
) {
    @Serializable
    @XmlSerialName("member", "","wfs")
    data class WfsMember(
        //@XmlSerialName("GridSeriesObservation","","omso",)
        //@SerialName("GridSeriesObservation")
        @XmlSerialName("GridSeriesObservation","http://inspire.ec.europa.eu/schemas/omso/3.0","omso",)
        val gridSeriesObservation: GridSeriesObservation
    )

    @Serializable
    @XmlSerialName("GridSeriesObservation","http://inspire.ec.europa.eu/schemas/omso/3.0","omso",)
    data class GridSeriesObservation(
        @XmlSerialName("phenomenonTime","http://www.opengis.net/om/2.0", "om")
        val phenomenonTime: PhenomenonTime,
        @XmlSerialName("resultTime","http://www.opengis.net/om/2.0","om")
        val resultTime: ResultTime,
//        @SerialName("om:procedure)
//        val procedure: Procedure,
//        @SerialName("om:parameter)
//        val parameter: Parameter,
//        @SerialName("om:observedProperty)
//        val observedProperty: observedProperty,
        @XmlSerialName("featureOfInterest", "http://www.opengis.net/om/2.0", "om")
        val featureOfInterest: FeatureOfInterest,
        @XmlSerialName("result","http://www.opengis.net/om/2.0", "om")
         val result: Result,
    )

    @Serializable
    @XmlSerialName("phenomenonTime","http://www.opengis.net/om/2.0", "om")
    data class PhenomenonTime(
        @XmlElement(true)
        @XmlSerialName("TimePeriod","http://www.opengis.net/gml/3.2","gml")
        val timePeriod: TimePeriod
    )

    @Serializable
    @XmlSerialName("TimePeriod","http://www.opengis.net/gml/3.2","gml")
    data class TimePeriod(
        @XmlElement(false) // Attribute instead of element
        @XmlSerialName("id", "http://www.opengis.net/gml/3.2", "gml")
        val id: String? = null, // Handles gml:id correctly

        @XmlElement(true)
        @XmlSerialName("beginPosition","","gml")
        val beginPosition: String, //@SerialName("gml:beginPosition")

        @XmlElement(true)
        @XmlSerialName("endPosition","http://www.opengis.net/gml/3.2","gml")
        val endPosition: String
    )

    @Serializable
    @XmlSerialName("beginPosition","http://www.opengis.net/gml/3.2","gml")
    data class BeginPosition(
        @XmlSerialName("beginPosition", "http://www.opengis.net/gml/3.2", "gml")
        val beginTime: String
    )

//    @Serializable
//    @XmlSerialName("endPosition","http://www.opengis.net/gml/3.2","gml")
//    data class EndPosition(
//        val endTime: String
//    )

    @Serializable
    @XmlSerialName("resultTime","http://www.opengis.net/om/2.0","om")
    data class ResultTime(
        @XmlSerialName("TimeInstant", "http://www.opengis.net/gml/3.2", "gml")
        val timeInstant: TimeInstant
    )

    @Serializable
    @XmlSerialName("gml:TimeInstant")
    data class TimeInstant(
        @XmlElement(true)
        @XmlSerialName("timePosition","http://www.opengis.net/gml/3.2","gml")
        val timePosition: String
    )

//    @Serializable
//    @XmlSerialName("gml:timePosition")
//    data class TimePosition(
//        val timePosition: String
//    )

    @Serializable
    @XmlSerialName("om:procedure")
    data class Procedure(
        @XmlElement(false)
        val xlinkhref: String
    )

    @Serializable
    @XmlSerialName("om:featureOfInterest")
    data class FeatureOfInterest(
        @XmlSerialName("SF_SpatialSamplingFeature","http://www.opengis.net/samplingSpatial/2.0","sams")
        val sam: Sam
    )

    @Serializable
    @XmlSerialName("SF_SpatialSamplingFeature","http://www.opengis.net/samplingSpatial/2.0","sams")
    data class Sam(
        @XmlSerialName("sampledFeature", "http://www.opengis.net/sampling/2.0", "sam")
        val sampledFeature: SampledFeature,
        @XmlSerialName("shape", "http://www.opengis.net/samplingSpatial/2.0", "sams")
        val shape: Shape
    )

    @Serializable
    @XmlSerialName("sampledFeature", "http://www.opengis.net/sampling/2.0", "sam")
    data class SampledFeature(
        @XmlSerialName("LocationCollection", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
        val locationCollection: LocationCollection
    )

    @Serializable
    @XmlSerialName("LocationCollection", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
    data class LocationCollection(
        @XmlSerialName("member", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
        val members: List<Member>
    )

    @Serializable
    @XmlSerialName("member", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
    data class Member(
        @XmlSerialName("Location", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
        val location: Location
    )

    @Serializable
    @XmlSerialName("Location", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
    data class Location(
        @XmlElement(true)
        @XmlSerialName("identifier", "http://www.opengis.net/gml/3.2", "gml")
        val fmiIdStationCode: String,

        @XmlElement(true)
        @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
        val names: List<String>,

        @XmlElement(true)
        @XmlSerialName("representativePoint", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
        val representativePoint: String,

        @XmlElement(true)
        @XmlSerialName("region", "http://xml.fmi.fi/namespace/om/atmosphericfeatures/1.1", "target")
        val region: String
        /*
        @XmlElement(true)
        @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
        val locationName: String,

        @XmlElement(true)
        @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
        val geoIdStationCode: String,

        @XmlElement(true)
        @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
        val wmoStationCode: String,*/
    )

    @Serializable
    @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
    data class NameEntry(
        @XmlElement(false) // Attribute instead of element
        @XmlSerialName("codeSpace", "", "")
        val codeSpace: String,

        @XmlElement(true)
        val value: String
    )



    @Serializable
    @XmlSerialName("shape", "http://www.opengis.net/samplingSpatial/2.0", "sams")
    data class Shape(
        @XmlSerialName("MultiPoint", "http://www.opengis.net/gml/3.2", "gml")
        val multiPoint: MultiPoint
    )

    @Serializable
    @XmlSerialName("MultiPoint", "http://www.opengis.net/gml/3.2", "gml")
    data class MultiPoint(
        @XmlSerialName("pointMember", "http://www.opengis.net/gml/3.2", "gml")
        val pointMembers: List<PointMember>
    )

    @Serializable
    @XmlSerialName("pointMember", "http://www.opengis.net/gml/3.2", "gml")
    data class PointMember(
        @XmlSerialName("Point", "http://www.opengis.net/gml/3.2", "gml")
        val point: Point
    )

    @Serializable
    @XmlSerialName("Point", "http://www.opengis.net/gml/3.2", "gml")
    data class Point(
        @XmlElement(true)
        @XmlSerialName("name", "http://www.opengis.net/gml/3.2", "gml")
        val locationName: String,

        @XmlElement(true)
        @XmlSerialName("pos", "http://www.opengis.net/gml/3.2", "gml")
        val locationPosition: String,
    )

    @Serializable
    @XmlSerialName("resul", "http://www.opengis.net/om/2.0", "om")
    data class Result(
        @XmlSerialName("MultiPointCoverage", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
        val multiPointCoverage: MultiPointCoverage
    )

    @Serializable
    @XmlSerialName("MultiPointCoverage", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
    data class MultiPointCoverage(
        @XmlSerialName("domainSet", "http://www.opengis.net/gml/3.2", "gml")
        val domainSet: DomainSet,
        @XmlSerialName("rangeSet", "http://www.opengis.net/gml/3.2", "gml")
        val rangeSet: RangeSet,
//        @SerialName("gml:coverageFunction")
//        val coverageFunction: CoverageFunction,
        @XmlSerialName("rangeType", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
        val rangeType: RangeType,
    )

    @Serializable
    @XmlSerialName("domainSet", "http://www.opengis.net/gml/3.2", "gml")
    data class DomainSet(
        @XmlSerialName("SimpleMultiPoint", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
        val simpleMultiPoint: SimpleMultiPoint
    )

    @Serializable
    @XmlSerialName("SimpleMultiPoint", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
    data class SimpleMultiPoint(
        @XmlElement(true)
        @XmlSerialName("positions", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
        val positions: String
    )

    @Serializable
    @XmlSerialName("rangeSet", "http://www.opengis.net/gml/3.2", "gml")
    data class RangeSet(
        @XmlSerialName("DataBlock", "http://www.opengis.net/gml/3.2", "gml")
        val dataBlock: DataBlock
    )

    @Serializable
    @XmlSerialName("DataBlock", "http://www.opengis.net/gml/3.2", "gml")
    data class DataBlock(
        @XmlElement(true)
        @XmlSerialName("doubleOrNilReasonTupleList", "http://www.opengis.net/gml/3.2", "gml")
        val tupleList: String
    )

    @Serializable
    @XmlSerialName("rangeType", "http://www.opengis.net/gmlcov/1.0", "gmlcov")
    data class RangeType(
        @XmlSerialName("DataRecord", "http://www.opengis.net/swe/2.0", "swe")
        val dataRecord: DataRecord
    )

    @Serializable
    @XmlSerialName("DataRecord", "http://www.opengis.net/swe/2.0", "swe")
    data class DataRecord(
        @XmlElement(false)
        @XmlSerialName("field", "http://www.opengis.net/swe/2.0", "swe")
        val fields: List<String>
    )
}
