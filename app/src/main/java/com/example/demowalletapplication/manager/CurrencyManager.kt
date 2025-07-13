package com.example.demowalletapplication.manager

import com.example.demowalletapplication.beans.CurrencyInfoBean

object CurrencyInfoManager {

    private var name: String = "USD"
    private var symbol: String = "$"
    private var rate: String = "1.0"

    fun getCurrencyInformation(): CurrencyInfoBean {
        return CurrencyInfoBean(name, symbol, rate)
    }

    fun updateCurrencyInformation(name: String, symbol: String, rate: String) {
        this.name = name
        this.symbol = symbol
        this.rate = rate
    }

}