package com.example.hobbittracker.data.repository

import com.example.hobbittracker.data.extension.await
import com.example.hobbittracker.domain.entity.CategoryHabits
import com.example.hobbittracker.domain.repository.CategoryHabitsRepository
import com.example.hobbittracker.domain.utils.Result
import com.google.firebase.firestore.FirebaseFirestore

class CategoryHabitsRepositoryImpl : CategoryHabitsRepository {

    companion object {
        private const val COLLECTION_NAME = "categories"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collection = firestoreInstance.collection(COLLECTION_NAME)

    override suspend fun getCategory(id: Int): Result<CategoryHabits> {
        return try {
            when (val resultDocumentSnapshot =
                collection.document(id.toString()).get().await()) {
                is Result.Success -> {
                    val category =
                        resultDocumentSnapshot.data.toObject(CategoryHabits::class.java)!!
                    Result.Success(category)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun getCategoriesAll(): Result<List<CategoryHabits>> {
        return try {
            when (val result =
                collection.get().await()) {
                is Result.Success -> {
                    val cats = result.data.toObjects(CategoryHabits::class.java)
                    Result.Success(cats)
                }
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun updateCategory(categoryHabits: CategoryHabits): Result<Void?> {
        val updatedCategory = mapOf(
            "name" to categoryHabits.name,
            "color" to categoryHabits.color
        )

        return try {
            val documentQuery = collection
                .document(categoryHabits.id.toString())
                .update(updatedCategory)

            when (val result = documentQuery.await()) {
                is Result.Success -> Result.Success(null)
                is Result.Error -> Result.Error(result.exception)
                is Result.Canceled -> Result.Canceled(result.exception)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}