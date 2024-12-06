package ma.ensa.soap.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ma.ensa.soap.R;
import ma.ensa.soap.beans.Compte;

public class CompteAdapter extends RecyclerView.Adapter<CompteAdapter.CompteViewHolder> {

    private final List<Compte> comptes = new ArrayList<>();
    private OnEditClickListener onEditClick;
    private OnDeleteClickListener onDeleteClick;

    public interface OnEditClickListener {
        void onEdit(Compte compte);
    }

    public interface OnDeleteClickListener {
        void onDelete(Compte compte);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClick = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClick = listener;
    }

    public void updateComptes(List<Compte> newComptes) {
        comptes.clear();
        comptes.addAll(newComptes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compte, parent, false);
        return new CompteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompteViewHolder holder, int position) {
        holder.bind(comptes.get(position));
    }

    @Override
    public int getItemCount() {
        return comptes.size();
    }

    public void removeCompte(Compte compte) {
        int position = comptes.indexOf(compte);
        if (position >= 0) {
            comptes.remove(position);
            notifyItemRemoved(position);
        }
    }

    class CompteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvId;
        private final TextView tvSolde;
        private final Chip tvType;
        private final TextView tvDate;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public CompteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvSolde = itemView.findViewById(R.id.tvSolde);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Compte compte) {
            tvId.setText("Compte N° " + compte.getId());
            tvSolde.setText(compte.getSolde() + " DH");
            tvType.setText(compte.getType().name());
            tvDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(compte.getDateCreation()));

            btnEdit.setOnClickListener(v -> {
                if (onEditClick != null) {
                    onEditClick.onEdit(compte);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onDeleteClick != null) {
                    onDeleteClick.onDelete(compte);
                }
            });
        }
    }
}
