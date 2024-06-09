package dev.ykzza.posluga.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.ykzza.posluga.data.entities.SearchResult
import dev.ykzza.posluga.data.entities.Service
import dev.ykzza.posluga.util.Constants
import dev.ykzza.posluga.util.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.xdrop.fuzzywuzzy.FuzzySearch

class ServiceRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: StorageReference
) : ServiceRepository {

    override fun postService(
        service: Service,
        imagesList: List<String>,
        result: (UiState<String>) -> Unit
    ) {
        val document: DocumentReference
        if (service.serviceId == "") {
            document = db.collection(Constants.SERVICE_COLLECTION).document()
            service.serviceId = document.id
        } else {
            document = db.collection(Constants.SERVICE_COLLECTION).document(service.serviceId)
        }
        service.images = imagesList
        document
            .set(service)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Service has been added successfully."
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

    override fun getService(serviceId: String, result: (UiState<Service>) -> Unit) {
        db.collection(Constants.SERVICE_COLLECTION).document(serviceId).get()
            .addOnSuccessListener {
                val service = it.toObject(Service::class.java)
                if (service != null) {
                    result.invoke(
                        UiState.Success(
                            service
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

    override fun getServices(
        searchQuery: String?,
        descriptionSearch: Boolean,
        minPrice: Int?,
        maxPrice: Int?,
        category: String?,
        subCategory: String?,
        state: String?,
        city: String?,
        result: (UiState<List<Service>>) -> Unit
    ) {
        val servicesRef =
            db.collection(Constants.SERVICE_COLLECTION).orderBy("date", Query.Direction.DESCENDING)
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

        val queryRef = servicesRef.where(
            Filter.and(
                *filterList.toTypedArray()
            )
        )

        queryRef.get()
            .addOnSuccessListener { querySnapshot ->
                val services = mutableListOf<Service>()
                for (document in querySnapshot) {
                    val service = document.toObject(Service::class.java)
                    services.add(service)
                }
                if (searchQuery != null) {
                    result.invoke(
                        UiState.Success(
                            filterServices(
                                services,
                                searchQuery,
                                descriptionSearch
                            )
                        )
                    )
                } else {
                    result.invoke(
                        UiState.Success(
                            services
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

    override fun getUserServices(userId: String, result: (UiState<List<Service>>) -> Unit) {
        db.collection(Constants.SERVICE_COLLECTION)
            .whereEqualTo("authorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val services = querySnapshot.toObjects(Service::class.java)
                result.invoke(
                    UiState.Success(
                        services
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

    override fun deleteService(serviceId: String, result: (UiState<String>) -> Unit) {
        db.collection(Constants.SERVICE_COLLECTION).document(serviceId)
            .delete()
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(
                        "Service has been deleted"
                    )
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Error(
                        "Failed to delete service"
                    )
                )
            }
    }

    override suspend fun getServicesByIds(
        idList: List<String>,
        result: (UiState<List<Service>>) -> Unit
    ) {
        val services = mutableListOf<Service>()
        for (serviceId in idList) {
            val document =
                db.collection(Constants.SERVICE_COLLECTION).document(serviceId).get().await()
            if (document.exists()) {
                val service = document.toObject(Service::class.java)
                if (service != null) {
                    services.add(service)
                }
            }
        }
        result.invoke(
            UiState.Success(
                services
            )
        )
    }

    private fun filterServices(
        services: List<Service>,
        searchQuery: String,
        descriptionSearch: Boolean
    ): List<Service> {
        val lowerCaseQuery = searchQuery.lowercase()
        val searchResults = services.mapNotNull { service ->
            val titleMatchScore =
                FuzzySearch.partialRatio(lowerCaseQuery, service.title.lowercase())
            val descriptionMatchScore = if (descriptionSearch) {
                FuzzySearch.partialRatio(lowerCaseQuery, service.description.lowercase())
            } else {
                0
            }
            if (titleMatchScore > 50 || descriptionMatchScore > 50) {
                val highestScore = maxOf(titleMatchScore, descriptionMatchScore)
                SearchResult(service, highestScore)
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
        val folder = Constants.SERVICE_PICTURES_FOLDER
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

