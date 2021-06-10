package com.example.quotes.theme

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.fragmentViewModel
import com.example.quotes.FBAnalytics
import com.example.quotes.FileManager
import com.example.quotes.R
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.epoxy.simpleController
import com.example.quotes.epoxy.viewholders.ThemeViewHolder
import com.example.quotes.epoxy.viewholders.themeViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.StorageReference


class ThemesListFragment : MvRxListBaseFragment(R.layout.epoxy) {

    private val themeViewModel: ThemeViewModel by fragmentViewModel()

    private val themeListener = object : ThemeViewHolder.ThemeListener {
        override fun applyTheme(theme: StorageReference) {
            theme.downloadUrl.addOnSuccessListener { uri ->
                themeViewModel.onQuoteThemeSelected(uri)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.theme_selected_message),
                    Toast.LENGTH_SHORT
                ).show()
                val themeString = uri.toString()
                val themeId = themeString.substring(themeString.indexOf('_') + 1, themeString.lastIndexOf('_')).toLong()
                FBAnalytics.getSetContentAnalytics(themeId, getString(R.string.select_theme))
            }
        }
    }

    private val requestForPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val themeLongPress = object : ThemeViewHolder.ThemeLongPress {
        override fun createDialogWindow(
            theme: StorageReference,
            text: String,
            image: View,
            imageName: String
        ): Boolean {

            val shareImage = View.OnClickListener {
                checkPermission(requireContext())
                val bitmap = createBitmapFromView(-3434, text, image)
                val fileManager = FileManager()

                val uri = fileManager.saveImageWithMediaStore(requireContext(), bitmap, imageName)
                if (uri == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.share_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }

                val shareIntent = fileManager.getSharedImageIntent(uri)
                startActivity(Intent.createChooser(shareIntent, null))
            }

            val downloadImage = View.OnClickListener {
                checkPermission(requireContext())
                val bitmap = createBitmapFromView(-5455, text, image)
                FileManager().saveImageWithMediaStore(requireContext(), bitmap, imageName)
            }

            val dialog = Dialog(requireContext())
            dialog.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCancelable(true)
                setContentView(R.layout.item_dialog_window_floating_buttons)
                window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                findViewById<TextView>(R.id.dialog_text_view).text =
                    getString(R.string.theme_dialog_message)

                findViewById<FloatingActionButton>(R.id.first_button).apply {
                    setOnClickListener(shareImage)
                    setBackgroundResource(R.drawable.ic_share)
                }
                findViewById<FloatingActionButton>(R.id.second_button).apply {
                    setOnClickListener(downloadImage)
                    setBackgroundResource(R.drawable.ic_download)
                }
                show()
            }
            return true
        }
    }

    override fun epoxyController(): EpoxyController = simpleController(themeViewModel) { state ->
        val themes = state.themes.invoke()

        themes?.forEach { theme ->
            themeViewHolder {
                id(theme.hashCode())
                theme(theme)
                quote(getString(R.string.theme_quote_example))
                themeListener(themeListener)
                longPress(themeLongPress)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun createBitmapFromView(color: Int, text: String, view: View): Bitmap {
        val bitmap = view.drawToBitmap()
        val canvas = Canvas(bitmap)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

        val scale = resources.displayMetrics.density
        val textS = 14 * scale

        paint.apply {
            textSize = textS
            setColor(Color.BLACK)
            style = Paint.Style.FILL
        }

        canvas.apply {
            drawColor(color)
            val textWidth = width - textS
            val textLayout =
                StaticLayout(
                    text,
                    paint,
                    textWidth.toInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
                )
            val textHeight = textLayout.height.toFloat()

            val x: Float = (bitmap.width - textWidth) / 2
            val y: Float = (bitmap.height - textHeight) / 2

            save()
            translate(x, y)
            textLayout.draw(this)
            canvas.restore()
        }
        return bitmap
    }

    // TODO: 01.03.21 Correct the extension
    private fun FileManager.getSharedImageIntent(uri: Uri): Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/*"
    }

    private fun checkPermission(context: Context): Boolean {
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val isWritePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            writeStoragePermission
        ) == PackageManager.PERMISSION_GRANTED
        if (isWritePermissionGranted) {
            return true
        }
        requestForPermission.launch(writeStoragePermission)
        return false
    }
}
