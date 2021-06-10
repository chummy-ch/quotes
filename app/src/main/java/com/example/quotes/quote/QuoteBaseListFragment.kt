package com.example.quotes.quote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.view.drawToBitmap
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.example.quotes.FBAnalytics
import com.example.quotes.FileManager
import com.example.quotes.R
import com.example.quotes.databinding.FragmentQuoteListBinding
import com.example.quotes.dialogwindows.ShareDialogFragment
import com.example.quotes.epoxy.MvRxListBaseFragment
import com.example.quotes.settings.SettingsFragment

abstract class QuoteBaseListFragment(layout: Int) : MvRxListBaseFragment(layout) {

    protected lateinit var snapHelper: SnapHelper
    protected val binding: FragmentQuoteListBinding by viewBinding()
    protected lateinit var quoteBaseViewModel: QuoteBaseViewModel
    private val settingButtonListener = View.OnClickListener {
        val settingsFragment =
            parentFragmentManager.findFragmentByTag(SettingsFragment.FRAGMENT_TAG)
        if (settingsFragment == null) {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                addToBackStack(null)
                replace(R.id.fragment_container, SettingsFragment(), SettingsFragment.FRAGMENT_TAG)
            }
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        val visibilityTracker = EpoxyVisibilityTracker()
        visibilityTracker.attach(recyclerView)

        binding.favoriteButtonArea.setOnClickListener(getFavoriteButtonOnClick())
        binding.favoriteButton.setOnClickListener(getFavoriteButtonOnClick())

        binding.shareButtonArea.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                createDialog()
            }
        }
        binding.shareButton.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                createDialog()
            }
        }

        binding.settingsButtonArea.setOnClickListener(settingButtonListener)
        binding.settingsButton.setOnClickListener(settingButtonListener)

    }

    private suspend fun createDialog() {
        if (!checkPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) return
        val holder = getHolder() ?: return
        val quote = quoteBaseViewModel.getById(holder.itemId)
        val theme = quoteBaseViewModel.getCurrentTheme().toUri()
        val bitmap = createBitmapFromView(theme, quote.text, holder.itemView)

        val fileManager = FileManager()
        val dialog = ShareDialogFragment(fileManager, quote, bitmap)
        dialog.show(parentFragmentManager, null)
    }

    private suspend fun createBitmapFromView(theme: Uri, text: String, view: View): Bitmap {
        val bitmap = view.drawToBitmap()
        val canvas = Canvas(bitmap)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

        val scale = resources.displayMetrics.density
        val textS = 24 * scale
        val font = resources.getFont(quoteBaseViewModel.getFont())

        paint.apply {
            textSize = textS
            isAntiAlias = true
            setColor(Color.BLACK)
            style = Paint.Style.FILL
            typeface = Typeface.create(font, Typeface.NORMAL)
            textAlign = Paint.Align.LEFT
        }

        val waterMark = ResourcesCompat.getDrawable(resources, R.mipmap.ic_app, null)?.toBitmap() ?: ImageView(
            requireContext()
        ).apply { setImageResource(R.mipmap.ic_app) }.drawable.toBitmap()
        val xPoint = (canvas.width - waterMark.width).toFloat()
        val yPoint = (canvas.height - waterMark.height).toFloat()

        val margin = (2 * resources.getDimension(R.dimen.quote_text_margin) * scale).toInt()


        val loader = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data(theme)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as SuccessResult).drawable
        val backgroundBitmap = (result as BitmapDrawable).bitmap
        val backgroundScaledBitmap = Bitmap.createScaledBitmap(
            backgroundBitmap,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
            false
        )

        canvas.apply {
            drawBitmap(backgroundScaledBitmap, 0f, 0f, null)
            drawBitmap(waterMark, xPoint, yPoint, null)
            val textWidth = width - textS
            val textLayout =
                StaticLayout(
                    text,
                    paint,
                    textWidth.toInt() - margin,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    1.0f,
                    false
                )
            val textHeight = textLayout.height.toFloat()

            val x: Float = (bitmap.width - textWidth) / 2 + (margin / 2)
            val y: Float = (bitmap.height - textHeight) / 2

            save()
            translate(x, y)
            textLayout.draw(this)
            canvas.restore()
        }
        return bitmap
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            return true
        }
        requestForPermission.launch(permission)
        return false
    }

    private fun getHolder(): RecyclerView.ViewHolder? {
        val pos = getRecyclerViewCurrentPosition() ?: return null
        return recyclerView.findViewHolderForAdapterPosition(pos)
    }

    private fun getRecyclerViewCurrentPosition(): Int? {
        val manager = recyclerView.layoutManager!!
        return when (val center = snapHelper.findSnapView(manager)) {
            null -> null
            else -> manager.getPosition(center)
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

    protected fun bindButtonBackground(isFavorite: Boolean) {
        binding.favoriteButton.setBackgroundResource(
            when (isFavorite) {
                true -> R.drawable.ic_favorites_liked
                false -> R.drawable.ic_favorites_unliked
            }
        )
    }

    private fun getFavoriteButtonOnClick() = View.OnClickListener {
        val holder = getHolder() ?: return@OnClickListener
        val quoteId = holder.itemId
        val quote = quoteBaseViewModel.getById(quoteId)
        when (quote.isFavorite) {
            true -> {
                quoteBaseViewModel.removeFromFavorite(quote.id)
                FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.like))
            }
            false -> {
                quoteBaseViewModel.addToFavorite(quote.id)
                FBAnalytics.getSetContentAnalytics(quote.id, getString(R.string.unlike))
            }
        }
        //bindButtonBackground(quote.isFavorite)
    }
}
