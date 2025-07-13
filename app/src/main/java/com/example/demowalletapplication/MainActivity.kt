package com.example.demowalletapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demowalletapplication.adapters.WalletCoinAdapter
import com.example.demowalletapplication.beans.WalletItemShowBean
import com.example.demowalletapplication.databinding.ActivityMainBinding
import com.example.demowalletapplication.viewModel.WalletBalanceViewModule
import com.scwang.smart.refresh.header.ClassicsHeader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var databinding: ActivityMainBinding
    private val viewModule: WalletBalanceViewModule by viewModels()
    private var walletAdapter: WalletCoinAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        viewModule.getDataList()
        initDataView()

        lifecycleScope.launch {
            viewModule.totalAssetFlow.collectLatest {
                databinding.tvBalance.text = it
            }
        }
        lifecycleScope.launch {
            viewModule.walletCoinListFlow.collectLatest { dataList ->
                if (walletAdapter == null) {
                    walletAdapter = WalletCoinAdapter(dataList)
                    databinding.rvContent.adapter = walletAdapter
                } else {
                    walletAdapter?.updateList(dataList)
                }
                databinding.refreshLayout.finishRefresh()
            }
        }

    }

    private fun initDataView() {
        with(databinding) {
            refreshLayout.setRefreshHeader(ClassicsHeader(clContent.context))
            refreshLayout.setOnRefreshListener {
                viewModule.getDataList()
            }
            rvContent.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }
}