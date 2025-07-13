package com.example.demowalletapplication.beans

data class SupportCurrenciesListBean(
    val currencies: List<SupportedCurrencyBean>,
    val ok: Boolean,
    val total: Int
)

data class SupportedCurrencyBean(
    val blockchain_symbol: String,
    val code: String,
    val coin_id: String,
    val colorful_image_url: String,
    val contract_address: String,
    val deposit_address_tag_name: String,
    val deposit_address_tag_type: String,
    val display_decimal: Int,
    val explorer: String,
    val gas_limit: Int,
    val gray_image_url: String,
    val has_deposit_address_tag: Boolean,
    val is_erc20: Boolean,
    val min_balance: Int,
    val name: String,
    val num_confirmation_required: Int,
    val supports_legacy_address: Boolean,
    val symbol: String,
    val token_decimal: Int,
    val token_decimal_value: String,
    val trading_symbol: String,
    val withdrawal_eta: List<String>
)