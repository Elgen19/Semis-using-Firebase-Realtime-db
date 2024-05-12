package com.ucb.semifinal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.ucb.semifinal.databinding.ActivityPrestosaUpdateCourseDetailBinding
import com.ucb.semifinal.firebaserealtimedb.models.CourseDetail


class PrestosaUpdateCourseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrestosaUpdateCourseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrestosaUpdateCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve extras from the intent
        val edpCode = intent.getLongExtra("edpCode", 0L).toString()
        val courseName = intent.getStringExtra("courseName")
        val time = intent.getStringExtra("time")
        val grade = intent.getDoubleExtra("grade", 0.0)
        val courseId = intent.getStringExtra("courseId")

        // Prepopulate EditText fields with the retrieved values
        // Debugging: Log the edpCode to ensure it's not null or empty
        Log.d("PrestosaUpdateCourseDetailActivity", "EDP Code from Intent: $edpCode")
        binding.edpCodeEditText.setText(edpCode)
        binding.courseNameEditText.setText(courseName)
        binding.timeEditText.setText(time)
        binding.gradeEditText.setText(grade.toString())

        if (grade in 1.0..3.0) {
            binding.screen.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPassed))
        } else {
            binding.screen.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFailed))
        }


        // Add text changed listener to the grade EditText
        binding.gradeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // Parse the grade value from the EditText
                val gradeValue = s.toString().toDoubleOrNull()
                if (gradeValue!= null) {
                    // Check if the grade is within the pass or fail range
                    if (gradeValue in 1.0..3.0) {
                        binding.screen.setBackgroundColor(ContextCompat.getColor(this@PrestosaUpdateCourseDetailActivity, R.color.colorPassed))
                    } else {
                        binding.screen.setBackgroundColor(ContextCompat.getColor(this@PrestosaUpdateCourseDetailActivity, R.color.colorFailed))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        // Set click listener for the save button
        binding.saveButton.setOnClickListener {
            if (courseId != null) {
                saveUpdatedCourseDetail(courseId)
            }
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, PrestosaMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun saveUpdatedCourseDetail(courseId: String) {
        // Parse the input values from the EditTexts
        val updatedEdpCode = binding.edpCodeEditText.text.toString().toLong()
        val updatedCourseName = binding.courseNameEditText.text.toString()
        val updatedTime = binding.timeEditText.text.toString()
        val updatedGrade = binding.gradeEditText.text.toString().toDoubleOrNull()

        // Check if all values are valid
        if (updatedEdpCode!= null && updatedCourseName.isNotEmpty() && updatedTime.isNotEmpty() && updatedGrade!= null) {
            // Create a CourseDetail object with the updated values
            val updatedCourseDetail = CourseDetail(
                id = courseId, // Assuming courseId is the unique identifier
                courseName = updatedCourseName,
                edpCode = updatedEdpCode,
                grade = updatedGrade,
                time = updatedTime
            )

            // Update the course detail in Firebase Realtime Database
            val dbRef = FirebaseDatabase.getInstance().getReference("courseDetails/$courseId")
            dbRef.setValue(updatedCourseDetail)
                .addOnSuccessListener {
                    Toast.makeText(this, "Course detail updated successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PrestosaUpdateCourseDetailActivity, PrestosaMainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Log.d("FirebaseUpdate", "Course detail updated successfully!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update course detail: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseUpdate", "Failed to update course detail", e)
                }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }


}

