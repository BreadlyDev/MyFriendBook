package com.example.notebook.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.notebook.R;
import com.example.notebook.databinding.ItemCardBinding;
import com.example.notebook.models.Contact;
import com.example.notebook.room.AppDatabase;
import com.example.notebook.room.ContactDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    List<Contact> list = new ArrayList<>();
    ContactDao contactDao;
    Context context;
    Contact newContact;

    public ContactAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<Contact> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding itemCardBinding = ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemCardBinding);
    }

    @SuppressLint({"SetTextI18n", "IntentReset", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        contactDao = Room.databaseBuilder(holder.binding.getRoot().getContext(), AppDatabase.class,"database").allowMainThreadQueries().build().contactDao();
        Contact contact = list.get(position);

        holder.binding.fullnameView.setText(contact.getSurname() + " " + contact.getName());
        holder.binding.phoneView.setText(contact.getPhone());
        holder.binding.imageView.setImageBitmap(BitmapFactory.decodeByteArray(contact.getImage(), 0, contact.getImage().length));

        newContact = contact;

        holder.binding.dropdownMenu.setOnClickListener(v1 -> {
            PopupMenu popup = new PopupMenu(holder.binding.getRoot().getContext(), holder.binding.dropdownMenu);

            popup.getMenuInflater().inflate(R.menu.card_menu,popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (Objects.requireNonNull(item.getTitle()).toString()){
                    case "Позвонить":

                        if (ContextCompat.checkSelfPermission(
                            holder.binding.getRoot().getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) holder.binding.getRoot().getContext(),
                                    new String[]{Manifest.permission.CALL_PHONE},0);
                        } else {
                            String phone = holder.binding.phoneView.getText().toString().trim();
                            Uri call = Uri.parse("tel:" + phone);
                            Intent intent = new Intent(Intent.ACTION_DIAL,call);
                            holder.binding.getRoot().getContext().startActivity(intent);
                        }
                        break;

                    case "Отправить сообщение":

                        holder.binding.myMessage.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(Intent.ACTION_SEND);

                        intent.setData(Uri.parse("mailto:"));
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "My message");
                        intent.putExtra(Intent.EXTRA_TEXT, holder.binding.myMessage.getText().toString());

                        try {
                            holder.itemView.getContext().startActivity(Intent.createChooser(intent,"Send mail..."));
                        } catch (android.content.ActivityNotFoundException e) {
                            Toast.makeText(holder.itemView.getContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Удалить контакт":

                        AlertDialog alertDialog = new AlertDialog.Builder(holder.binding.getRoot().getContext()).create();

                        alertDialog.setTitle("Вы уеверены что хотите удалить контакт?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да",
                                (dialog, which) -> {
                                    contactDao.delete(contact);
                                    list.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет",
                                (dialog, which) -> dialog.dismiss());

                        alertDialog.show();
                        break;

                    default:
                        Toast.makeText(context,"Ок",  Toast.LENGTH_LONG).show();
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCardBinding binding;
        public ViewHolder(@NonNull ItemCardBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
