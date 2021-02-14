package de.trbnb.darts.di

import androidx.databinding.ViewDataBinding
import de.trbnb.mvvmbase.MvvmBindingActivity
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.findGenericSuperclass

typealias HiltMvvmActivity<VM> = HiltMvvmBindingActivity<VM, ViewDataBinding>

abstract class HiltMvvmBindingActivity<VM, B> : MvvmBindingActivity<VM, B>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {

    constructor() : super()
    constructor(layoutId: Int) : super(layoutId)

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<HiltMvvmBindingActivity<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")
}