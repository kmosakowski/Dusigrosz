package com.example.dusigrosz

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dusigrosz.entity.Borrower
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_add_edit_borrower.*
import java.math.BigDecimal
import java.math.RoundingMode

class AddEditBorrower : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var amount: EditText
    private lateinit var addButton: Button
    private lateinit var updateButton: Button
    private lateinit var simulateBtn: FloatingActionButton
    private lateinit var smsBtn: FloatingActionButton
    private lateinit var db : DatabaseHelper
    private lateinit var dialogAdd: AlertDialog
    private lateinit var dialogUpdate: AlertDialog
    private var oldAmount: Double = 0.0

    private lateinit var borrower: Borrower


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Dodaj dłużnika"
        setContentView(R.layout.activity_add_edit_borrower)

        name = findViewById(R.id.name)
        surname = findViewById(R.id.surname)
        amount = findViewById(R.id.amount)
        addButton = findViewById(R.id.addButton)
        updateButton = findViewById(R.id.updateButton)
        simulateBtn = findViewById(R.id.simulateBtn)
        smsBtn = findViewById(R.id.smsBtn)

        db = DatabaseHelper(this)

        if(intent.hasExtra("id")){
            borrower = Borrower(
                intent.getStringExtra("id").toString().toInt(),
                intent.getStringExtra("name"),
                intent.getStringExtra("surname"),
                intent.getStringExtra("amount").toDouble()
            )
            name.setText(borrower.name)
            surname.setText(borrower.surname)
            amount.setText(borrower.amount.toString())
            oldAmount = borrower.amount
            addButton.visibility = View.GONE
        } else {
            borrower = Borrower(-1, "", "", 0.0)
            updateButton.visibility = View.GONE
            simulateBtn.visibility = View.GONE
            smsBtn.visibility = View.GONE
        }


        nav_view.setOnNavigationItemReselectedListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun add(view:View){
        val builderAdd = AlertDialog.Builder(this)

        builderAdd.setTitle("Nowy dłużnik?")
        builderAdd.setMessage("Czy dodać go do listy dłużników?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    val newBorrower = Borrower(name.text.toString(), surname.text.toString(), BigDecimal(amount.text.toString().toDouble()).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                    db.insert(newBorrower)
                    Toast.makeText(this, "Zadłużony (ง •̀_•́)ง", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    name.setText("")
                    surname.setText("")
                    amount.setText("")
                    Toast.makeText(this, "Anulowano", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builderAdd.setPositiveButton("Tak",dialogClickListener)
        builderAdd.setNegativeButton("Nie",dialogClickListener)
        dialogAdd = builderAdd.create()
        dialogAdd.show()
    }

    fun update(view:View){
        val builder = AlertDialog.Builder(this)

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    borrower.name = name.text.toString()
                    borrower.surname = surname.text.toString()
                    borrower.amount = BigDecimal(amount.text.toString().toDouble()).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                    db.update(borrower)
                    Toast.makeText(this, "Zaktualizowano (ง •̀_•́)ง", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    Toast.makeText(this, "Anulowano zmiany (˘⌣˘)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (amount.text.toString().toDouble() > oldAmount){
            builder.setTitle("Podwyżka zadłużenia")
            builder.setMessage("Czy chcesz podwyższyć mu dług?")

            builder.setPositiveButton("Tak",dialogClickListener)
            builder.setNegativeButton("Nie",dialogClickListener)
        } else {
            builder.setTitle("Zniżka zadłużenia")
            builder.setMessage("Czy chcesz zniżyć mu zadłużenie?")

            builder.setPositiveButton("Tak",dialogClickListener)
            builder.setNegativeButton("Nie",dialogClickListener)
        }

        dialogUpdate = builder.create()
        dialogUpdate.show()
    }

    fun goToSimulate(view: View){
        val intent = Intent(this, Simulate::class.java)
        intent.putExtra("id", borrower.id.toString())
        intent.putExtra("name", borrower.name)
        intent.putExtra("surname", borrower.surname)
        intent.putExtra("amount", borrower.amount.toString())
        startActivity(intent)
    }

    fun sendSMS(view: View){
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "Ile jeszcze mam czekać na kasę? Wiem gdzie mieszkasz (☞■_■)☞")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, "Pośpieszenie do zapłaty"))
    }

    fun undoChanged(view: View){
        name.setText(borrower.name)
        surname.setText(borrower.surname)
        amount.setText(borrower.amount.toString())
    }
}