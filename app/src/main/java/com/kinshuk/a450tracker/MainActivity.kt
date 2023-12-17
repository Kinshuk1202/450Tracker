package com.kinshuk.a450tracker

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kinshuk.a450tracker.databinding.ActivityMainBinding
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var topics: MutableSet<String>
    private lateinit var binding: ActivityMainBinding
    var listOfTopics = ArrayList<Topic>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.purple_500))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use binding.root here
        topics = mutableSetOf()
        readFromExcel()
        for(item in topics)
            listOfTopics.add(Topic(item))
        val adapter = TopicAdapter(this, listOfTopics)
        binding.topicsLV.adapter = adapter
    }
    class TopicAdapter(context: Context, listOfTopics: ArrayList<Topic>) : BaseAdapter(){
        private val listOfTopic:ArrayList<Topic> = listOfTopics
        private val context: Context = context
        override fun getCount(): Int {
            return listOfTopic.size
        }

        override fun getItem(p0: Int): Any {
            return listOfTopic[p0]        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val ds = listOfTopic[p0]
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val myView = inflater.inflate(R.layout.topic_ticket, null)
            val topicName = myView.findViewById<TextView>(R.id.DStv)
            topicName.text =ds.name
            val btn = myView.findViewById<Button>(R.id.Gobtn)
            btn
                .setOnClickListener {
                    val intent = Intent(context,questions::class.java)
                    intent.putExtra("DSname",ds.name.toString())
                    context.startActivity(intent)
                }
            return myView
        }

    }
    private fun readFromExcel() {
        try {
            val myInput: InputStream
            val assetManager = assets
            myInput = assetManager.open("FINAL450..xls")

            val myFileSystem = POIFSFileSystem(myInput)

            val myWorkBook = HSSFWorkbook(myFileSystem)

            val mySheet = myWorkBook.getSheetAt(0)

            val rowIter = mySheet.rowIterator()

            while (rowIter.hasNext()) {
                val myRow = rowIter.next() as HSSFRow
                val cell = myRow.getCell(0) // Get the cell from the first column

                if (cell != null) {
                    val cellValue = cell.toString()
//                    Toast.makeText(this, cellValue, Toast.LENGTH_LONG).show()
                    topics.add(cellValue)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
            Log.e(TAG, "Error reading from Excel", e)
        }
    }
}
