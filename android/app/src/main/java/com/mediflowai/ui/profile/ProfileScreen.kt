package com.mediflowai.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediflowai.navigation.MediFlowBottomBar
import com.mediflowai.navigation.Screen
import com.mediflowai.viewmodel.AuthViewModel

private val PageBackground = Color(0xFFF6F8FC)

private val Navy900 = Color(0xFF081225)
private val Navy800 = Color(0xFF10213F)
private val Navy700 = Color(0xFF16345F)

private val Teal = Color(0xFF00A6A6)
private val Cyan = Color(0xFF38BDF8)
private val Emerald = Color(0xFF10B981)
private val Red = Color(0xFFEF4444)

private val TextPrimary = Color(0xFF101828)
private val TextSecondary = Color(0xFF667085)
private val BorderColor = Color(0xFFE4EAF2)

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {

    val email =
        authViewModel.getCurrentUserEmail()
            ?: "Authenticated User"

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            MediFlowBottomBar(
                currentRoute = Screen.Profile.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PageBackground)
                .verticalScroll(rememberScrollState())
        ) {

            ProfileHeader(
                email = email
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Account",
                    subtitle = "Authentication and session information"
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileCard {

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Email",
                        value = email,
                        accent = Cyan
                    )

                    HorizontalDivider(
                        color = BorderColor
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.VerifiedUser,
                        label = "Authentication",
                        value = "Firebase",
                        accent = Teal
                    )

                    HorizontalDivider(
                        color = BorderColor
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Security,
                        label = "Session",
                        value = "Active",
                        accent = Emerald
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(
                    title = "Security",
                    subtitle = "Account protection and access"
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileCard {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    Teal.copy(alpha = 0.10f),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Teal
                            )
                        }

                        Spacer(modifier = Modifier.width(13.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Secure Authentication",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Email/password and Google Sign-In enabled",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(
                    title = "Application",
                    subtitle = "MediFlow AI project information"
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileCard {

                    ProfileInfoRow(
                        icon = Icons.Default.Badge,
                        label = "Application",
                        value = "MediFlow AI",
                        accent = Cyan
                    )

                    HorizontalDivider(
                        color = BorderColor
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Info,
                        label = "Tagline",
                        value = "Predict. Prioritize. Care.",
                        accent = Teal
                    )

                    HorizontalDivider(
                        color = BorderColor
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Security,
                        label = "Project Type",
                        value = "Academic Prototype",
                        accent = Navy700
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(
                    title = "About MediFlow AI",
                    subtitle = "Smart hospital decision support"
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileCard {

                    Text(
                        text =
                            "MediFlow AI combines hospital resource management, patient readmission risk prediction, appointment prioritization, doctor load balancing, and smart bed allocation in one system.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFFF7ED)
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(11.dp))

                        Column {

                            Text(
                                text = "Academic Disclaimer",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text =
                                    "This application is an academic prototype and is not intended for clinical diagnosis or medical decision-making.",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Red
                    )
                ) {

                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Logout",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    email: String
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Navy900,
                        Navy800,
                        Navy700
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 32.dp,
                    bottom = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(82.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.10f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Cyan,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "MediFlow AI User",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Emerald.copy(alpha = 0.14f)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                Emerald,
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "Authenticated",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accent: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    accent.copy(alpha = 0.10f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {

    Column {

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProfileCard(
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}