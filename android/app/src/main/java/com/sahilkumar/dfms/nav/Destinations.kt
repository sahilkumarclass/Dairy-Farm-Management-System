package com.sahilkumar.dfms.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.sahilkumar.dfms.R
import com.sahilkumar.dfms.model.Role

object Routes {
    const val LOGIN = "login"
    const val COW_DETAIL = "herd/{cowId}"
    fun cowDetail(cowId: String) = "herd/$cowId"
}

/** Top-level drawer destinations, gated by role exactly like the web admin. */
enum class TopDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val ownerOnly: Boolean,
) {
    DASHBOARD("dashboard", R.string.nav_dashboard, Icons.Filled.Dashboard, false),
    CUSTOMERS("customers", R.string.nav_customers, Icons.Filled.People, false),
    MILK("milk", R.string.nav_milk, Icons.Filled.WaterDrop, false),
    EXPENSES("expenses", R.string.nav_expenses, Icons.AutoMirrored.Filled.ReceiptLong, true),
    BILLS("bills", R.string.nav_bills, Icons.Filled.Description, true),
    HERD("herd", R.string.nav_herd, Icons.Filled.Pets, true),
    STAFF("staff", R.string.nav_staff, Icons.Filled.Badge, true);

    companion object {
        fun forRole(role: Role): List<TopDestination> =
            entries.filter { !it.ownerOnly || role == Role.OWNER }
    }
}
