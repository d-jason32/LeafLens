package com.example.android_development_capstone.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyRepository(context: Context) {

   private val database: StudyDatabase = Room.databaseBuilder(
      context.applicationContext,
      StudyDatabase::class.java,
      "study.db"
   )
      .addCallback(object : RoomDatabase.Callback() {
         override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Use the database instance directly to insert data
            // This runs on a background thread automatically
            CoroutineScope(Dispatchers.IO).launch {
               // Get DAOs from the database instance
               val dbInstance = database
               val subjDao = dbInstance.subjectDao()
               val questDao = dbInstance.questionDao()
               addStarterData(subjDao, questDao)
            }
         }
      })
      .build()

   private val subjectDao = database.subjectDao()
   private val questionDao = database.questionDao()

   fun getSubject(subjectId: Long) = subjectDao.getSubject(subjectId)

   fun getSubjects() = subjectDao.getSubjects()

   fun addSubject(subject: Subject) {
      if (subject.title.trim() != "") {
         CoroutineScope(Dispatchers.IO).launch {
            subject.id = subjectDao.addSubject(subject)
         }
      }
   }

   fun deleteSubject(subject: Subject) {
      CoroutineScope(Dispatchers.IO).launch {
         subjectDao.deleteSubject(subject)
      }
   }

   fun getQuestion(questionId: Long) = questionDao.getQuestion(questionId)

   fun getQuestions(subjectId: Long) = questionDao.getQuestions(subjectId)

   fun addQuestion(question: Question) {
      CoroutineScope(Dispatchers.IO).launch {
         question.id = questionDao.addQuestion(question)
      }
   }

   fun updateQuestion(question: Question) {
      CoroutineScope(Dispatchers.IO).launch {
         questionDao.updateQuestion(question)
      }
   }

   fun deleteQuestion(question: Question) {
      CoroutineScope(Dispatchers.IO).launch {
         questionDao.deleteQuestion(question)
      }
   }

   private fun addStarterData(subjDao: SubjectDao, questDao: QuestionDao) {
       // Plant anatomy
      var subjectId = subjDao.addSubject(Subject(title = "Plant Anatomy"))
      questionDao.addQuestion(
         Question(
            text = "What part of the plant makes food using sunlight?",
            answer = "The leaf.",
            subjectId = subjectId
         )
      )
      questionDao.addQuestion(
         Question(
            text = "What part of the plant absorbs water and nutrients from the soil?",
            answer = "The roots.",
            subjectId = subjectId
         )
      )
      questionDao.addQuestion(
         Question(
            text = "What part of the plant carries water up from the roots to the leaves?",
            answer = "The stem.",
            subjectId = subjectId
         )
      )
       questionDao.addQuestion(
           Question(
               text = "What part of the flower produces seeds?",
               answer = "The ovary",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What colorful part of a flower attracts pollinators?",
               answer = "The petals.",
               subjectId = subjectId
           )
       )

       // Plant Physiology
      subjectId = subjectDao.addSubject(Subject(title = "Plant Physiology"))
       questionDao.addQuestion(
           Question(
               text = "What process allows plants to make their own food using sunlight?",
               answer = "Photosynthesis",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What gas do plants take in during photosynthesis?",
               answer = "Carbon dioxide",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What gas do plants release during photosynthesis?",
               answer = "Oxygen",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What tubes in a plant carry water upwards from the roots?",
               answer = "Xylem",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What tubes in a plant carry sugars throughout the plant?",
               answer = "Phloem",
               subjectId = subjectId
           )
       )

       // Plant Chemistry
       subjectId = subjectDao.addSubject(Subject(title = "Plant Chemistry"))
       questionDao.addQuestion(
           Question(
               text = "What pigment gives plants their green color?",
               answer = "Photosynthesis",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What molecule do plants produce as their main source of stored energy during photosynthesis",
               answer = "Glucose",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What element is needed to build chlorophyll?",
               answer = "Nitrogen",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "How do plants get energy to drive chemical reactions?",
               answer = "The sun.",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What gas do plants release as a product of splitting water molecules?",
               answer = "Oxygen.",
               subjectId = subjectId
           )
       )

       // Plant History
       subjectId = subjectDao.addSubject(Subject(title = "Plant History"))
       questionDao.addQuestion(
           Question(
               text = "Who is the father of Taxonomy?",
               answer = "Carl Linnaeus",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "Who discovered that plants pass traits from parents to offspring?",
               answer = "Gregor Mendel",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "Who discovered that plants grow towards light?",
               answer = "Charles Darwin",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What female botanist discovered how plants detect day length?",
               answer = "Barbara McClintock",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "Who is the Father of Modern Genetics?",
               answer = "Gregor Mendel",
               subjectId = subjectId
           )
       )

       // Horticulture
       subjectId = subjectDao.addSubject(Subject(title = "Horticulture"))
       questionDao.addQuestion(
           Question(
               text = "What is horticulture?",
               answer = "The science and art of growing plants?",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What is the term for growing plants in containers instead of the ground?",
               answer = "Container gardening",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What tool is used for digging small holes for planting?",
               answer = "A trowel.",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What is the practice of cutting off dead or overgrown branches to help plants grow better?",
               answer = "Pruning",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What do gardeners add to soil to improve its nutrients?",
               answer = "Compost.",
               subjectId = subjectId
           )
       )

       // Gardening tips
       subjectId = subjectDao.addSubject(Subject(title = "Gardening Tips"))
       questionDao.addQuestion(
           Question(
               text = "Why is it important to water plants at the base?",
               answer = "To make sure water reaches the roots.",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What is the best time of day to water most plants?",
               answer = "Early morning",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "Why should you remove yellowing leaves?",
               answer = "It promotes healthy growth.",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "What does mulch help with?",
               answer = "Keeping soil moist",
               subjectId = subjectId
           )
       )
       questionDao.addQuestion(
           Question(
               text = "Why is it important to check how much sunlight a plant needs?",
               answer = "Different plants need different amount of sunlight.",
               subjectId = subjectId
           )
       )


   }
}

