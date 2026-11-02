package com.example.contactlist

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.contactlist.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CONTACT = "extra_contact"
        const val EXTRA_POSITION = "extra_position"
    }

    private lateinit var binding: ActivityDetailBinding
    private var contact: Contact? = null
    private var position: Int = -1

    private var currentPhotoUri: String? = null
    private var tempCameraUri: Uri? = null
    private var tempCameraFilePath: String? = null

    // Gallery Picker Launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUtils.saveImageToInternalStorage(this, uri)
            if (savedPath != null) {
                currentPhotoUri = savedPath
                updatePhotoDisplay()
                Toast.makeText(this, R.string.msg_photo_updated, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.msg_photo_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Capture Launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraFilePath != null) {
            currentPhotoUri = tempCameraFilePath
            updatePhotoDisplay()
            Toast.makeText(this, R.string.msg_photo_updated, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.msg_photo_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    // Permission Launcher for Camera
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, R.string.error_camera_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarDetail.setNavigationOnClickListener {
            cancelAndExit()
        }

        // Retrieve Contact data from Intent
        position = intent.getIntExtra(EXTRA_POSITION, -1)
        contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CONTACT, Contact::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CONTACT)
        }

        if (contact == null) {
            Toast.makeText(this, "Erreur : Contact introuvable", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentPhotoUri = contact?.photoUri

        populateFields()
        setupListeners()
    }

    private fun populateFields() {
        val c = contact ?: return
        binding.etNom.setText(c.nom)
        binding.etPrenom.setText(c.prenom)
        binding.etTelephone.setText(c.telephone)
        binding.etEmail.setText(c.email)

        updatePhotoDisplay()
    }

    private fun updatePhotoDisplay() {
        val c = contact
        ImageUtils.loadContactPhoto(
            context = this,
            photoUriStr = currentPhotoUri,
            photoResId = c?.photoResId,
            imageView = binding.ivDetailPhoto
        )
    }

    private fun setupListeners() {
        // Photo Pickers
        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        // Implicit Intent Actions
        binding.btnCall.setOnClickListener {
            makePhoneCall()
        }

        binding.btnEmail.setOnClickListener {
            sendEmail()
        }

        // Form Actions
        binding.btnSave.setOnClickListener {
            saveContact()
        }

        binding.btnCancel.setOnClickListener {
            cancelAndExit()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_SHORT).show()
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            val (contentUri, filePath) = ImageUtils.createTempCameraUri(this)
            tempCameraUri = contentUri
            tempCameraFilePath = filePath
            cameraLauncher.launch(contentUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall() {
        val phone = binding.etTelephone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.inputLayoutTelephone.error = getString(R.string.error_field_required)
            Toast.makeText(this, R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }
        binding.inputLayoutTelephone.error = null

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_dialer_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.inputLayoutEmail.error = getString(R.string.error_field_required)
            Toast.makeText(this, R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }
        binding.inputLayoutEmail.error = null

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_email_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContact() {
        val nom = binding.etNom.text.toString().trim()
        val prenom = binding.etPrenom.text.toString().trim()
        val telephone = binding.etTelephone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        var isValid = true

        if (nom.isEmpty()) {
            binding.inputLayoutNom.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutNom.error = null
        }

        if (prenom.isEmpty()) {
            binding.inputLayoutPrenom.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutPrenom.error = null
        }

        if (telephone.isEmpty()) {
            binding.inputLayoutTelephone.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutTelephone.error = null
        }

        if (!isValid) {
            Toast.makeText(this, R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }

        val c = contact ?: return
        c.nom = nom
        c.prenom = prenom
        c.telephone = telephone
        c.email = email
        c.photoUri = currentPhotoUri

        val resultIntent = Intent().apply {
            putExtra(EXTRA_CONTACT, c)
            putExtra(EXTRA_POSITION, position)
        }

        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, R.string.msg_contact_updated, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun cancelAndExit() {
        setResult(RESULT_CANCELED)
        Toast.makeText(this, R.string.msg_changes_cancelled, Toast.LENGTH_SHORT).show()
        finish()
    }
}