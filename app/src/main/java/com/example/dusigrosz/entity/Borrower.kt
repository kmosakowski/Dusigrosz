package com.example.dusigrosz.entity

class Borrower{

    lateinit var id: Number
    var name: String
    var surname: String
    var amount: Double

    constructor(id: Number, name: String, surname: String, amount: Double){
        this.id = id
        this.name = name
        this.surname = surname
        this.amount = amount
    }

    constructor(name: String, surname: String, amount: Double){
        this.name = name
        this.surname = surname
        this.amount = amount
    }
}