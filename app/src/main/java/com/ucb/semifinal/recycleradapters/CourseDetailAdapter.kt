package com.ucb.semifinal.recycleradapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucb.semifinal.PrestosaDisplayCourseDetailActivity
import com.ucb.semifinal.PrestosaUpdateCourseDetailActivity
//import com.ucb.semifinal.PrestosaDisplayCourseDetailActivity
//import com.ucb.semifinal.PrestosaUpdateCourseDetailActivity
import com.ucb.semifinal.R
import com.ucb.semifinal.firebaserealtimedb.models.CourseDetail
import kotlinx.coroutines.launch

class CourseDetailAdapter(
    private val context: Context,
    private val dbRef: DatabaseReference, // Updated to use Firebase Realtime Database
) : RecyclerView.Adapter<CourseDetailAdapter.CourseDetailViewHolder>() {

    private var courseDetails: MutableList<CourseDetail> = mutableListOf()

    inner class CourseDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener {
        private val edpCodeTextView: TextView = itemView.findViewById(R.id.edpCodeTextView)
        private val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)
        private val gradeTextView: TextView = itemView.findViewById(R.id.gradeTextView)

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun bind(courseDetail: CourseDetail) {
            edpCodeTextView.text = courseDetail.edpCode.toString()
            courseNameTextView.text = courseDetail.courseName
            gradeTextView.text = courseDetail.grade.toString()



            // Update background color based on grade
            if (courseDetail.grade > 3.0) {
                itemView.setBackgroundResource(R.color.colorFailed)
            } else {
                itemView.setBackgroundResource(R.color.colorPassed)
            }
        }

        override fun onClick(v: View?) {
//             Handle item click event here
            val courseDetail = courseDetails[adapterPosition]
            val intent = Intent(itemView.context, PrestosaDisplayCourseDetailActivity::class.java)

            // Pass the course detail data to the intent individually
            intent.putExtra("id", courseDetail.id)
            intent.putExtra("edpCode", courseDetail.edpCode)
            intent.putExtra("courseName", courseDetail.courseName)
            intent.putExtra("time", courseDetail.time)
            intent.putExtra("grade", courseDetail.grade)
            // Before putting into the intent
            Log.d("GradeTag", "Grade value before putting into intent: ${courseDetail.grade}")
            // Start the activity
            itemView.context.startActivity(intent)
        }

        override fun onLongClick(v: View?): Boolean {
            // Show options dialog
            showOptionsDialog(adapterPosition)
            return true
        }

        private fun showOptionsDialog(position: Int) {
            val options = arrayOf("Update", "Delete")

            val builder = AlertDialog.Builder(context) // Use context from the adapter
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
//                        // Start the update activity
                        val intent = Intent(context, PrestosaUpdateCourseDetailActivity::class.java)
                        intent.putExtra("edpCode", courseDetails[position].edpCode)
                        intent.putExtra("courseName", courseDetails[position].courseName)
                        intent.putExtra("time", courseDetails[position].time)
                        intent.putExtra("grade", courseDetails[position].grade)
                        intent.putExtra("courseId", courseDetails[position].id)
                        context.startActivity(intent)
                    }
                    1 -> {
                        // Delete action
                        val courseDetailToDelete = courseDetails[position]
                        Log.d("courseDetailToDelete", courseDetailToDelete.toString())
                        deleteCourseDetailFromFirebase(courseDetailToDelete)

                    }
                }
            }
            builder.create().show()
        }

        private fun deleteCourseDetailFromFirebase(courseDetail: CourseDetail) {
            val nodeKey = courseDetail.id
            Log.d("NodeKey", nodeKey)

            if (nodeKey.isNotEmpty()) {
                dbRef.child(nodeKey).removeValue()
                    .addOnSuccessListener {
                        dbRef.child(nodeKey).removeValue()
                        Toast.makeText(context, "Course Deleted Successfully!", Toast.LENGTH_SHORT).show()
                        Log.d("FirebaseDelete", "Item deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Deletion Unsuccessful!", Toast.LENGTH_SHORT).show()
                        Log.e("FirebaseDelete", "Error deleting item", e)
                    }
            } else {
                Toast.makeText(context, "Node key related problem!", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseDelete", "Node Key is empty, deletion failed")
            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_grade, parent, false)
        return CourseDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseDetailViewHolder, position: Int) {
        holder.bind(courseDetails[position])
    }

    override fun getItemCount(): Int {
        return courseDetails.size
    }

    init {
        // Fetch data from Firebase Realtime Database
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseDetails = mutableListOf<CourseDetail>()
                for (childSnapshot in snapshot.children) {
                    val courseDetail = childSnapshot.getValue(CourseDetail::class.java)
                    // Assign the Firebase key as the id
                    courseDetail?.id = childSnapshot.key?: ""
                    if (courseDetail!= null) {
                        courseDetails.add(courseDetail)
                    }
                }
                Log.e("at onDataChange", courseDetails.toString())
                submitList(courseDetails)
            }



            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                Log.e("FirebaseError", "Error fetching course details: ${error.message}")
            }
        })


    }

    fun submitList(newList: List<CourseDetail>) {
        // Clear the current list
        courseDetails.clear()

        // Add the new list to the adapter
        courseDetails.addAll(newList)
        Log.e("at submitlist", newList.toString())
        // Notify the adapter of the data change
        notifyDataSetChanged()
    }


}
