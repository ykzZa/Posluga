package dev.ykzza.posluga.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dev.ykzza.posluga.data.entities.Project
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProjectRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: StorageReference
) : ProjectRepository {

    override fun postProject(
        project: Project,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    ) {
        val document = db.collection(Constants.PROJECT_COLLECTION).document()
        project.projectId = document.id
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

}
