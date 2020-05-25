package com.example.dusigrosz

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dusigrosz.entity.Borrower
import kotlinx.android.synthetic.main.activity_add_edit_borrower.*
import java.math.BigDecimal
import java.math.RoundingMode


class Simulate : AppCompatActivity() {

    private lateinit var nameSimulate: TextView
    private lateinit var surnameSimulate: TextView
    private lateinit var amountSimulate: TextView
    private lateinit var commissionNumberSimulate: EditText
    private lateinit var speedRepaymentNumberSimulation: EditText
    private lateinit var simulateButton: Button
    private var isClicked = true
    private var cashLeft = 0.0

    private lateinit var db: DatabaseHelper
    private lateinit var dialogAdd: AlertDialog
    private lateinit var borrower: Borrower
    private var commission: Double = 0.0
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Symuluj spłatę"
        setContentView(R.layout.activity_simulate)

        nameSimulate = findViewById(R.id.nameSimulate)
        surnameSimulate = findViewById(R.id.surnameSimulate)
        amountSimulate = findViewById(R.id.amountSimulate)
        commissionNumberSimulate = findViewById(R.id.commissionNumberSimulate)
        speedRepaymentNumberSimulation = findViewById(R.id.speedRepaymentNumberSimulation)
        simulateButton = findViewById(R.id.simulateButton)
        db = DatabaseHelper(this)

        if(intent.hasExtra("id")){
            borrower = Borrower(
                intent.getStringExtra("id").toString().toInt(),
                intent.getStringExtra("name"),
                intent.getStringExtra("surname"),
                intent.getStringExtra("amount").toDouble()
            )
            nameSimulate.text = borrower.name
            surnameSimulate.text = borrower.surname
            amountSimulate.text = borrower.amount.toString()
            cashLeft = borrower.amount
        }

        nav_view.setOnNavigationItemReselectedListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun startStop(view: View){
        if(speedRepaymentNumberSimulation.text.isNotBlank()){
            if(isClicked){
                isClicked = false
                timer(borrower.amount.toLong() * 1000, 1000).start()
                simulateButton.background = this.getDrawable(R.drawable.custom_button_pause)
            } else {
                isClicked = true
                simulateButton.background = this.getDrawable(R.drawable.custom_button_play)
            }
        } else {
            Toast.makeText(this, "Wpisz ile mu zabieramy", Toast.LENGTH_SHORT).show()
        }
    }

    private fun timer(money: Long, speed: Long): CountDownTimer {
        return object: CountDownTimer(money, speed) {

            override fun onTick(money: Long) {
                cashLeft = amountSimulate.text.toString().toDouble() - speedRepaymentNumberSimulation.text.toString().toDouble()
                if(cashLeft <= 0) {
                    cashLeft = 0.0
                    if (commissionNumberSimulate.text.isNotBlank()) {
                        commission += commission(commissionNumberSimulate.text.toString().toDouble(), amountSimulate.text.toString().toDouble())
                    }
                } else {
                    cashLeft = BigDecimal(cashLeft).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                    if (commissionNumberSimulate.text.isNotBlank()) {
                        commission += commission(commissionNumberSimulate.text.toString().toDouble(), amountSimulate.text.toString().toDouble())
                    }
                }
                amountSimulate.text = "$cashLeft"
                if (isClicked) {
                    borrower.amount = amountSimulate.text.toString().toDouble()
                    db.update(borrower)
                    cancel()
                } else if( cashLeft == 0.0){
                    borrower.amount = amountSimulate.text.toString().toDouble()
                    cancel()
                    onFinish()
                }
            }

            override fun onFinish() {
                delete(borrower)
            }
        }
    }

    private fun delete(borrower: Borrower){
        val builderAdd = AlertDialog.Builder(this)

        builderAdd.setTitle("Podsumujmy ( ᷇⁰ ͜U ᷇⁰ )")
        builderAdd.setMessage("No więc, zarobiliśmy na nim $commission zł")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    if (db.delete(borrower.id)){
                        Toast.makeText(this, "[̲̅$̲̅(̲̅ ͡°͜ʖ͡°̲̅)̲̅$̲̅]", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("Simulate", "Coś poszło nie tak :/")
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    db.update(borrower)
                    Toast.makeText(this, "Czekamy (☞ﾟヮﾟ)☞", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        builderAdd.setPositiveButton("Usuńmy go",dialogClickListener)
        builderAdd.setNegativeButton("Zostawmy go, niedługo wróci",dialogClickListener)
        dialogAdd = builderAdd.create()
        dialogAdd.show()
    }

    private fun commission(commission: Double, debit: Double): Double{
        return debit * (commission / 100)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("cashLeft", cashLeft);
        outState.putBoolean("cashRunning", isClicked);

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        cashLeft = savedInstanceState.getDouble("cashLeft")
        isClicked = savedInstanceState.getBoolean("cashRunning")
        amountSimulate.text = cashLeft.toString()

        if(!isClicked){
            isClicked = true
            startStop(this.simulateButton)
        }
    }
}
