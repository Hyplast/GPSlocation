package fi.infinitygrow.gpslocation.data.mapper


fun cleanString(input: String): String {
    // Replace all Unicode 10 (Line Feed) with Unicode 32 (Space)
    val cleaned = input.replace("\u000A", " ")

    // Replace multiple consecutive spaces with a single space
    return cleaned.replace(Regex("\\s+"), " ")
}


