package io.snaps.basebilling.model

enum class Sku(val code: String, val isConsumable: Boolean, val type: ProductType) {

    NEWBIE("newbie", true, ProductType.IN_APP),
    VIEWER("viewer", true, ProductType.IN_APP),
    FOLLOWER("follower", true, ProductType.IN_APP),
    SUB("sub", true, ProductType.IN_APP),
    SPONSOR("sponsor", true, ProductType.IN_APP);

    companion object {
        fun byCode(code: String) = values().firstOrNull { it.code == code }

        fun byType(type: ProductType) = values().filter { it.type == type }.map { it.code }
    }
}