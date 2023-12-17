package com.kinshuk.a450tracker

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.system.Os.link
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.kinshuk.a450tracker.databinding.ActivityMainBinding
import org.apache.poi.common.usermodel.Hyperlink
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.hssf.usermodel.HSSFHyperlink

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.InputStream


class questions : AppCompatActivity() {
    private lateinit var ques: MutableMap<String,String>
    private lateinit var binding: ActivityMainBinding
    private lateinit var topics:String
    var listOfPS = ArrayList<PS>()
    private lateinit var  adapter :PSAdapter
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use binding.root here
        val bundle: Bundle? = intent.extras
        topics = bundle!!.getString("DSname").toString()
        supportActionBar!!.title = topics
        ques = mutableMapOf()
        sharedPreferences = getSharedPreferences("checkbox_state", Context.MODE_PRIVATE)
        readFromExcel()
        for(it in ques.keys)
        {
            val isChecked = sharedPreferences.getBoolean(it, false)
            listOfPS.add(PS(it, isChecked,ques[it]!!))
        }
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.purple_500))
        adapter = PSAdapter(this,listOfPS,sharedPreferences)
        binding.topicsLV.adapter = adapter

}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sites_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){

            R.id.gfg->{
                gotoLink("https://www.geeksforgeeks.org/")

            }
            R.id.lc->{
                gotoLink("https://leetcode.com/")
            }
            R.id.reset->{
                for(ps in listOfPS) {
                    ps.iChecked = false
                    adapter.checkedStateMap[ps.name!!] = false
                }
//                Toast.makeText(this,"reset called",Toast.LENGTH_LONG).show()
                adapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    class PSAdapter(context: Context, listOfPS: ArrayList<PS>,sharedPreferences: SharedPreferences) : BaseAdapter(){
        private val listOfPS:ArrayList<PS> = listOfPS
        private val context: Context = context
        private val sharedPreferences:SharedPreferences=sharedPreferences
        val checkedStateMap = HashMap<String, Boolean>()

        init {
            // Initialize the checked state map
            for (ps in listOfPS) {
                checkedStateMap[ps.name!!] = ps.iChecked ?: false
            }
        }
        override fun getCount(): Int {
            return listOfPS.size
        }

        override fun getItem(p0: Int): Any {
            return listOfPS[p0]        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val ps = listOfPS[p0]
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val myView = inflater.inflate(R.layout.questiion_ticket, null)
            val psdec = myView.findViewById<TextView>(R.id.DStv)
            val tkt = myView.findViewById<LinearLayout>(R.id.ticketid)
            val linkbtn = myView.findViewById<Button>(R.id.linkgoto)
            val hylink = ps.link

            linkbtn.setOnClickListener {
                if(hylink == "NA")
                    Toast.makeText(context,"Link not present for this question!!",Toast.LENGTH_LONG).show()
                else{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(hylink))
                    context.startActivity(intent)
                }

            }
            psdec.text = ps.name
            val cb = myView.findViewById<CheckBox>(R.id.Gobtn)
            cb.isChecked = ps.iChecked!!
            cb.isChecked = checkedStateMap[ps.name] ?: false
            cb.setOnCheckedChangeListener { _, isChecked ->
                checkedStateMap[ps.name!!] = isChecked
                sharedPreferences.edit().putBoolean(ps.name, isChecked).apply()
                if(cb.isChecked) {
                    tkt.setBackgroundResource(R.color.gray)
                    linkbtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
                }
                else {
                    tkt.setBackgroundResource(R.color.white)
                    linkbtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))


                }
            }
            if(cb.isChecked) {
                tkt.setBackgroundResource(R.color.gray)
                linkbtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
            }
            else {
                tkt.setBackgroundResource(R.color.white)
                linkbtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))

            }
            return myView
        }
    }

    private fun gotoLink(link:String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
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
                val dsName = myRow.getCell(0)
                if(dsName.toString() == topics) {
                    val cell = myRow.getCell(1) // Get the cell from the first column

                    if (cell != null) {
                        val cellValue = cell.toString()
                        //                    Toast.makeText(this, cellValue, Toast.LENGTH_LONG).show()

                        val hyperlink = cell.hyperlink
                        if (hyperlink != null) {
                            val linkAddress = hyperlink.address.toString()
//                            Log.d(ContentValues.TAG,"Text: ${cell.stringCellValue}, Hyperlink: $linkAddress")
                            ques.put(cellValue,linkAddress)
                        }
                        else
                            ques.put(cellValue,"NA")

                    }
                }
            }
        } catch (e: Exception) {
            // Handle exceptions appropriately
            Log.e(ContentValues.TAG, "Error reading from Excel", e)
        }
    }
}

