package com.example.demowalletapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demowalletapplication.beans.ExchangeRateBean
import com.example.demowalletapplication.beans.Rate
import com.example.demowalletapplication.beans.SupportCurrenciesListBean
import com.example.demowalletapplication.beans.SupportedCurrencyBean
import com.example.demowalletapplication.beans.Tier
import com.example.demowalletapplication.beans.WalletBalance
import com.example.demowalletapplication.beans.WalletItemShowBean
import com.example.demowalletapplication.manager.CurrencyInfoManager
import com.example.demowalletapplication.utils.EXCHANGE_RATES_JSON_STRING
import com.example.demowalletapplication.utils.SUPPORTED_CURRENCIES_LIST_JSON_STRING
import com.example.demowalletapplication.utils.WALLET_BALANCE_JSON_STRING
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

class WalletBalanceViewModule : ViewModel() {

    private val gson = Gson()
    private val supportedCurrencyMap = HashMap<String, SupportedCurrencyBean>(12)
    private val exchangeRateMap = HashMap<String, List<Rate>>(128)
    private var tierList: List<Tier> = emptyList()
    private val _totalAssetFlow = MutableSharedFlow<String>()
    val totalAssetFlow = _totalAssetFlow.asSharedFlow()
    private val _walletCoinListFlow = MutableSharedFlow<List<WalletItemShowBean>>()
    val walletCoinListFlow = _walletCoinListFlow.asSharedFlow()

    fun getDataList() {
        viewModelScope.launch {
            val currencyList = viewModelScope.async {
                getSupportCurrenciesList()
            }
            val exchangeRateList = viewModelScope.async {
                getExchangeRateList()
            }
            currencyList.await()
            exchangeRateList.await()

            getWalletBalanceData()
        }

    }

    private suspend fun getWalletBalanceData() {
        withContext(Dispatchers.IO) {
            val supportCurrencyList = gson.fromJson(
                WALLET_BALANCE_JSON_STRING,
                WalletBalance::class.java
            )
            if (supportCurrencyList.ok) {
                val walletCoinList = ArrayList<WalletItemShowBean>()
                var totalBigDecimal = BigDecimal("0")
                val currencyInfo = CurrencyInfoManager.getCurrencyInformation()
                supportCurrencyList.wallet.forEach {
                    supportedCurrencyMap[it.currency]?.apply {
                        val rate = exchangeRateMap[it.currency]?.firstOrNull()
                        val balance =
                            BigDecimal(it.amount) *
                                    BigDecimal(rate?.rate ?: "0") *
                                    BigDecimal(currencyInfo.rate)
                        totalBigDecimal += balance
                        val realShowBalance = balance.setScale(2, RoundingMode.HALF_DOWN)
                        walletCoinList.add(
                            WalletItemShowBean(
                                coinId = this.coin_id,
                                coinUrl = this.colorful_image_url,
                                coinName = this.blockchain_symbol,
                                coinSymbol = this.symbol,
                                coinAmount = it.amount,
                                coinBalance = "${currencyInfo.symbol} $realShowBalance",
                            )
                        )
                    }
                }
                val realShowTotalAsset = totalBigDecimal.setScale(2, RoundingMode.HALF_DOWN)
                _totalAssetFlow.emit("${currencyInfo.symbol} $realShowTotalAsset ${currencyInfo.name}")
                _walletCoinListFlow.emit(walletCoinList)
            }
        }

    }

    private suspend fun getSupportCurrenciesList() {
        withContext(Dispatchers.IO) {
            val supportCurrencyList = gson.fromJson(
                SUPPORTED_CURRENCIES_LIST_JSON_STRING,
                SupportCurrenciesListBean::class.java
            )
            if (supportCurrencyList.ok) {
                supportCurrencyList.currencies.forEach {
                    supportedCurrencyMap[it.coin_id] = it
                }
            }
        }
    }

    private suspend fun getExchangeRateList() {
        withContext(Dispatchers.IO) {
            val supportCurrencyList = gson.fromJson(
                EXCHANGE_RATES_JSON_STRING,
                ExchangeRateBean::class.java
            )
            if (supportCurrencyList.ok) {
                tierList = supportCurrencyList.tiers
                supportCurrencyList.tiers.forEach {
                    exchangeRateMap[it.from_currency] = it.rates
                }
            }
        }
    }


}