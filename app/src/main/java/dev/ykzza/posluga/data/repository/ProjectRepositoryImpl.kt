package dev.ykzza.posluga.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.util.SearchResult
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.xdrop.fuzzywuzzy.FuzzySearch

class ProjectRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: StorageReference
) : ProjectRepository {

    override fun postProject(
        project: Project,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    ) {
        val document: DocumentReference
        if (project.projectId == "") {
            document = db.collection(Constants.PROJECT_COLLECTION).document()
            project.projectId = document.id
        } else {
            document = db.collection(Constants.PROJECT_COLLECTION).document(project.projectId)
        }
        project.images = imagesList
        document
            .set(project)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Project has been added successfully."
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun getProject(projectId: String, result: (UiState<Project>) -> Unit) {
        db.collection(Constants.PROJECT_COLLECTION).document(projectId).get()
            .addOnSuccessListener {
                val project = it.toObject(Project::class.java)
                if (project != null) {
                    result.invoke(
                        UiState.Success(
                            project
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun getUserProjects(userId: String, result: (UiState<List<Project>>) -> Unit) {
        db.collection(Constants.PROJECT_COLLECTION)
            .whereEqualTo("authorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val project = querySnapshot.toObjects(Project::class.java)
                result.invoke(
                    UiState.Success(
                        project
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    override fun deleteProject(projectId: String, result: (UiState<String>) -> Unit) {
        db.collection(Constants.PROJECT_COLLECTION).document(projectId)
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Project has been deleted"
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        "Failed to delete project"
                    )
                )
            }
    }

    override fun getProjects(
        searchQuery: String?,
        descriptionSearch: Boolean,
        minPrice: Int?,
        maxPrice: Int?,
        category: String?,
        subCategory: String?,
        state: String?,
        city: String?,
        result: (UiState<List<Project>>) -> Unit
    ) {

        val projectsRef =
            db.collection(Constants.PROJECT_COLLECTION).orderBy("date", Query.Direction.DESCENDING)
        val filterList = mutableListOf<Filter>()

        if (category != null) {
            filterList.add(
                Filter.equalTo(
                    "category", category
                )
            )
        }
        if (subCategory != null) {
            filterList.add(
                Filter.equalTo(
                    "subCategory", subCategory
                )
            )
        }
        if (state != null) {
            filterList.add(
                Filter.equalTo(
                    "state", state
                )
            )
        }
        if (city != null) {
            filterList.add(
                Filter.equalTo(
                    "city",
                    city
                )
            )
        }
        if (minPrice != null) {
            filterList.add(
                Filter.greaterThanOrEqualTo(
                    "price",
                    minPrice
                )
            )
        }
        if (maxPrice != null) {
            filterList.add(
                Filter.lessThanOrEqualTo(
                    "price", maxPrice
                )
            )
        }

        val queryRef = projectsRef.where(
            Filter.and(
                *filterList.toTypedArray()
            )
        )

        queryRef.get()
            .addOnSuccessListener { querySnapshot ->
                val projects = mutableListOf<Project>()
                for (document in querySnapshot) {
                    val project = document.toObject(Project::class.java)
                    projects.add(project)
                }
                if (searchQuery != null) {
                    result.invoke(
                        UiState.Success(
                            filterProjects(
                                projects,
                                searchQuery,
                                descriptionSearch
                            )
                        )
                    )
                } else {
                    result.invoke(
                        UiState.Success(
                            projects
                        )
                    )
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        it.localizedMessage ?: "Oops, something went wrong"
                    )
                )
            }
    }

    private fun filterProjects(
        projects: List<Project>,
        searchQuery: String,
        descriptionSearch: Boolean
    ): List<Project> {
        val lowerCaseQuery = searchQuery.lowercase()
        val searchResults = projects.mapNotNull { project ->
            val titleMatchScore =
                FuzzySearch.partialRatio(lowerCaseQuery, project.title.lowercase())
            val descriptionMatchScore = if (descriptionSearch) {
                FuzzySearch.partialRatio(lowerCaseQuery, project.description.lowercase())
            } else {
                0
            }
            if (titleMatchScore > 50 || descriptionMatchScore > 50) {
                val highestScore = maxOf(titleMatchScore, descriptionMatchScore)
                SearchResult(project, highestScore)
            } else {
                null
            }
        }
        return searchResults.map { it.item }
    }

    override suspend fun uploadImages(
        userId: String,
        imagesUrl: List<Uri>,
        result: (UiState<List<String>>) -> Unit
    ) {
        val folder = Constants.PROJECT_PICTURES_FOLDER
        try {
            val resultList = withContext(Dispatchers.IO) {
                imagesUrl.map { image ->
                    async {
                        storage
                            .child(
                                "$folder/$userId:" +
                                        "${image.lastPathSegment}:" +
                                        "${System.currentTimeMillis()}"
                            )
                            .putFile(image)
                            .await()
                            .storage
                            .downloadUrl
                            .await()
                    }
                }.awaitAll()
            }
            result.invoke(
                UiState.Success(
                    resultList.map {
                        it.toString()
                    }
                )
            )
        } catch (e: Exception) {
            result.invoke(
                UiState.Error(
                    "Failed to upload images"
                )
            )
        }
    }

    override suspend fun deleteImages(
        imageUrls: List<String>,
        result: (UiState<List<String>>) -> Unit
    ) {
        try {
            withContext(Dispatchers.IO) {
                imageUrls.map { image ->
                    async {
                        FirebaseStorage
                            .getInstance()
                            .getReferenceFromUrl(image)
                            .delete()
                            .await()
                    }
                }.awaitAll()
            }
            result.invoke(
                UiState.Success(listOf())
            )
        } catch (e: Exception) {
            result.invoke(
                UiState.Error(
                    "Failed to delete images"
                )
            )
        }
    }
}
