package de.trbnb.darts.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.OnColorSelectionListener

class DatabindingHslColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HSLColorPicker(context, attrs, defStyleAttr) {
    var actualColor: Int = 0
        private set

    private var _colorSelectionListener: OnColorSelectionListener? = null

    init {
        super.setColorSelectionListener(object : OnColorSelectionListener {
            override fun onColorSelected(color: Int) {
                actualColor = color
                _colorSelectionListener?.onColorSelected(color)
            }

            override fun onColorSelectionEnd(color: Int) {
                _colorSelectionListener?.onColorSelectionEnd(color)
            }

            override fun onColorSelectionStart(color: Int) {
                _colorSelectionListener?.onColorSelectionStart(color)
            }
        })
    }

    override fun setColorSelectionListener(listener: OnColorSelectionListener) {
        _colorSelectionListener = listener
    }
}

@get:ColorInt
@get:InverseBindingAdapter(attribute = "color")
@set:BindingAdapter("color")
var DatabindingHslColorPicker.colorDataBinding: Int
    set(value) { setColor(value) }
    get() = actualColor

@BindingAdapter("colorAttrChanged")
fun DatabindingHslColorPicker.colorListener(inverseBindingListener: InverseBindingListener?) {
    inverseBindingListener ?: return
    setColorSelectionListener(object : OnColorSelectionListener {
        override fun onColorSelected(color: Int) {
            inverseBindingListener.onChange()
        }

        override fun onColorSelectionEnd(color: Int) {}
        override fun onColorSelectionStart(color: Int) {}
    })
}