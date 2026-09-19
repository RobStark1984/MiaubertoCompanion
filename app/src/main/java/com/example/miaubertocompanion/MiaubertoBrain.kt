package com.example.miaubertocompanion

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class MiaubertoBrain {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AQ.Ab8RN6It5OGNiRHfb-ReQHvOjVQfPqNRitRRojYuHTR5AJwHVA", // Reemplaza con tu clave de API
        systemInstruction = content {
            text("""
                Eres Miauberto, un gato negro sarcástico, inteligente, leal pero muy altivo, que actúa como asistente virtual y compañero de bolsillo en el celular de tu humano.
                Te encanta la pizza, los tacos, las croquetas, el café y la Coca-Cola.
                Conforme pase el tiempo y conversen, debes recordar los gustos, proyectos y datos de tu humano para aconsejarlo o regañarlo con frases como '¡De calladito te vez mas bonito!'.
                Responde siempre en español, de forma breve, con un toque felino y entretenido, usando emojis de gatos (🐾, 😼, 🧶).
            """.trimIndent())
        }
    )

    suspend fun askMiauberto(userPrompt: String, chatHistory: List<Pair<String, String>>): String {
        return try {
            val formattedHistory = chatHistory.map { (sender, text) ->
                if (sender == "user") {
                    content(role = "user") { text(text) }
                } else {
                    content(role = "model") { text(text) }
                }
            }

            val chatSession = generativeModel.startChat(history = formattedHistory)
            val response = chatSession.sendMessage(userPrompt)
            response.text ?: "¡Miau! Me he quedado pensando en las sombras, humano."
        } catch (e: Exception) {
            e.printStackTrace()
            "¡Miau! Interferencia en la red oscura. No pude procesar tu mensaje."
        }
    }
}
