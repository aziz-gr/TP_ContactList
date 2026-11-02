package com.example.contactlist

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.contactlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contactList = mutableListOf<Contact>()
    private lateinit var adapter: ContactAdapter

    // Activity Result Launcher to receive edited contact data back from DetailActivity
    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data ?: return@registerForActivityResult
            val position = data.getIntExtra(DetailActivity.EXTRA_POSITION, -1)
            val updatedContact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getParcelableExtra(DetailActivity.EXTRA_CONTACT, Contact::class.java)
            } else {
                @Suppress("DEPRECATION")
                data.getParcelableExtra(DetailActivity.EXTRA_CONTACT)
            }

            if (position in contactList.indices && updatedContact != null) {
                contactList[position] = updatedContact
                adapter.notifyDataSetChanged()
                updateUIState()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSampleContacts()
        setupListView()
        updateUIState()
    }

    private fun initSampleContacts() {
        if (contactList.isNotEmpty()) return

        contactList.addAll(
            listOf(
                Contact(
                    id = "1",
                    nom = "Dupont",
                    prenom = "Jean",
                    telephone = "+33 6 12 34 56 78",
                    email = "jean.dupont@email.com",
                    photoResId = R.drawable.ic_avatar_1
                ),
                Contact(
                    id = "2",
                    nom = "Curie",
                    prenom = "Marie",
                    telephone = "+33 6 98 76 54 32",
                    email = "marie.curie@science.fr",
                    photoResId = R.drawable.ic_avatar_2
                ),
                Contact(
                    id = "3",
                    nom = "Pesquet",
                    prenom = "Thomas",
                    telephone = "+33 6 11 22 33 44",
                    email = "thomas.pesquet@esa.int",
                    photoResId = R.drawable.ic_avatar_3
                ),
                Contact(
                    id = "4",
                    nom = "Martin",
                    prenom = "Sophie",
                    telephone = "+33 7 44 55 66 77",
                    email = "sophie.martin@tech.org",
                    photoResId = R.drawable.ic_avatar_4
                ),
                Contact(
                    id = "5",
                    nom = "Bernard",
                    prenom = "Lucas",
                    telephone = "+33 6 55 44 33 22",
                    email = "lucas.bernard@work.com",
                    photoResId = R.drawable.ic_avatar_5
                )
            )
        )
    }

    private fun setupListView() {
        adapter = ContactAdapter(this, contactList)
        binding.listViewContacts.adapter = adapter

        // Click listener to open DetailActivity via Explicit Intent
        binding.listViewContacts.setOnItemClickListener { _, _, position, _ ->
            val selectedContact = contactList[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_CONTACT, selectedContact)
                putExtra(DetailActivity.EXTRA_POSITION, position)
            }
            detailLauncher.launch(intent)
        }
    }

    private fun updateUIState() {
        val count = contactList.size
        binding.tvContactCount.text = getString(R.string.subtitle_contact_count, count)

        if (count == 0) {
            binding.listViewContacts.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.listViewContacts.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }
}