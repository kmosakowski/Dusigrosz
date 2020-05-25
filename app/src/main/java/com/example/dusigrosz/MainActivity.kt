package com.example.dusigrosz

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.dusigrosz.adapter.BorrowerListAdapter
import com.example.dusigrosz.entity.Borrower

class MainActivity : AppCompatActivity() {

    private lateinit var borrowers: ListView
    private lateinit var db : DatabaseHelper
    private lateinit var allBorrowers: ArrayList<Borrower>
    private lateinit var adapt: BaseAdapter
    private lateinit var dialogAdd: AlertDialog
    private lateinit var sum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Lista Dłużników"
        setContentView(R.layout.activity_main)

        sum = findViewById(R.id.sumAll)
        db = DatabaseHelper(this)
        borrowers = findViewById(R.id.borrower_list)
        registerForContextMenu(borrowers)
        allBorrowers = db.getBorrowers()
        adapt = BorrowerListAdapter(this, allBorrowers)
        borrowers.adapter = adapt
        adapt.notifyDataSetChanged()
        var sumOfAmount = 0.0
        allBorrowers.forEach{
            sumOfAmount += it.amount
        }
        sum.text = sumOfAmount.toString()

        findViewById<ListView>(borrowers.id).setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(this, AddEditBorrower::class.java)
            Log.e("MainActivity", allBorrowers[i].id.toString())
            intent.putExtra("id", allBorrowers[i].id.toString())
            intent.putExtra("name", allBorrowers[i].name)
            intent.putExtra("surname", allBorrowers[i].surname)
            intent.putExtra("amount", allBorrowers[i].amount.toString())
            startActivity(intent)
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.pop_up_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        when (item.itemId) {
            R.id.deleteItem -> {
                delete(allBorrowers[info.position])
            }
            R.id.simulateItem -> {
                val intent = Intent(this, Simulate::class.java)
                Log.e("MainActivity", allBorrowers[info.position].id.toString())
                intent.putExtra("id", allBorrowers[info.position].id.toString())
                intent.putExtra("name", allBorrowers[info.position].name)
                intent.putExtra("surname", allBorrowers[info.position].surname)
                intent.putExtra("amount", allBorrowers[info.position].amount.toString())
                startActivity(intent)
            }
        }

        return super.onContextItemSelected(item)
    }

    fun goToAdd(view: View){
        val intent = Intent(this, AddEditBorrower::class.java)
        startActivity(intent)
    }

    private fun delete(borrower: Borrower){
        val builderAdd = AlertDialog.Builder(this)

        builderAdd.setTitle("Jesteś pewien?")
        builderAdd.setMessage("Czy na pewno chcesz usunąć?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    if (db.delete(borrower.id)){
                        allBorrowers.remove(borrower)
                        adapt.notifyDataSetChanged()
                        Toast.makeText(this, "Usunięto (ง •̀_•́)ง", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("MainActivity", "Coś poszło nie tak :/")
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    Toast.makeText(this, "Anulowano (˘⌣˘)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builderAdd.setPositiveButton("Tak",dialogClickListener)
        builderAdd.setNegativeButton("Nie",dialogClickListener)
        dialogAdd = builderAdd.create()
        dialogAdd.show()
    }
}
