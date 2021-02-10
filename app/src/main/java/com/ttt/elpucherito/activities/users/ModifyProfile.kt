package com.ttt.elpucherito.activities.users

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ttt.elpucherito.R
import com.ttt.elpucherito.db.ElPucheritoDB

class ModifyProfile : AppCompatActivity(), View.OnClickListener {

    private var modifyTvmodify: TextView? = null
    private var modifyEtEmail: EditText? = null
    private var modifyEtAddress: EditText? = null
    private var modifyEtPassword: EditText? = null
    private var modifyBtnSave: Button? = null

    private var modifyEtName: EditText? = null
    private var modifyEtSurname: EditText? = null
    private var modifyEtPhone: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_profile)

        modifyTvmodify = findViewById(R.id.modify_tv_modify)
        modifyBtnSave = findViewById(R.id.modify_btn_save)

        modifyEtName = findViewById(R.id.modify_et_name)
        modifyEtSurname = findViewById(R.id.modify_et_surname)
        modifyEtAddress = findViewById(R.id.modify_et_address)
        modifyEtEmail = findViewById(R.id.modify_et_email)
        modifyEtPassword = findViewById(R.id.modify_et_password)
        modifyEtPhone = findViewById(R.id.modify_et_phonenumber)

        modifyBtnSave!!.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        updateUser()
    }

    fun updateUser() {
        var name = modifyEtName?.text.toString()
        var surname = modifyEtSurname?.text.toString()
        var address = modifyEtAddress?.text.toString()
        var email = modifyEtEmail?.text.toString()
        var password = modifyEtPassword?.text.toString()
        var phone = modifyEtPhone?.text.toString()

        Thread {

            var db: ElPucheritoDB = ElPucheritoDB.getInstance(this)
            val user = db.userDao().getLoggedUser()

            if (name.isEmpty()) {
                user.name = name
            }
            if (surname.isEmpty()) {
                user.surname = surname
            }
            if (address.isEmpty()) {
                user.address = address
            }
            if (phone.isEmpty()) {
                user.phonenum = phone.toInt()
            }
            if (email.isEmpty()) {
                user.email = email
            }
            if (password.isEmpty()) {
                user.password = password
            }

            db.userDao().updateUser(user)

            println(user)
        }.start()
    }
}