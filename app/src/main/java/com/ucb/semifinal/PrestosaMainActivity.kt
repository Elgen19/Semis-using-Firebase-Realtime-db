package com.ucb.semifinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucb.semifinal.databinding.PrestosaActivityMainBinding
import com.ucb.semifinal.firebaserealtimedb.models.CourseDetail
import com.ucb.semifinal.recycleradapters.CourseDetailAdapter

class PrestosaMainActivity : AppCompatActivity() {

    private lateinit var binding: PrestosaActivityMainBinding
    private lateinit var courseDetailAdapter: CourseDetailAdapter
    private lateinit var db: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PrestosaActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Realtime Database
        db = FirebaseDatabase.getInstance()

        // Obtain a DatabaseReference to a specific node in your database
        myRef = db.getReference().child("courseDetails")

        // Set up RecyclerView
        setUpRecyclerView(myRef)

        // Set OnClickListener for FloatingActionButton
        binding.addGradeButton.setOnClickListener {
            val intent = Intent(this, PrestosaAddCourseDetailActivity::class.java)
            startActivity(intent)
        }

        // Fetch and display data from the database
        fetchDataAndUpdateRecyclerView(myRef)


    }

    private fun setUpRecyclerView(dbRef: DatabaseReference) {
        // Create and set layout manager
        binding.gradesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter
        courseDetailAdapter = CourseDetailAdapter(this, dbRef)

        // Set adapter to RecyclerView
        binding.gradesRecyclerView.adapter = courseDetailAdapter
    }

    private fun fetchDataAndUpdateRecyclerView(dbRef: DatabaseReference) {
        // Fetch data from Firebase Realtime Database
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseDetails = mutableListOf<CourseDetail>()
                for (childSnapshot in snapshot.children) {
                    val courseDetail = childSnapshot.getValue(CourseDetail::class.java)
                    // Assign the Firebase key as the id
                    courseDetail?.id = childSnapshot.key?: ""
                    courseDetails.add(courseDetail!!)
                }
                // Update RecyclerView with fetched data
                courseDetailAdapter.submitList(courseDetails)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
                Toast.makeText(this@PrestosaMainActivity, error.message, Toast.LENGTH_LONG).show()
                Log.e("FirebaseError", "Error fetching course details: ${error.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setUpRecyclerView(myRef) // Use myRef here
        fetchDataAndUpdateRecyclerView(myRef) // Use myRef here
    }
}
