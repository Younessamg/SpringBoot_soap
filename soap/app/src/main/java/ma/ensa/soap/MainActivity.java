package ma.ensa.soap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ma.ensa.soap.adapter.CompteAdapter;
import ma.ensa.soap.beans.Compte;
import ma.ensa.soap.beans.TypeCompte;
import ma.ensa.soap.service.SoapService;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private final CompteAdapter adapter = new CompteAdapter();
    private final SoapService soapService = new SoapService();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadComptes();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up delete listener
        adapter.setOnDeleteClickListener(compte ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Supprimer le compte")
                        .setMessage("Voulez-vous vraiment supprimer ce compte ?")
                        .setPositiveButton("Supprimer", (dialog, which) ->
                                executorService.execute(() -> {
                                    boolean success = soapService.deleteCompte(compte.getId());
                                    runOnUiThread(() -> {
                                        if (success) {
                                            adapter.removeCompte(compte);
                                            Toast.makeText(this, "Compte supprimé", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Échec de la suppression", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                })
                        )
                        .setNegativeButton("Annuler", null)
                        .show()
        );
    }

    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddCompteDialog());
    }

    private void showAddCompteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialoge_add_compte, null);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Nouveau compte")
                .setPositiveButton("Ajouter", (dialog, which) -> {
                    TextInputEditText etSolde = dialogView.findViewById(R.id.etSolde);
                    RadioButton radioCourant = dialogView.findViewById(R.id.radioCourant);

                    double solde = etSolde.getText() != null ?
                            Double.parseDouble(etSolde.getText().toString()) : 0.0;
                    TypeCompte type = radioCourant.isChecked() ? TypeCompte.COURANT : TypeCompte.EPARGNE;

                    executorService.execute(() -> {
                        boolean success = soapService.createCompte(solde, type);
                        runOnUiThread(() -> {
                            if (success) {
                                loadComptes();
                                Toast.makeText(this, "Compte ajouté", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Échec de l'ajout", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void loadComptes() {
        executorService.execute(() -> {
            try {
                java.util.List<Compte> comptes = soapService.getComptes();
                runOnUiThread(() -> {
                    if (!comptes.isEmpty()) {
                        adapter.updateComptes(comptes);
                    } else {
                        Toast.makeText(this, "Aucun compte trouvé", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}