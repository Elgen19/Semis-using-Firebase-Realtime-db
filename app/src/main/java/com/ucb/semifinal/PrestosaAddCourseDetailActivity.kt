package com.ucb.semifinal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ucb.semifinal.databinding.ActivityPrestosaAddCourseDetailBinding
import com.google.firebase.database.FirebaseDatabase

class PrestosaAddCourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrestosaAddCourseDetailBinding
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrestosaAddCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Realtime Database
        db = FirebaseDatabase.getInstance()

        // Set click listener for save button
        binding.saveButton.setOnClickListener {
            saveCourseDetail()
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, PrestosaMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun saveCourseDetail() {
        val edpCode = binding.edpCodeEditText.text.toString().toIntOrNull()
        val courseName = binding.courseNameEditText.text.toString()
        val time = binding.timeEditText.text.toString()
        val grade = binding.gradeEditText.text.toString().toDoubleOrNull()

        if (edpCode == null || grade == null) {
            Toast.makeText(this, "Invalid EDP Code or Grade", Toast.LENGTH_SHORT).show()
            return
        }

        val courseDetail = hashMapOf(
            "edpCode" to edpCode,
            "courseName" to courseName,
            "time" to time,
            "grade" to grade
        )

        // Save course detail to Firebase Realtime Database
        val myRef = db.getReference("courseDetails")
        myRef.push().setValue(courseDetail)
            .addOnSuccessListener {
                Toast.makeText(this, "Course detail saved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving course detail: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
