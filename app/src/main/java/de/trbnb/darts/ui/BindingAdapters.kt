package de.trbnb.darts.ui

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.TriangleEdgeTreatment
import com.google.android.material.slider.Slider
import de.trbnb.darts.R
import de.trbnb.darts.models.InOutRule

@BindingAdapter("invisible")
fun View.setInvisible(invisible: Boolean) { isInvisible = invisible }

@BindingAdapter("strikethrough")
fun TextView.setStrikethrough(strikethrough: Boolean) {
    paintFlags = when {
        strikethrough -> paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else -> paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}

@BindingAdapter("backgroundTintRes")
fun View.setBackgroundTintRes(@ColorRes colorRes: Int) {
    backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
}

@BindingAdapter("backgroundRes")
fun View.setBackgroundRes(@ColorRes colorRes: Int?) {
    setBackgroundResource(colorRes ?: return)
}

@BindingAdapter("textColorRes")
fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(context.getColorStateList(colorRes))
}

@BindingAdapter("bold")
fun TextView.setBold(bold: Boolean) {
    if (bold) {
        setTypeface(typeface, Typeface.BOLD)
    } else {
        typeface = Typeface.create(typeface, Typeface.NORMAL)
    }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@BindingAdapter("android:selected")
fun View.setSelected(selected: Boolean) {
    this.isSelected = selected
}

@BindingAdapter("specialShape")
fun MaterialButton.setSpecialShape(specialShape: Boolean) {
    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
        .setAllCorners(if (specialShape) CutCornerTreatment() else RoundedCornerTreatment())
        .run {
            if (specialShape) {
                setAllEdges(TriangleEdgeTreatment(8f, true))
            } else this
        }.build()
}

@BindingAdapter("color")
fun ImageView.setColor(@ColorInt color: Int) {
    setImageDrawable(ColorDrawable(color))
}

@set:BindingAdapter("value")
@get:InverseBindingAdapter(attribute = "value")
var Slider.dataBindingValue: Int
    get() = value.toInt()
    set(value) { this.value = value.toFloat() }

@BindingAdapter("valueAttrChanged")
fun Slider.dataBindingValueListener(inverseBindingListener: InverseBindingListener?) {
    addOnChangeListener(inverseBindingListener?.let {
        Slider.OnChangeListener { _, _, _ -> inverseBindingListener.onChange() }
    })
}

@set:BindingAdapter("inRule")
@get:InverseBindingAdapter(attribute = "inRule", event = "ruleAttrChanged")
var ChipGroup.inRule: InOutRule
    get() = when (checkedChipId) {
        R.id.in_double -> InOutRule.DOUBLE
        R.id.in_master -> InOutRule.MASTER
        R.id.in_triple -> InOutRule.TRIPLE
        else -> InOutRule.STRAIGHT
    }
    set(value) {
        findViewById<Chip>(when (value) {
            InOutRule.STRAIGHT -> R.id.in_straight
            InOutRule.DOUBLE -> R.id.in_double
            InOutRule.MASTER -> R.id.in_master
            InOutRule.TRIPLE -> R.id.in_triple
        }).isChecked = true
    }

@set:BindingAdapter("outRule")
@get:InverseBindingAdapter(attribute = "outRule", event = "ruleAttrChanged")
var ChipGroup.outRule: InOutRule
    get() = when (checkedChipId) {
        R.id.out_double -> InOutRule.DOUBLE
        R.id.out_master -> InOutRule.MASTER
        R.id.out_triple -> InOutRule.TRIPLE
        else -> InOutRule.STRAIGHT
    }
    set(value) {
        findViewById<Chip>(when (value) {
            InOutRule.STRAIGHT -> R.id.out_straight
            InOutRule.DOUBLE -> R.id.out_double
            InOutRule.MASTER -> R.id.out_master
            InOutRule.TRIPLE -> R.id.out_triple
        }).isChecked = true
    }

@BindingAdapter("ruleAttrChanged")
fun ChipGroup.setDataBindingListener(inverseBindingListener: InverseBindingListener?) {
    setOnCheckedChangeListener(inverseBindingListener?.let {
        ChipGroup.OnCheckedChangeListener { _, _ -> it.onChange() }
    })
}

@BindingAdapter("show")
fun FloatingActionButton.setShow(show: Boolean) {
    animate()
        .translationX(if (show) {
            0f
        } else {
            val (marginStart, marginEnd) = (layoutParams as? ViewGroup.MarginLayoutParams)?.run { marginStart to marginEnd } ?: 0 to 0
            (measuredWidth + marginStart + marginEnd).toFloat()
        })
        .setDuration(500)
        .start()
}

@BindingAdapter("tintRes")
fun ImageView.setTintRes(@ColorRes res: Int) {
    imageTintList = context.getColorStateList(res)
}