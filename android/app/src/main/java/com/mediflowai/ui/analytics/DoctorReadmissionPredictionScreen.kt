package com.mediflowai.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediflowai.data.model.ReadmissionPredictionRequest
import com.mediflowai.viewmodel.DoctorReadmissionPredictionViewModel
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.items


private val PredictionNavy =
    Color(0xFF081225)

private val PredictionTeal =
    Color(0xFF00A6A6)

private val PredictionMuted =
    Color(0xFF64748B)

private val PredictionBackground =
    Color(0xFFF5F8FB)

private val PredictionGreen =
    Color(0xFF16A34A)

private val PredictionAmber =
    Color(0xFFD97706)

private val PredictionRed =
    Color(0xFFDC2626)


@Composable
fun DoctorReadmissionPredictionScreen(
    patientId: Int,
    onBack: () -> Unit,
    viewModel:
    DoctorReadmissionPredictionViewModel =
        viewModel()
) {

    val state by
    viewModel.uiState.collectAsState()


    var losDays by remember {
        mutableStateOf("")
    }

    var admitType by remember {
        mutableStateOf("")
    }

    var wardType by remember {
        mutableStateOf("")
    }

    var numProcedures by remember {
        mutableStateOf("")
    }

    var charlsonIndex by remember {
        mutableStateOf("")
    }

    var hba1c by remember {
        mutableStateOf("")
    }

    var creatinine by remember {
        mutableStateOf("")
    }

    var haemoglobin by remember {
        mutableStateOf("")
    }

    var systolicBp by remember {
        mutableStateOf("")
    }

    var age by remember {
        mutableStateOf("")
    }

    var gender by remember {
        mutableStateOf("")
    }

    var stateValue by remember {
        mutableStateOf("")
    }

    var bplCard by remember {
        mutableStateOf(false)
    }

    var insuranceType by remember {
        mutableStateOf("")
    }

    var comorbidityCount by remember {
        mutableStateOf("")
    }

    var prevAdmissions by remember {
        mutableStateOf("")
    }

    var hospitalState by remember {
        mutableStateOf("")
    }

    var tier by remember {
        mutableStateOf("")
    }

    var beds by remember {
        mutableStateOf("")
    }

    var teaching by remember {
        mutableStateOf(false)
    }

    var diagnosisCount by remember {
        mutableStateOf("")
    }

    var primaryDiagnosis by remember {
        mutableStateOf("")
    }

    var admissionMonth by remember {
        mutableStateOf("")
    }

    var validationError by remember {
        mutableStateOf<String?>(null)
    }


    LaunchedEffect(
        patientId
    ) {

        viewModel.load(
            patientId
        )
    }


    LaunchedEffect(
        state.patient
    ) {

        val patient =
            state.patient

        if (
            patient != null &&
            gender.isBlank()
        ) {

            gender =
                patient.gender ?: ""
        }
    }


    Scaffold(
        containerColor =
            PredictionBackground
    ) { paddingValues ->


        when {

            state.isLoading -> {

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }


            else -> {

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                paddingValues
                            ),

                    contentPadding =
                        androidx.compose.foundation.layout
                            .PaddingValues(
                                20.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {


                    item {

                        Button(
                            onClick =
                                onBack
                        ) {

                            Text(
                                "Back"
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "Readmission Risk Prediction",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                PredictionNavy
                        )


                        Text(
                            modifier =
                                Modifier.padding(
                                    top = 4.dp
                                ),
                            text =
                                state.patient?.name
                                    ?: "Assigned Patient",
                            color =
                                PredictionMuted
                        )
                    }


                    state.result?.let {
                            result ->

                        item {

                            PredictionResultCard(
                                probability =
                                    result
                                        .readmission_probability,

                                riskCategory =
                                    result
                                        .risk_category,

                                modelVersion =
                                    result
                                        .model_version
                            )
                        }
                    }


                    item {

                        SectionHeader(
                            "Admission Details"
                        )
                    }


                    item {

                        IntegerField(
                            value = losDays,
                            onValueChange = {
                                losDays = it
                            },
                            label =
                                "Length of Stay (days)"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                admitType,
                            onValueChange = {
                                admitType = it
                            },
                            label =
                                "Admission Type"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                wardType,
                            onValueChange = {
                                wardType = it
                            },
                            label =
                                "Ward Type"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                numProcedures,
                            onValueChange = {
                                numProcedures = it
                            },
                            label =
                                "Number of Procedures"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                admissionMonth,
                            onValueChange = {
                                admissionMonth = it
                            },
                            label =
                                "Admission Month (1-12)"
                        )
                    }


                    item {

                        SectionHeader(
                            "Clinical Measurements"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                charlsonIndex,
                            onValueChange = {
                                charlsonIndex = it
                            },
                            label =
                                "Charlson Index"
                        )
                    }


                    item {

                        DecimalField(
                            value =
                                hba1c,
                            onValueChange = {
                                hba1c = it
                            },
                            label =
                                "HbA1c"
                        )
                    }


                    item {

                        DecimalField(
                            value =
                                creatinine,
                            onValueChange = {
                                creatinine = it
                            },
                            label =
                                "Creatinine"
                        )
                    }


                    item {

                        DecimalField(
                            value =
                                haemoglobin,
                            onValueChange = {
                                haemoglobin = it
                            },
                            label =
                                "Haemoglobin"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                systolicBp,
                            onValueChange = {
                                systolicBp = it
                            },
                            label =
                                "Systolic BP"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                comorbidityCount,
                            onValueChange = {
                                comorbidityCount = it
                            },
                            label =
                                "Comorbidity Count"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                prevAdmissions,
                            onValueChange = {
                                prevAdmissions = it
                            },
                            label =
                                "Previous Admissions"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                diagnosisCount,
                            onValueChange = {
                                diagnosisCount = it
                            },
                            label =
                                "Diagnosis Count"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                primaryDiagnosis,
                            onValueChange = {
                                primaryDiagnosis = it
                            },
                            label =
                                "Primary Diagnosis Category"
                        )
                    }


                    item {

                        SectionHeader(
                            "Patient Information"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                age,
                            onValueChange = {
                                age = it
                            },
                            label =
                                "Age"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                gender,
                            onValueChange = {
                                gender = it
                            },
                            label =
                                "Gender"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                stateValue,
                            onValueChange = {
                                stateValue = it
                            },
                            label =
                                "Patient State"
                        )
                    }


                    item {

                        BooleanField(
                            label =
                                "BPL Card",
                            checked =
                                bplCard,
                            onCheckedChange = {
                                bplCard = it
                            }
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                insuranceType,
                            onValueChange = {
                                insuranceType = it
                            },
                            label =
                                "Insurance Type (optional)"
                        )
                    }


                    item {

                        SectionHeader(
                            "Hospital Information"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                hospitalState,
                            onValueChange = {
                                hospitalState = it
                            },
                            label =
                                "Hospital State"
                        )
                    }


                    item {

                        TextFieldItem(
                            value =
                                tier,
                            onValueChange = {
                                tier = it
                            },
                            label =
                                "Hospital Tier"
                        )
                    }


                    item {

                        IntegerField(
                            value =
                                beds,
                            onValueChange = {
                                beds = it
                            },
                            label =
                                "Hospital Beds"
                        )
                    }


                    item {

                        BooleanField(
                            label =
                                "Teaching Hospital",
                            checked =
                                teaching,
                            onCheckedChange = {
                                teaching = it
                            }
                        )
                    }


                    validationError?.let {
                            message ->

                        item {

                            Text(
                                text =
                                    message,
                                color =
                                    PredictionRed,
                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }


                    state.error?.let {
                            message ->

                        item {

                            Text(
                                text =
                                    message,
                                color =
                                    PredictionRed
                            )
                        }
                    }


                    item {

                        Button(
                            modifier =
                                Modifier.fillMaxWidth(),

                            enabled =
                                !state.isPredicting,

                            onClick = {

                                val request =
                                    buildPredictionRequest(
                                        losDays =
                                            losDays,
                                        admitType =
                                            admitType,
                                        wardType =
                                            wardType,
                                        numProcedures =
                                            numProcedures,
                                        charlsonIndex =
                                            charlsonIndex,
                                        hba1c =
                                            hba1c,
                                        creatinine =
                                            creatinine,
                                        haemoglobin =
                                            haemoglobin,
                                        systolicBp =
                                            systolicBp,
                                        age =
                                            age,
                                        gender =
                                            gender,
                                        stateValue =
                                            stateValue,
                                        bplCard =
                                            bplCard,
                                        insuranceType =
                                            insuranceType,
                                        comorbidityCount =
                                            comorbidityCount,
                                        prevAdmissions =
                                            prevAdmissions,
                                        hospitalState =
                                            hospitalState,
                                        tier =
                                            tier,
                                        beds =
                                            beds,
                                        teaching =
                                            teaching,
                                        diagnosisCount =
                                            diagnosisCount,
                                        primaryDiagnosis =
                                            primaryDiagnosis,
                                        admissionMonth =
                                            admissionMonth
                                    )


                                if (request == null) {

                                    validationError =
                                        "Please complete all required fields with valid values."

                                } else {

                                    validationError =
                                        null

                                    viewModel.predict(
                                        patientId =
                                            patientId,
                                        request =
                                            request
                                    )
                                }
                            }
                        ) {

                            Text(
                                text =
                                    if (
                                        state.isPredicting
                                    ) {
                                        "Predicting..."
                                    } else {
                                        "Generate Readmission Prediction"
                                    }
                            )
                        }
                    }


                    if (
                        state.history.isNotEmpty()
                    ) {

                        item {

                            SectionHeader(
                                "Prediction History"
                            )
                        }


                        items(
                            items =
                                state.history,
                            key = {
                                it.id
                            }
                        ) { prediction ->


                            HistoryCard(
                                riskScore =
                                    prediction.risk_score,
                                riskLevel =
                                    prediction.risk_level,
                                modelVersion =
                                    prediction.model_version,
                                createdAt =
                                    prediction.created_at
                            )
                        }
                    }


                    item {

                        Card(
                            modifier =
                                Modifier.fillMaxWidth(),
                            shape =
                                RoundedCornerShape(
                                    18.dp
                                ),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        PredictionTeal.copy(
                                            alpha =
                                                0.08f
                                        )
                                )
                        ) {

                            Text(
                                modifier =
                                    Modifier.padding(
                                        16.dp
                                    ),
                                text =
                                    "This application is an academic prototype " +
                                            "and is not intended for clinical diagnosis " +
                                            "or medical decision-making.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    PredictionMuted
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SectionHeader(
    title: String
) {

    Text(
        text =
            title,
        style =
            MaterialTheme
                .typography
                .titleLarge,
        fontWeight =
            FontWeight.Bold,
        color =
            PredictionNavy
    )
}


@Composable
private fun TextFieldItem(
    value: String,
    onValueChange:
        (String) -> Unit,
    label: String
) {

    OutlinedTextField(
        modifier =
            Modifier.fillMaxWidth(),
        value =
            value,
        onValueChange =
            onValueChange,
        label = {
            Text(label)
        },
        singleLine =
            true
    )
}


@Composable
private fun IntegerField(
    value: String,
    onValueChange:
        (String) -> Unit,
    label: String
) {

    OutlinedTextField(
        modifier =
            Modifier.fillMaxWidth(),
        value =
            value,
        onValueChange = {

            if (
                it.all {
                        character ->
                    character.isDigit()
                }
            ) {
                onValueChange(it)
            }
        },
        label = {
            Text(label)
        },
        singleLine =
            true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Number
            )
    )
}


@Composable
private fun DecimalField(
    value: String,
    onValueChange:
        (String) -> Unit,
    label: String
) {

    OutlinedTextField(
        modifier =
            Modifier.fillMaxWidth(),
        value =
            value,
        onValueChange =
            onValueChange,
        label = {
            Text(label)
        },
        singleLine =
            true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType =
                    KeyboardType.Decimal
            )
    )
}


@Composable
private fun BooleanField(
    label: String,
    checked: Boolean,
    onCheckedChange:
        (Boolean) -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Checkbox(
            checked =
                checked,
            onCheckedChange =
                onCheckedChange
        )


        Text(
            text =
                label,
            color =
                PredictionNavy
        )
    }
}


@Composable
private fun PredictionResultCard(
    probability: Double,
    riskCategory: String,
    modelVersion: String
) {

    val riskColor =
        when (
            riskCategory.uppercase()
        ) {

            "LOW" ->
                PredictionGreen

            "MEDIUM" ->
                PredictionAmber

            "HIGH" ->
                PredictionRed

            else ->
                PredictionMuted
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    20.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )
        ) {

            Text(
                text =
                    "Prediction Result",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.Bold,
                color =
                    PredictionNavy
            )


            Text(
                text =
                    riskCategory.uppercase(),
                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    riskColor
            )


            Text(
                text =
                    "Readmission probability: " +
                            "${(probability * 100).roundToInt()}%",
                color =
                    PredictionNavy
            )


            Text(
                text =
                    "Model version: $modelVersion",
                color =
                    PredictionMuted
            )
        }
    }
}


@Composable
private fun HistoryCard(
    riskScore: Double,
    riskLevel: String,
    modelVersion: String?,
    createdAt: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                18.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    4.dp
                )
        ) {

            Text(
                text =
                    riskLevel.uppercase(),
                fontWeight =
                    FontWeight.Bold,
                color =
                    PredictionNavy
            )


            Text(
                text =
                    "Probability: " +
                            "${(riskScore * 100).roundToInt()}%",
                color =
                    PredictionMuted
            )


            Text(
                text =
                    "Model: " +
                            (modelVersion ?: "Unknown"),
                color =
                    PredictionMuted
            )


            Text(
                text =
                    createdAt,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    PredictionMuted
            )
        }
    }
}


private fun buildPredictionRequest(

    losDays: String,
    admitType: String,
    wardType: String,
    numProcedures: String,
    charlsonIndex: String,
    hba1c: String,
    creatinine: String,
    haemoglobin: String,
    systolicBp: String,
    age: String,
    gender: String,
    stateValue: String,
    bplCard: Boolean,
    insuranceType: String,
    comorbidityCount: String,
    prevAdmissions: String,
    hospitalState: String,
    tier: String,
    beds: String,
    teaching: Boolean,
    diagnosisCount: String,
    primaryDiagnosis: String,
    admissionMonth: String

): ReadmissionPredictionRequest? {


    val parsedLos =
        losDays.toIntOrNull()
            ?: return null

    val parsedProcedures =
        numProcedures.toIntOrNull()
            ?: return null

    val parsedCharlson =
        charlsonIndex.toIntOrNull()
            ?: return null

    val parsedHba1c =
        hba1c.toDoubleOrNull()
            ?: return null

    val parsedCreatinine =
        creatinine.toDoubleOrNull()
            ?: return null

    val parsedHaemoglobin =
        haemoglobin.toDoubleOrNull()
            ?: return null

    val parsedBp =
        systolicBp.toIntOrNull()
            ?: return null

    val parsedAge =
        age.toIntOrNull()
            ?: return null

    val parsedComorbidities =
        comorbidityCount.toIntOrNull()
            ?: return null

    val parsedAdmissions =
        prevAdmissions.toIntOrNull()
            ?: return null

    val parsedBeds =
        beds.toIntOrNull()
            ?: return null

    val parsedDiagnosisCount =
        diagnosisCount.toIntOrNull()
            ?: return null

    val parsedMonth =
        admissionMonth.toIntOrNull()
            ?: return null


    if (
        admitType.isBlank() ||
        wardType.isBlank() ||
        gender.isBlank() ||
        stateValue.isBlank() ||
        hospitalState.isBlank() ||
        tier.isBlank() ||
        primaryDiagnosis.isBlank()
    ) {
        return null
    }


    if (
        parsedLos !in 1..365 ||
        parsedBp !in 50..300 ||
        parsedAge !in 0..120 ||
        parsedMonth !in 1..12 ||
        parsedBeds < 1 ||
        parsedProcedures < 0 ||
        parsedCharlson < 0 ||
        parsedHba1c < 0 ||
        parsedCreatinine <= 0 ||
        parsedHaemoglobin <= 0 ||
        parsedComorbidities < 0 ||
        parsedAdmissions < 0 ||
        parsedDiagnosisCount < 1
    ) {
        return null
    }


    return ReadmissionPredictionRequest(

        los_days =
            parsedLos,

        admit_type =
            admitType.trim(),

        ward_type =
            wardType.trim(),

        num_procedures =
            parsedProcedures,

        charlson_index =
            parsedCharlson,

        hba1c =
            parsedHba1c,

        creatinine =
            parsedCreatinine,

        haemoglobin =
            parsedHaemoglobin,

        systolic_bp =
            parsedBp,

        age =
            parsedAge,

        gender =
            gender.trim(),

        state =
            stateValue.trim(),

        bpl_card =
            bplCard,

        insurance_type =
            insuranceType
                .trim()
                .takeIf {
                    it.isNotBlank()
                },

        comorbidity_count =
            parsedComorbidities,

        prev_admissions =
            parsedAdmissions,

        hospital_state =
            hospitalState.trim(),

        tier =
            tier.trim(),

        beds =
            parsedBeds,

        teaching =
            teaching,

        diagnosis_count =
            parsedDiagnosisCount,

        primary_diagnosis_category =
            primaryDiagnosis.trim(),

        admission_month =
            parsedMonth
    )
}