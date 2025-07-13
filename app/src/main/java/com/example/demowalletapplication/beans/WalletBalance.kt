package com.example.demowalletapplication.beans



data class WalletBalance(
    val ok: Boolean,
    val wallet: List<WalletBalanceInfo>,
    val warning: String
)

data class WalletBalanceInfo(
    val amount: String,
    val currency: String
)