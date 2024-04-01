package com.example.notebook.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.notebook.MainActivity;
import com.example.notebook.R;
import com.example.notebook.databinding.FragmentHomeBinding;
import com.example.notebook.models.Contact;
import com.example.notebook.room.AppDatabase;
import com.example.notebook.room.ContactDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AppDatabase appDatabase;
    private ContactDao contactDao;
    private Bitmap bitmapImage;
    private ActivityResultLauncher<String> contentL;
    private boolean isImgSelected=false;
    NavController navController;


    public View onCreateView(@NonNull LayoutInflater inflater,
                                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnLoadPhoto.setOnClickListener(v1 -> {
            HomeFragment.this.contentL.launch("image/*");
        });
        contentL = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        try {
                            bitmapImage = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getContext().getContentResolver(),result);
                            isImgSelected=true;
                        }catch (IOException error){
                            error.printStackTrace();
                            isImgSelected=false;
                        }
                    }
                });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSave.setOnClickListener(v2 -> {
            String surname = binding.editSurname.getText().toString();
            String name = binding.editName.getText().toString();
            String phone = binding.editPhone.getText().toString();

            if(surname.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireActivity(),"Заполните все поля",Toast.LENGTH_LONG).show();
                isImgSelected=false;
            } else {
                if(bitmapImage != null){
                    ByteArrayOutputStream baos_imageStudent = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG,100,baos_imageStudent);

                    byte[] image = baos_imageStudent.toByteArray();

                    Contact student = new Contact(surname, name, phone, image);
                    this.appDatabase = Room.databaseBuilder(binding.getRoot().getContext(),
                            AppDatabase.class,"database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
                    contactDao = appDatabase.contactDao();
                    contactDao.insert(student);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    navController = Navigation.findNavController(requireActivity(), R.id.nav_host);
                    navController.navigate(R.id.action_navigation_home_to_navigation_dashboard);
                } else {
                    Toast.makeText(requireActivity(),"Загрузите фото",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}