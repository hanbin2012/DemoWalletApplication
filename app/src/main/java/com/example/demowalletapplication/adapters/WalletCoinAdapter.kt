package com.example.demowalletapplication.adapters

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.demowalletapplication.R
import com.example.demowalletapplication.beans.WalletItemShowBean
import com.example.demowalletapplication.databinding.LayoutWalletCoinItemBinding


class WalletCoinAdapter(private var dataList: List<WalletItemShowBean>) :
    Adapter<WalletCoinAdapter.WalletCoinViewHolder>() {

    fun updateList(newList: List<WalletItemShowBean>) {
        val diffCallback = WalletCoinDiffCallback(dataList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        dataList = newList.toList() // 更新数据
        diffResult.dispatchUpdatesTo(this) // 应用差异更新
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletCoinViewHolder {
        val layoutWalletCoinItemBinding = DataBindingUtil.inflate<LayoutWalletCoinItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_wallet_coin_item, parent, false
        )
        return WalletCoinViewHolder(layoutWalletCoinItemBinding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: WalletCoinViewHolder, position: Int) {
        if (position < dataList.size) {
            val data = dataList[position]
            holder.bindView(data)
        }
    }

    override fun onBindViewHolder(
        holder: WalletCoinViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            // 解析 payloads 并局部更新视图
            val bundle = payloads[position] as? Bundle
            if (bundle == null) {
                onBindViewHolder(holder, position)
            } else {
                val coinAmount = bundle.getString("coinAmount") ?: ""
                val coinBalance = bundle.getString("coinBalance") ?: ""
                val dataBean = dataList[position]
                val tvCoinAmountString = "$coinAmount ${dataBean.coinSymbol}"
                holder.updateBalance(tvCoinAmountString, coinBalance)
            }
        }
    }

    class WalletCoinViewHolder(private val itemViewBinding: LayoutWalletCoinItemBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bindView(data: WalletItemShowBean) {
            Glide.with(itemViewBinding.ivCoin)
                .load(data.coinUrl)
                .placeholder(R.mipmap.logo)
                .transform(CircleCrop())
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("GLIDE_ERROR", "加载失败", e);
                        return false;
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                       return true
                    }
                })
               .into(itemViewBinding.ivCoin)
            itemViewBinding.tvCoinName.text = data.coinName
            val tvCoinAmountString = "${data.coinAmount} ${data.coinSymbol}"
            itemViewBinding.tvCoinAmount.text = tvCoinAmountString
            itemViewBinding.tvCoinBalance.text = data.coinBalance
        }

        fun updateBalance(tvCoinAmountString: String, coinBalanceString: String) {
            itemViewBinding.tvCoinAmount.text = tvCoinAmountString
            itemViewBinding.tvCoinBalance.text = coinBalanceString
        }
    }
}

class WalletCoinDiffCallback(
    private val dataList: List<WalletItemShowBean>,
    private val newList: List<WalletItemShowBean>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return dataList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItemPosition >= dataList.size || newItemPosition >= newList.size) {
            return false
        }
        val oldItem = dataList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItemPosition >= dataList.size || newItemPosition >= newList.size) {
            return false
        }
        val oldItem = dataList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.coinId == newItem.coinId &&
                oldItem.coinUrl == newItem.coinUrl &&
                oldItem.coinName == newItem.coinName &&
                oldItem.coinSymbol == newItem.coinSymbol &&
                oldItem.coinAmount == newItem.coinAmount &&
                oldItem.coinBalance == newItem.coinBalance
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = dataList[oldItemPosition]
        val newItem = newList[newItemPosition]
        val diff = Bundle()
        if (oldItem.coinAmount != newItem.coinAmount) diff.putString(
            "coinAmount",
            newItem.coinAmount
        )
        if (oldItem.coinBalance != newItem.coinBalance) diff.putString(
            "coinBalance",
            newItem.coinBalance
        )
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}
