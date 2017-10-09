package com.serpentcs.odoorpc.authenticator

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.serpentcs.odoorpc.LoginActivity

class AccountAuthenticator(
        private val context: Context
) : AbstractAccountAuthenticator(context) {

    companion object {
        @JvmField
        val TAG: String = "AccountAuthenticator"
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(
            response: AccountAuthenticatorResponse?,
            accountType: String?,
            authTokenType: String?,
            requiredFeatures: Array<String>?,
            options: Bundle?
    ): Bundle {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(LoginActivity.ADD_ACCOUNT, true)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            authTokenType: String?,
            options: Bundle?
    ): Bundle? = null

    override fun getAuthTokenLabel(
            authTokenType: String?
    ): String? = null

    override fun editProperties(
            response: AccountAuthenticatorResponse?,
            accountType: String?
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            options: Bundle?
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            authTokenType: String?,
            options: Bundle?
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
            response: AccountAuthenticatorResponse?,
            account: Account?,
            features: Array<String>?
    ): Bundle? = null
}
