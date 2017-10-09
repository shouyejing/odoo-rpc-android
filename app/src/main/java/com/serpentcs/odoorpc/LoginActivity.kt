package com.serpentcs.odoorpc

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.serpentcs.odoorpc.core.Odoo
import com.serpentcs.odoorpc.core.utils.*
import com.serpentcs.odoorpc.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        @JvmField
        val TAG = "LoginActivity"

        @JvmField
        val ADD_ACCOUNT: String = "authenticator_add_account"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForLoggedInUser()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setSupportActionBar(binding.tb)

        binding.tlHost.post {
            binding.tlHost.isErrorEnabled = false
        }

        binding.etHost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tlHost.post {
                    binding.tlHost.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })

        binding.bnCheckVersion.setOnClickListener {
            if (binding.etHost.text.toString().isEmpty()) {
                binding.tlHost.error = getString(R.string.login_host_error)
                return@setOnClickListener
            }

            hideSoftKeyboard()
            binding.spProtocol.post {
                binding.spProtocol.isEnabled = false
            }
            binding.tlHost.post {
                binding.tlHost.isEnabled = false
            }
            binding.bnCheckVersion.post {
                binding.bnCheckVersion.visibility = View.GONE
            }
            binding.llCheckVersionProgress.post {
                binding.llCheckVersionProgress.visibility = View.VISIBLE
            }
            binding.llCheckVersionResult.post {
                binding.llCheckVersionResult.visibility = View.GONE
            }
            resetLoginLayout()
            binding.llLogin.post {
                binding.llLogin.visibility = View.GONE
            }

            Odoo.protocol = when (binding.spProtocol.selectedItemPosition) {
                0 -> {
                    Retrofit2Helper.Companion.Protocol.HTTP
                }
                else -> {
                    Retrofit2Helper.Companion.Protocol.HTTPS
                }
            }
            Odoo.host = binding.etHost.text.toString()
            Odoo.versionInfo { versionInfo ->
                binding.spProtocol.post {
                    binding.spProtocol.isEnabled = true
                }
                binding.tlHost.post {
                    binding.tlHost.isEnabled = true
                }
                binding.bnCheckVersion.post {
                    binding.bnCheckVersion.visibility = View.VISIBLE
                }
                binding.llCheckVersionProgress.post {
                    binding.llCheckVersionProgress.visibility = View.GONE
                }
                binding.llCheckVersionResult.post {
                    binding.llCheckVersionResult.visibility = View.VISIBLE
                }
                if (versionInfo.isSuccessful) {
                    logD(TAG, versionInfo.toString())
                    if (versionInfo.result.serverVersion in Odoo.supportedOdooVersions) {
                        Odoo.list { list ->
                            if (list.isSuccessful) {
                                logD(TAG, list.toString())
                                binding.ivSuccess.post {
                                    binding.ivSuccess.visibility = View.VISIBLE
                                }
                                binding.ivFail.post {
                                    binding.ivFail.visibility = View.GONE
                                }
                                binding.tvServerMessage.post {
                                    binding.tvServerMessage.text = getString(
                                            R.string.login_server_success,
                                            versionInfo.result.serverVersion
                                    )
                                }
                                binding.spDatabase.post {
                                    binding.spDatabase.adapter = ArrayAdapter(
                                            this@LoginActivity,
                                            R.layout.support_simple_spinner_dropdown_item,
                                            list.result
                                    )
                                    binding.spDatabase.visibility =
                                            if (list.result.size == 1) {
                                                View.GONE
                                            } else {
                                                View.VISIBLE
                                            }
                                }
                                binding.llLogin.post {
                                    binding.llLogin.visibility = View.VISIBLE
                                }
                            } else {
                                logD(TAG, "Error: " + list.errorCode + ": " + list.errorMessage)
                                binding.ivSuccess.post {
                                    binding.ivSuccess.visibility = View.GONE
                                }
                                binding.ivFail.post {
                                    binding.ivFail.visibility = View.VISIBLE
                                }
                                binding.tvServerMessage.post {
                                    binding.tvServerMessage.text = list.errorMessage
                                }
                            }
                        }
                    } else {
                        binding.ivSuccess.post {
                            binding.ivSuccess.visibility = View.GONE
                        }
                        binding.ivFail.post {
                            binding.ivFail.visibility = View.VISIBLE
                        }
                        binding.tvServerMessage.post {
                            binding.tvServerMessage.text = getString(
                                    R.string.login_server_error,
                                    versionInfo.result.serverVersion
                            )
                        }
                    }
                } else {
                    logD(TAG, "Error: " + versionInfo.errorCode + ": " + versionInfo.errorMessage)
                    binding.ivSuccess.post {
                        binding.ivSuccess.visibility = View.GONE
                    }
                    binding.ivFail.post {
                        binding.ivFail.visibility = View.VISIBLE
                    }
                    binding.tvServerMessage.post {
                        binding.tvServerMessage.text = versionInfo.errorMessage
                    }
                }
            }
        }

        binding.tlLogin.post {
            binding.tlLogin.isErrorEnabled = false
        }

        binding.etLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tlLogin.post {
                    binding.tlLogin.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })

        binding.tlPassword.post {
            binding.tlPassword.isErrorEnabled = false
        }

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.tlPassword.post {
                    binding.tlPassword.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        })

        binding.bn.setOnClickListener {
            if (binding.etLogin.text.toString().isEmpty()) {
                binding.tlLogin.error = getString(R.string.login_username_error)
                return@setOnClickListener
            }

            if (binding.etPassword.text.toString().isEmpty()) {
                binding.tlPassword.error = getString(R.string.login_password_error)
                return@setOnClickListener
            }

            val selectedDatabase = binding.spDatabase.selectedItem
            if (selectedDatabase == null || selectedDatabase.toString().isEmpty()) {
                showMessage(message = getString(R.string.login_database_error))
                return@setOnClickListener
            }

            hideSoftKeyboard()
            binding.spProtocol.post {
                binding.spProtocol.isEnabled = false
            }
            binding.tlHost.post {
                binding.tlHost.isEnabled = false
            }
            binding.bnCheckVersion.post {
                binding.bnCheckVersion.visibility = View.GONE
            }
            binding.tlLogin.post {
                binding.tlLogin.isEnabled = false
            }
            binding.tlPassword.post {
                binding.tlPassword.isEnabled = false
            }
            binding.spDatabase.post {
                binding.spDatabase.isEnabled = false
            }
            binding.bn.post {
                binding.bn.visibility = View.GONE
            }
            binding.tvLoginProgress.post {
                binding.tvLoginProgress.text = getString(R.string.login_progress)
            }
            binding.llProgress.post {
                binding.llProgress.visibility = View.VISIBLE
            }
            binding.llError.post {
                binding.llError.visibility = View.GONE
            }

            Odoo.login = binding.etLogin.text.toString()
            Odoo.password = binding.etPassword.text.toString()
            Odoo.database = selectedDatabase.toString()
            Odoo.authenticate { authenticate ->
                if (authenticate.isSuccessful) {
                    binding.tvLoginProgress.post {
                        binding.tvLoginProgress.text = getString(R.string.login_success)
                    }
                    createAccount()
                } else {
                    binding.spProtocol.post {
                        binding.spProtocol.isEnabled = true
                    }
                    binding.tlHost.post {
                        binding.tlHost.isEnabled = true
                    }
                    binding.bnCheckVersion.post {
                        binding.bnCheckVersion.visibility = View.VISIBLE
                    }
                    binding.tlLogin.post {
                        binding.tlLogin.isEnabled = true
                    }
                    binding.tlPassword.post {
                        binding.tlPassword.isEnabled = true
                    }
                    binding.spDatabase.post {
                        binding.spDatabase.isEnabled = true
                    }
                    binding.bn.post {
                        binding.bn.visibility = View.VISIBLE
                    }
                    binding.llProgress.post {
                        binding.llProgress.visibility = View.GONE
                    }
                    binding.tvLoginError.post {
                        binding.tvLoginError.text = authenticate.errorMessage
                    }
                    binding.llError.post {
                        binding.llError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun resetLoginLayout() {
        binding.tlLogin.post {
            binding.tlLogin.isErrorEnabled = false
            binding.tlLogin.isEnabled = true
        }
        // binding.etLogin.post {
        //     binding.etLogin.text.clear()
        // }
        binding.tlPassword.post {
            binding.tlPassword.isErrorEnabled = false
            binding.tlPassword.isEnabled = true
        }
        // binding.etPassword.post {
        //     binding.etPassword.text.clear()
        // }
        // binding.spDatabase.post {
        //     binding.spDatabase.adapter = null
        // }
        binding.spDatabase.post {
            binding.spDatabase.isEnabled = true
        }
        binding.llError.post {
            binding.llError.visibility = View.GONE
        }
    }

    private fun createAccount() {
        object : AsyncTask<Void?, Void?, Boolean>() {
            override fun doInBackground(vararg p0: Void?) =
                    if (createOdooUser()) {
                        val odooUser = odooUserByAndroidName(
                                Odoo.androidName
                        )
                        if (odooUser != null) {
                            loginOdooUser(
                                    odooUser
                            )
                        }
                        true
                    } else {
                        false
                    }

            override fun onPostExecute(result: Boolean) {
                super.onPostExecute(result)
                if (result) {
                    val intent = intent
                    if (intent != null && intent.hasExtra(ADD_ACCOUNT)) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        startMainActivity()
                    }
                } else {
                    binding.llProgress.post {
                        binding.llProgress.visibility = View.GONE
                    }
                    binding.llError.post {
                        binding.llError.visibility = View.VISIBLE
                    }
                    binding.tvLoginError.post {
                        binding.tvLoginError.text = getString(R.string.login_create_account_error)
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun checkForLoggedInUser() {
        val intent = intent
        if (intent == null || !intent.hasExtra(ADD_ACCOUNT)) {
            val activeUser = getActiveOdooUser()
            if (activeUser != null) {
                Odoo.initFromUser(activeUser)
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}
