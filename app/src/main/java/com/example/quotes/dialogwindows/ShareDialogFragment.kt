package com.example.quotes.dialogwindows

import android.app.WallpaperManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.quotes.FBAnalytics
import com.example.quotes.FileManager
import com.example.quotes.R
import com.example.quotes.databinding.ItemDialogWindowBinding
import com.example.quotes.quote.QuoteModel

class ShareDialogFragment(
    private val fileManager: FileManager,
    val quote: QuoteModel,
    private val bitmap: Bitmap
) : DialogFragment(R.layout.item_dialog_window) {

    private val binding: ItemDialogWindowBinding by viewBinding()

    private val shareImage = View.OnClickListener {
        val uri = download() ?: return@OnClickListener
        val shareIntent = fileManager.getSharedImageIntent(uri)
        startActivity(requireContext(), Intent.createChooser(shareIntent, null), null)
        FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.quote_shared))
        dismiss()
    }

    private val downloadImage = View.OnClickListener {
        download()
        dismiss()
    }

    private val copyText = View.OnClickListener {
        val clipboardManager = ContextCompat.getSystemService(
            requireContext(),
            ClipboardManager::class.java
        ) as ClipboardManager
        val data = ClipData.newPlainText("text", quote.text)
        clipboardManager.setPrimaryClip(data)
        Toast.makeText(requireContext(), requireContext().getString(R.string.text_copied), Toast.LENGTH_SHORT).show()
        FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.quote_copied))
        dismiss()
    }

    private val shareToInstagramStories = View.OnClickListener {
        val uri = download() ?: return@OnClickListener
        val shareIntent = fileManager.getSharedImageIntent(uri).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            `package` = "com.instagram.android"
        }
        requireActivity().grantUriPermission("com.instagram.android", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooserIntent = Intent.createChooser(shareIntent, null)
        requireActivity().startActivity(chooserIntent)
        FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.quote_shared_insta))
        dismiss()
    }

    private val setAsWallpaper = View.OnClickListener {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        wallpaperManager.setBitmap(bitmap)
        Toast.makeText(requireContext(), requireContext().getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show()
        FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.quote_set_wallpaper))
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireDialog().apply {
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            with(binding) {
                disableButton.setOnClickListener { dismiss() }

                shareButton.setOnClickListener(shareImage)
                saveButton.setOnClickListener(downloadImage)
                shareStoriesButton.setOnClickListener(shareToInstagramStories)
                copyTextButton.setOnClickListener(copyText)
                setWallpaperButton.setOnClickListener(setAsWallpaper)
            }
        }
    }

    private fun download(): Uri? {
        val uri = fileManager.saveImageWithMediaStore(requireContext(), bitmap, quote.id.toString())
        if (uri == null) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.share_error),
                Toast.LENGTH_SHORT

            ).show()
            dismiss()
        }
        return uri
    }
}
