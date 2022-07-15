package com.proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns

import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.proyecto.databinding.ActivityRegistrarUsuarioBinding
import com.proyecto.model.Usuario

class RegistrarUsuario : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegistrarUsuarioBinding
    lateinit var mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        mDatabase = FirebaseDatabase.getInstance().getReference("Usuarios")

        binding.btCrearUsuario.setOnClickListener { haceRegistro() }

    }

    private fun haceRegistro() {
        val nombre = binding.etNombre.text.toString()
        val email = binding.etCorreo.text.toString()
        val clave = binding.etClave.text.toString()
        val alias = binding.etNombreUsuario.text.toString()

        if(nombre.isEmpty()){
            binding.etNombre.setError("El nombre es requerido!");
            binding.etNombre.requestFocus();
            return;
        }
        if(email.isEmpty()){
            binding.etCorreo.setError("El email es requerido!");
            binding.etCorreo.requestFocus();
            return;
        }
        if(clave.isEmpty()){
            binding.etClave.setError("La clave es requerido!");
            binding.etClave.requestFocus();
            return;
        }
        if(alias.isEmpty()){
            binding.etNombreUsuario.setError("El nombre de usuario es requerido!");
            binding.etNombreUsuario.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etCorreo.setError("El email no es valido!");
            binding.etCorreo.requestFocus();
        }


        //Se usa la función para crear un usuario por medio de correo y contraseña
        auth.createUserWithEmailAndPassword(email,clave)
            .addOnCompleteListener(this) { task ->

               if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user!!.uid
                   mDatabase.child(uid).child("Usuarios").setValue(nombre)
                   mDatabase.child(uid).child("Usuarios").setValue(alias)
                   mDatabase.child(uid).child("Usuarios").setValue(email)
                    actualiza(user)
                } else {
                    Toast.makeText(baseContext,
                        getString(R.string.msg_fallo_registro),
                        Toast.LENGTH_SHORT).show()
                    actualiza(null)
                }
            }
    }

    private fun actualiza(user: FirebaseUser?) {
        if (user!=null) {
            // paso a la pantalla principal
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        actualiza(user)
    }

}


