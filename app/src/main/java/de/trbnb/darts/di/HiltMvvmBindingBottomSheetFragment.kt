package de.trbnb.darts.di

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelLazy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.trbnb.mvvmbase.MvvmView
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.ViewModelPropertyChangedCallback
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.findGenericSuperclass

abstract class HiltMvvmBindingBottomSheetFragment<VM, B> : MvvmBindingBottomSheetFragment<VM, B>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {

    constructor() : super()
    constructor(layoutId: Int) : super(layoutId)

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<HiltMvvmBindingBottomSheetFragment<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")
}

abstract class MvvmBindingBottomSheetFragment<VM, B>(@LayoutRes override val layoutId: Int = 0) : BottomSheetDialogFragment(), MvvmView<VM, B>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    override var binding: B? = null

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    @Suppress("LeakingThis")
    private val viewModelObserver = ViewModelPropertyChangedCallback(this)

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBindingBottomSheetFragment<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory }
    )

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener = { event: Event -> onEvent(event) }

    /**
     * Called by the lifecycle.
     *
     * Creates the [ViewDataBinding].
     */
    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return initBinding(inflater, container).also {
            it.lifecycleOwner = viewLifecycleOwner
            binding = it
        }.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.setVariable(viewModelBindingId, viewModel)
        viewModel.onBind()
        binding?.let(this::onBindingCreated)
        onViewModelLoaded(viewModel)
    }

    protected open fun onBindingCreated(binding: B) { }

    /**
     * Creates a new [ViewDataBinding].
     *
     * @return The new [ViewDataBinding] instance that fits this Fragment.
     */
    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B {
        return DataBindingUtil.inflate(inflater, layoutId, container, false)
    }

    @CallSuper
    override fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.eventChannel.addListener(eventListener)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    @Suppress("EmptyFunctionBlock")
    override fun onEvent(event: Event) { }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.setVariable(viewModelBindingId, null)
        viewModel.onUnbind()
        viewModel.eventChannel.removeListener(eventListener)
        viewModel.removeOnPropertyChangedCallback(viewModelObserver)

        binding = null
    }
}