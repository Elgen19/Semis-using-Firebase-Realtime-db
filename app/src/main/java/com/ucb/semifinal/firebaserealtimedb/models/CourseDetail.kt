package com.ucb.semifinal.firebaserealtimedb.models

data class CourseDetail(
    var id: String, // Unique ID of the course detail
    var courseName: String,
    var edpCode: Long,
    var grade: Double,
    var time: String
) {
    // No-argument constructor for Firebase deserialization
    constructor() : this("", "", 0L, 0.0,  "")
}
