package com.example.notebook.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.notebook.databinding.FragmentDashboardBinding;
import com.example.notebook.room.AppDatabase;
import com.example.notebook.room.ContactDao;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    RecyclerView rv_note_book;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        rv_note_book = binding.rvNoteBook;
        ContactAdapter studentAdapter = new ContactAdapter(getContext());
        rv_note_book.setAdapter(studentAdapter);

        AppDatabase appDatabase = Room.databaseBuilder(binding.getRoot().getContext()
                        , AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        ContactDao contactDao = appDatabase.contactDao();
        studentAdapter.setList(contactDao.getAll());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}