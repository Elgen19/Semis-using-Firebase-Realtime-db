package com.ucb.semifinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ucb.semifinal.databinding.ActivityPrestosaDisplayCourseDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrestosaDisplayCourseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrestosaDisplayCourseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrestosaDisplayCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Retrieve intent extras
        val couseID = intent.getStringExtra("id")
        val edpCode = intent.getLongExtra("edpCode", 0L)
        val courseName = intent.getStringExtra("courseName")
        val time = intent.getStringExtra("time")
        val grade = intent.getDoubleExtra("grade", 0.0)

        // After retrieving in PrestosaDisplayCourseDetailActivity
        Log.d("GradeTag", "Grade value after retrieving from intent: $grade")
        // Display the retrieved data
        binding.edpCodeTextView.text = edpCode.toString()
        binding.courseNameTextView.text = courseName
        binding.timeTextView.text = time
        binding.gradeTextView.text = grade.toString()

        binding.updateButton.setOnClickListener {
            // Create an intent to start the PrestosaUpdateCourseDetailActivity
            val intent = Intent(this@PrestosaDisplayCourseDetailActivity, PrestosaUpdateCourseDetailActivity::class.java)

            // Pass the retrieved data to the update activity using intent extras
            intent.putExtra("courseId", couseID)
            intent.putExtra("edpCode", edpCode)
            intent.putExtra("courseName", courseName)
            intent.putExtra("time", time)
            intent.putExtra("grade", grade)

            // Start the update activity
            startActivity(intent)
            finish()
        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, PrestosaMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
