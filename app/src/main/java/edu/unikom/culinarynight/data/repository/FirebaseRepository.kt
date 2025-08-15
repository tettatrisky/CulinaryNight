package edu.unikom.culinarynight.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.unikom.culinarynight.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Auth methods
    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    name = name
                )
                saveUserToFirestore(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = getUserFromFirestore(firebaseUser.uid)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    private suspend fun getUserFromFirestore(userId: String): User {
        val document = firestore.collection("users").document(userId).get().await()
        return document.toObject(User::class.java) ?: User()
    }

    // Review methods
    suspend fun addReview(review: Review): Result<Unit> {
        return try {
            firestore.collection("reviews").add(review).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getReviewsForLocation(lokasi: String): Flow<List<Review>> = callbackFlow {
        val listener = firestore.collection("reviews")
            .whereEqualTo("lokasiPkl", lokasi)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reviews = snapshot?.toObjects(Review::class.java) ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    // Voucher methods
    suspend fun getAvailableVouchers(): Result<List<Voucher>> {
        return try {
            val snapshot = firestore.collection("vouchers")
                .whereEqualTo("isActive", true)
                .get().await()
            val vouchers = snapshot.toObjects(Voucher::class.java)
            Result.success(vouchers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimVoucher(userId: String, voucherId: String): Result<Unit> {
        return try {
            val userVoucher = UserVoucher(
                userId = userId,
                voucherId = voucherId
            )
            firestore.collection("userVouchers").add(userVoucher).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserVouchers(userId: String): Result<List<UserVoucher>> {
        return try {
            val snapshot = firestore.collection("userVouchers")
                .whereEqualTo("userId", userId)
                .get().await()
            val userVouchers = snapshot.toObjects(UserVoucher::class.java)
            Result.success(userVouchers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crowd meter methods
    suspend fun updateCrowdLevel(lokasi: String, level: CrowdLevel, userId: String): Result<Unit> {
        return try {
            val crowdMeter = CrowdMeter(
                lokasiPkl = lokasi,
                level = level,
                updatedBy = userId
            )
            firestore.collection("crowdMeters").document(lokasi).set(crowdMeter).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCrowdLevel(lokasi: String): Flow<CrowdMeter?> = callbackFlow {
        val listener = firestore.collection("crowdMeters").document(lokasi)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val crowdMeter = snapshot?.toObject(CrowdMeter::class.java)
                trySend(crowdMeter)
            }
        awaitClose { listener.remove() }
    }
}