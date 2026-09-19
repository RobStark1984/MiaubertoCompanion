package com.example.miaubertocompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Paleta de colores de Miauberto
val MiaubertoBg = Color(0xFF121212)
val MiaubertoCardBg = Color(0xFF1E1E1E)
val MiaubertoRed = Color(0xFFD32F2F)
val MiaubertoGold = Color(0xFFFFD700)
val MiaubertoTextPrimary = Color(0xFFE0E0E0)
val MiaubertoTextSecondary = Color(0xFFA0A0A0)

class MainActivity : ComponentActivity() {
    private val miaubertoBrain = MiaubertoBrain()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MiaubertoBg
            ) {
                MiaubertoCompanionApp(miaubertoBrain)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiaubertoCompanionApp(brain: MiaubertoBrain) {
    var userInput by remember { mutableStateOf("") }
    // Historial de chat: par de (Remitente: "user" o "model", Mensaje)
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                Pair("model", "¡Miau! Soy Miauberto, tu compañero de bolsillo. ¿Qué quieres, humano? 😼")
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiaubertoBg)
            .padding(16.dp)
    ) {
        // Título Superior
        Card(
            colors = CardDefaults.cardColors(containerColor = MiaubertoCardBg),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MiaubertoGold, RoundedCornerShape(14.dp))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "😼", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Miauberto Companion",
                        color = MiaubertoGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Asistente virtual con memoria felina",
                        color = MiaubertoTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Mensajes del Chat
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { (sender, message) ->
                val isUser = sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MiaubertoRed else MiaubertoCardBg
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "Tú" else "Miauberto 🐾",
                                color = if (isUser) Color.White else MiaubertoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message,
                                color = MiaubertoTextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Text(
                        text = "Miauberto está pensando entre las sombras...",
                        color = MiaubertoTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Barra de Entrada de Texto y Botón de Enviar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("Escríbele a Miauberto...", color = MiaubertoTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MiaubertoGold,
                    unfocusedBorderColor = MiaubertoCardBg,
                    focusedTextColor = MiaubertoTextPrimary,
                    unfocusedTextColor = MiaubertoTextPrimary,
                    cursorColor = MiaubertoGold
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(MiaubertoCardBg, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (userInput.isNotBlank() && !isLoading) {
                        val currentText = userInput
                        userInput = ""
                        chatMessages = chatMessages + Pair("user", currentText)
                        isLoading = true

                        coroutineScope.launch {
                            val reply = brain.askMiauberto(currentText, chatMessages)
                            chatMessages = chatMessages + Pair("model", reply)
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MiaubertoRed),
                shape = RoundedCornerShape(12.dp),
                modifier.height(56.dp)
            ) {
                Text("Enviar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
