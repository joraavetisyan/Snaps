package io.snaps.basewallet.data.blockchain

import io.horizontalsystems.ethereumkit.contracts.ContractMethod
import io.horizontalsystems.ethereumkit.models.Address
import java.math.BigInteger

class RepairContractMethod(
    val owner: Address,
    val contract: Address,
    val profitWallet: Address,
    val fromAccountAmounts: BigInteger,
    val nonce: BigInteger,
    val deadline: BigInteger,
    val signature: ByteArray,
) : ContractMethod() {

    override val methodSignature =
        "execTransaction(uint256[],uint256,address,address,address[],uint256[],address,address[],uint256[],uint256,uint256,bytes)"

    // 12 params
    override fun getArguments() = listOf(
        listOf<BigInteger>(),
        BigInteger.ZERO,
        owner,
        contract,
        listOf(profitWallet),
        listOf(fromAccountAmounts),
        Address("0x0000000000000000000000000000000000000000"),
        listOf<Address>(),
        listOf<BigInteger>(),
        nonce,
        deadline,
        signature,
    )
}

class MintContractMethod(
    val owner: Address,
    val profitWallet: Address,
    val fromAccountAmounts: BigInteger,
    val nonce: BigInteger,
    val deadline: BigInteger,
    val signature: ByteArray,
    val tokensCount: BigInteger? = null,
) : ContractMethod() {

    override val methodSignature =
        "execTransaction(uint256[],uint256,address,address,address[],uint256[],address,address[],uint256[],uint256,uint256,bytes)"

    // 12 params
    override fun getArguments() = listOf(
        listOf<BigInteger>(),
        tokensCount ?: BigInteger.ONE,
        owner,
        Address("0x0000000000000000000000000000000000000000"),
        listOf(profitWallet),
        listOf(fromAccountAmounts),
        Address("0x0000000000000000000000000000000000000000"),
        listOf<Address>(),
        listOf<BigInteger>(),
        nonce,
        deadline,
        signature,
    )
}