package com.marcosandre.weatherapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.marcosandre.weatherapp.ui.theme.WeatherAppTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConfirm by rememberSaveable { mutableStateOf("") }
    val activity = LocalContext.current as Activity

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = "Registro",
            fontSize = 24.sp
        )

        Spacer(modifier = modifier.size(6.dp))

        Text(
            text = "Preencha abaixo",
            fontSize = 16.sp
        )

        Spacer(modifier = modifier.size(10.dp))

        OutlinedTextField(
            value = name,
            label = { Text(text = "Digite seu nome") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { name = it }
        )

        Spacer(modifier = modifier.size(6.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { email = it }
        )

        Spacer(modifier = modifier.size(6.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = modifier.size(6.dp))

        OutlinedTextField(
            value = passwordConfirm,
            label = { Text(text = "Confirme sua senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { passwordConfirm = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = modifier.size(10.dp))

        Row(modifier = modifier) {
            Button( onClick = {

                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Registro OK!", Toast.LENGTH_LONG).show()
                            //activity.finish()  // usuário já está logado automaticamente
                        } else {
                            Toast.makeText(activity, "Registro FALHOU!", Toast.LENGTH_LONG).show()
                        }
                    }

            },
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && (password == passwordConfirm)
            ) {
                Text("Registrar")
            }

            Spacer(modifier = modifier.size(4.dp))

            Button(
                onClick = { name = ""; email = ""; password = "" ; passwordConfirm = "" }
            ) {
                Text("Limpar")
            }
        }
    }
}
