package com.example.thikaeshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thikaeshop.data.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdminUiState {
    object Loading : AdminUiState()

    // For tab-based loading (old way)
    data class VerificationsSuccess(val verifications: List<PendingVerification>) : AdminUiState()
    data class LandmarksSuccess(val landmarks: List<PendingLandmark>) : AdminUiState()
    data class UsersSuccess(val users: List<AdminUser>) : AdminUiState()
    data class TransactionsSuccess(val transactions: List<AdminTransaction>) : AdminUiState()
    data class ProductsSuccess(val products: List<AdminProduct>) : AdminUiState()
    data class DisputesSuccess(val disputes: List<AdminDispute>) : AdminUiState()
    data class RidersSuccess(val riders: List<RiderApplication>) : AdminUiState()
    data class ReportsSuccess(val reports: List<ReportedItem>) : AdminUiState()
    data class DeliveriesSuccess(
        val pendingDeliveries: List<PendingDelivery>,
        val availableRiders: List<AvailableRider>,
        val assignedDeliveries: List<AssignedDelivery>
    ) : AdminUiState()
    data class StatsSuccess(
        val totalUsers: Int,
        val totalOrders: Int,
        val totalRevenue: Int,
        val pendingVerifications: Int,
        val pendingLandmarks: Int,
        val pendingDisputes: Int
    ) : AdminUiState()

    // NEW: For combined all-data loading (used by expandable sections)
    data class AllDataSuccess(
        val pendingVerifications: List<PendingVerification>,
        val pendingLandmarks: List<PendingLandmark>,
        val users: List<AdminUser>,
        val transactions: List<AdminTransaction>,
        val products: List<AdminProduct>,
        val disputes: List<AdminDispute>,
        val riderApplications: List<RiderApplication>,
        val reportedItems: List<ReportedItem>,
        val pendingDeliveries: List<PendingDelivery>,
        val availableRiders: List<AvailableRider>,
        val assignedDeliveries: List<AssignedDelivery>,
        val stats: StatsSuccess
    ) : AdminUiState()

    data class Error(val message: String) : AdminUiState()
}

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState

    // Load ALL data at once for the expandable sections view
    fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            delay(500) // Simulate network delay

            val stats = AdminUiState.StatsSuccess(
                totalUsers = 156,
                totalOrders = 342,
                totalRevenue = 45200,
                pendingVerifications = SampleAdminData.pendingVerifications.size,
                pendingLandmarks = SampleAdminData.pendingLandmarks.size,
                pendingDisputes = SampleAdminData.disputes.filter { it.status == "Pending" }.size
            )

            _uiState.value = AdminUiState.AllDataSuccess(
                pendingVerifications = SampleAdminData.pendingVerifications,
                pendingLandmarks = SampleAdminData.pendingLandmarks,
                users = SampleAdminData.users,
                transactions = SampleAdminData.transactions,
                products = SampleAdminData.products,
                disputes = SampleAdminData.disputes,
                riderApplications = SampleAdminData.riderApplications,
                reportedItems = SampleAdminData.reportedItems,
                pendingDeliveries = SampleAdminData.pendingDeliveries,
                availableRiders = SampleAdminData.availableRiders,
                assignedDeliveries = SampleAdminData.assignedDeliveries,
                stats = stats
            )
        }
    }

    // Individual functions for actions
    fun approveVerification(verificationId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData() // Refresh after action
        }
    }

    fun rejectVerification(verificationId: String, reason: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun approveLandmark(landmarkId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun rejectLandmark(landmarkId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun suspendUser(userId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun removeProduct(productId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun resolveDispute(disputeId: String, decision: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun approveRider(applicationId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun rejectRider(applicationId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun dismissReport(reportId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData()
        }
    }

    fun assignRiderToOrder(orderId: String, riderId: String) {
        viewModelScope.launch {
            delay(500)
            loadAllData() // Refresh after assignment
        }
    }
}