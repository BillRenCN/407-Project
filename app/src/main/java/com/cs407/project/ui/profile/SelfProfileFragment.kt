package com.cs407.project.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.project.LauncherActivity
import com.cs407.project.MainActivity
import com.cs407.project.R
import com.cs407.project.data.AppDatabase
import com.cs407.project.data.ReviewDatabase
import com.cs407.project.data.SharedPreferences
import com.cs407.project.data.UsersDatabase
import com.cs407.project.databinding.FragmentProfileBinding
import com.cs407.project.ui.listing.ListingFragment
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SelfProfileFragment(private val injectedProfileViewModel: ProfileViewModel? = null) : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userDB: UsersDatabase
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null
    private lateinit var appDB: AppDatabase
    private lateinit var reviewDB: ReviewDatabase

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        profileViewModel = injectedProfileViewModel
            ?: ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userDB = UsersDatabase.getDatabase(requireContext())
        appDB = AppDatabase.getDatabase(requireContext())
        reviewDB = ReviewDatabase.getDatabase(requireContext())
        val root: View = binding.root
        val sharedPrefs = SharedPreferences(requireContext())
        val username = sharedPrefs.getLogin().username.toString()

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.list,
                ListingFragment2::class.java,
                null
            )  // Pass the fragment instance here
            ?.setReorderingAllowed(true)
            ?.addToBackStack("loading listing fragment")
            ?.commit()

        view?.findViewById<Button>(R.id.listing_button)?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.list,
                    ListingFragment2::class.java,
                    null
                )  // Pass the fragment instance here
                ?.setReorderingAllowed(true)
                ?.addToBackStack("loading listing fragment")
                ?.commit()
        }

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    handleImageSelection(uri)
                } else {
                    Toast.makeText(requireContext(), "Image selection canceled", Toast.LENGTH_SHORT).show()
                }
            }

        val logoutButton = binding.buttonLogout
        logoutButton.setOnClickListener {
            SharedPreferences(requireContext()).saveLogin(null, null)
            val intent = Intent(requireContext(), LauncherActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val editProfile=binding.myButton
        editProfile.setOnClickListener {
            (requireActivity() as MainActivity).replaceProfileFragmentWithEditProfile()
        }

        val profilePic=binding.profilepic

        profilePic.setOnClickListener {
            openImagePicker()
            Toast.makeText(requireContext(), "Image clicked!", Toast.LENGTH_SHORT).show()
        }
        lifecycleScope.launch {
            val user=userDB.userDao().getUserFromUsername(username)
            val date = user?.date
            val urlString= user?.imageUrl
            val reviews=reviewDB.reviewDao().getReviewsByUser(username)
            if (reviews.isEmpty()){
                binding.rating.text="No Reviews"
            }
            else{
                val rating=reviews.map { it.iconResource }.average()*20
                val count=reviews.size
                binding.rating.text=rating.toString()+"% positive feedback ("+count.toString()+")"
            }



            Log.d("date", date.toString())
            binding.username.text = username
            binding.description.text = "Member since " + date?.let { convertUnixDateToString(it) }
            if (urlString!=null){
                val uri: Uri = Uri.parse(urlString)
                binding.profilepic.setImageURI(uri)
            }
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun convertUnixDateToString(unixDate: Long): String {
        // Create a Date object from the Unix timestamp
        val date = Date(unixDate * 1000)  // Unix timestamps are in seconds, so multiply by 1000 for milliseconds

        // Define the desired format: MM/dd/yyyy
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        // Return the formatted date string
        return dateFormat.format(date)
    }
    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun handleImageSelection(uri: Uri) {
        selectedImageUri = saveImageLocally(uri)?.let { Uri.fromFile(File(it)) }
        val sharedPrefs=SharedPreferences(requireContext())
        val username=sharedPrefs.getLogin().username.toString()
        lifecycleScope.launch{
            userDB.userDao().updateImageUrlByUsername(username, selectedImageUri.toString())
        }
        Log.d("image", selectedImageUri.toString())
        if (selectedImageUri != null) {
            binding.profilepic.setImageURI(selectedImageUri)
        } else {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageLocally(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val fileName = "user_image_${System.currentTimeMillis()}.jpg"
            val file = File(requireContext().filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath // Return the local file path
        } catch (e: Exception) {
            Log.e("AddListingActivity", "Failed to save image: ${e.message}")
            null
        }
    }
}