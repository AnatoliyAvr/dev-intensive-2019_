package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx

class AvatarImageView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

  companion object {
    private const val DEFAULT_BORDER_WIDTH = 2F
    private const val DEFAULT_BORDER_COLOR = Color.WHITE
    private const val DEFAULT_SIZE = 40F
  }

  @Px
  var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
  @ColorInt
  private var borderColor: Int = Color.WHITE
  var initials: String = "??"
    set(value) {
      field = value
    }

  private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val viewRect = Rect()
  private lateinit var resultBm: Bitmap
  private lateinit var maskBm: Bitmap
  private lateinit var srcBm: Bitmap

  init {
    if (attrs != null) {
      val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageViewMask)
      borderWidth = ta.getDimension(
        R.styleable.AvatarImageViewMask_aiv_borderWidth,
        context.dpToPx(DEFAULT_BORDER_WIDTH)
      )

      borderColor =
        ta.getColor(R.styleable.AvatarImageViewMask_aiv_borderColor, DEFAULT_BORDER_COLOR)
      initials = ta.getString(R.styleable.AvatarImageViewMask_aiv_initials) ?: "??"

      scaleType = ScaleType.CENTER_CROP
      setup()

      setOnLongClickListener {
        handleLongClick()
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    Log.d("M_AvatarImageViewMask", "onAttachedToWindow")
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    Log.d(
      "M_AvatarImageViewMask", """
      onMeasure
      width: ${MeasureSpec.toString(widthMeasureSpec)}
      height: ${MeasureSpec.toString(heightMeasureSpec)}
    """.trimIndent()
    )

    val initSize = resolveDefaultSize(widthMeasureSpec)
    setMeasuredDimension(initSize, initSize)
    Log.d("M_AvatarImageViewMask", "onMeasure after set size: $measuredWidth $measuredHeight")
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    Log.d("M_AvatarImageViewMask", "onSizeChanged")
    if (w == 0) return
    with(viewRect) {
      left = 0
      top = 0
      right = w
      bottom = h
    }

    prepareBitmaps(w, h)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    Log.d("M_AvatarImageViewMask", "onLayout")
  }

  override fun onDraw(canvas: Canvas) {
//    super.onDraw(canvas)
    Log.d("M_AvatarImageViewMask", "onDraw")
    //NOT allocate, ONLY draw

    canvas.drawBitmap(resultBm, viewRect, viewRect, null)
    //resize rect
    val half = (borderWidth / 2).toInt()
    viewRect.inset(half, half)
    canvas.drawOval(viewRect.toRectF(), borderPaint)
  }

  private fun setup() {
    with(maskPaint) {
      color = Color.RED
      style = Paint.Style.FILL
    }

    with(borderPaint) {
      style = Paint.Style.STROKE
      strokeWidth = borderWidth
      color = borderColor
    }
  }

  private fun prepareBitmaps(w: Int, h: Int) {
    //prepare buffer this

    maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
    resultBm = maskBm.copy(Bitmap.Config.ARGB_8888, true)

    val maskCanvas = Canvas(maskBm)
    maskCanvas.drawOval(viewRect.toRectF(), maskPaint)
    maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

    val resultCanvas = Canvas(resultBm)

    resultCanvas.drawBitmap(maskBm, viewRect, viewRect, null)
    resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)
  }

  private fun resolveDefaultSize(spec: Int): Int {
    return when (MeasureSpec.getMode(spec)) {
      MeasureSpec.UNSPECIFIED -> {
        context.dpToPx(DEFAULT_SIZE).toInt() //resolveDefaultSize()
      }
      MeasureSpec.AT_MOST -> {
        MeasureSpec.getSize(spec) //from spec
      }
      MeasureSpec.EXACTLY -> {
        MeasureSpec.getSize(spec) //from spec
      }
      else -> MeasureSpec.getSize(spec) //from spec
    }
  }

  private fun handleLongClick(): Boolean {
    srcBm != srcBm
    Log.d("M_AvatarImageViewMask", "handleLongClick")
    return true
  }
}