package com.example.pethome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class InicioActivity : AppCompatActivity() {

    private lateinit var dlayMenu: DrawerLayout
    private lateinit var nvMenu: NavigationView
    private lateinit var ivMenu: ImageView
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //  CONFIGURAR GOOGLE SIGN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Drawer views
        dlayMenu = findViewById(R.id.dlayMenu)
        nvMenu = findViewById(R.id.nvMenu)
        ivMenu = findViewById(R.id.ivMenu)

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(InicioFragment())
            nvMenu.setCheckedItem(R.id.itInicio)
        }

        // Abrir drawer
        ivMenu.setOnClickListener {
            dlayMenu.openDrawer(GravityCompat.START)
        }

        // Click en menú
        nvMenu.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            dlayMenu.closeDrawer(GravityCompat.START)

            when (item.itemId) {
                R.id.itInicio -> replaceFragment(InicioFragment())
                R.id.itHistorial -> replaceFragment(HistorialFragment())
                R.id.itPerfil -> replaceFragment(PerfilFragment())
                R.id.itSeguimiento -> replaceFragment(SeguimientoFragment())
                R.id.itMascotas -> replaceFragment(MascotasFragment())

                R.id.itCerrarSesion -> {
                    //  Cerrar Google + Firebase
                    googleSignInClient.signOut().addOnCompleteListener {

                        FirebaseAuth.getInstance().signOut()

                        val intent = Intent(this, WelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
            true
        }

        // Notificaciones + WorkManager
        pedirPermisoNotificaciones()
        programarRecordatorios()
        manejarIntentDeNotificacion()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val ok = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!ok) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    200
                )
            }
        }
    }

    private fun programarRecordatorios() {
        val work = PeriodicWorkRequestBuilder<RecordatorioWorker>(15, TimeUnit.MINUTES).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recordatorio_pethome",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    private fun manejarIntentDeNotificacion() {
        val open = intent.getStringExtra("open_fragment")
        if (open == "seguimiento") {
            replaceFragment(SeguimientoFragment())
            nvMenu.setCheckedItem(R.id.itSeguimiento)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        manejarIntentDeNotificacion()
    }

    override fun onBackPressed() {
        if (::dlayMenu.isInitialized && dlayMenu.isDrawerOpen(GravityCompat.START)) {
            dlayMenu.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun navegarA(itemId: Int) {
        nvMenu.setCheckedItem(itemId)
        dlayMenu.closeDrawer(GravityCompat.START)
        when (itemId) {
            R.id.itMascotas -> replaceFragment(MascotasFragment())
            R.id.itHistorial -> replaceFragment(HistorialFragment())
            R.id.itSeguimiento -> replaceFragment(SeguimientoFragment())

        }
    }
}