package jfyp.addressgenerator.addressgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.github.novacrypto.bip32.PrivateKey
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.SeedCalculator
import io.github.novacrypto.bip32.networks.Bitcoin
import io.github.novacrypto.bip32.Index.hard
import io.github.novacrypto.bip44.Account
import io.github.novacrypto.bip44.AddressIndex
import io.github.novacrypto.bip44.BIP44.m


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // generate Mnemonic w/ x number of words
        val generator: String = WordsGenerator.GetInstance().generateNewMnemonic(Words.TWELVE)

        // generate seed w/ x-bits
        val seed = SeedCalculator().calculateSeed(generator, "")

        // obtain root key from seed
        val root = PrivateKey.fromSeed(seed, Bitcoin.TEST_NET)

        // generate addresses using root key
        generateBIP44Addresses(root)

        // generate addresses another way w/ BIP44 Library
        generateSecondBIP44Addresses(root)

        // print first 20 public keys & addresses
        printAddresses(root)
    }

    private fun generateBIP44Addresses(root: PrivateKey) {
        val addressMethod1 = root
                .cKDpriv(hard(44)) //fixed
                .cKDpriv(hard(1)) //bitcoin testnet coin
                .cKDpriv(hard(0)) //account =1
                .cKDpriv(0) //external
                .cKDpriv(0) //first address
                .neuter().p2pkhAddress()

        Log.i("BIP44 Addresses I: ", addressMethod1)
    }

    private fun generateSecondBIP44Addresses(root: PrivateKey?) {
        val addressIndex = m()
                .purpose44()
                .coinType(1)
                .account(0)
                .external()
                .address(0)
        val addressMethod4 = root?.derive(addressIndex, AddressIndex.DERIVATION)
                ?.neuter()?.p2pkhAddress()

        Log.i("BIP44 Addresses II: ", addressMethod4)
    }

    private fun printAddresses(root: PrivateKey) {
        val account = m().purpose44()
                .coinType(1)
                .account(0)
        val accountKey = root.derive(account, Account.DERIVATION)
                .neuter()

        val external = account.external()

        for (i in 0..19) {
            val derivationPath = external.address(i)
            val publicKey = accountKey.derive(derivationPath, AddressIndex.DERIVATION_FROM_ACCOUNT)
            Log.i("BIP44 Addresses II: ", derivationPath.toString() + " = " + publicKey.p2pkhAddress())
        }
    }
}

