package com.example.android_development_capstone

import kotlinx.serialization.Serializable

@Serializable
object Routes {
   @Serializable
   data class Question(val subjectId: Long, val showLastQuestion: Boolean = false)

   @Serializable
   data class AddQuestion(val subjectId: Long)

   @Serializable
   data class EditQuestion(val questionId: Long)
}

