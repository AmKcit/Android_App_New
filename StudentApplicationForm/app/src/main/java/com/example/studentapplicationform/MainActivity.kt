package com.example.studentapplicationform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fullName: EditText = findViewById(R.id.fullName)
        val email: EditText = findViewById(R.id.email)
        val phone: EditText = findViewById(R.id.phone)

        val sub1: EditText = findViewById(R.id.sub1)
        val sub2: EditText = findViewById(R.id.sub2)
        val sub3: EditText = findViewById(R.id.sub3)

        val mark1: EditText = findViewById(R.id.mark1)
        val mark2: EditText = findViewById(R.id.mark2)
        val mark3: EditText = findViewById(R.id.mark3)

        val address: EditText = findViewById(R.id.address)
        val courseSpinner: Spinner = findViewById(R.id.courseSpinner)
        val submitBtn: Button = findViewById(R.id.submitBtn)

        // Spinner list
        val items = arrayOf("Computer Science", "Information Technology", "Mechanical Engineering", "Civil Engineering")
        // Use simple_spinner_item for the selected view and simple_spinner_dropdown_item for the dropdown list
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter

        submitBtn.setOnClickListener {
            val name = fullName.text.toString()
            val mail = email.text.toString()
            val ph = phone.text.toString()

            if (name.isEmpty() || mail.isEmpty() || ph.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Application Submitted!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
