package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import nl.adaptivity.xmlutil.serialization.XML


fun cleanString(input: String): String {
    // Replace all Unicode 10 (Line Feed) with Unicode 32 (Space)
    val cleaned = input.replace("\u000A", " ")

    // Replace multiple consecutive spaces with a single space
    return cleaned.replace(Regex("\\s+"), " ")
}


