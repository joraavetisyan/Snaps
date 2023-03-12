package io.snaps.basebilling.model

enum class Sku(val code: String, val isConsumable: Boolean, val type: ProductType) {

    TEST_PRODUCT("iap_newbie_test", true, ProductType.IN_APP);

    companion object {
        fun byCode(code: String) = values().firstOrNull { it.code == code }

        fun byType(type: ProductType) = values().filter { it.type == type }.map { it.code }
    }
}