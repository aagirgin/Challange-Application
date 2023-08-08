package com.example.challapp.ui.feed.completedchallenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.challapp.databinding.SheetDailyquestiondisplayBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class QuestionTaskSheet(private val documentId: String):BottomSheetDialogFragment() {

    private lateinit var binding: SheetDailyquestiondisplayBinding
    private val completedChallangeViewModel: CompletedChallengeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SheetDailyquestiondisplayBinding.inflate(inflater,container,false)
        viewLifecycleOwner.lifecycleScope.launch {
            completedChallangeViewModel.getDailyQuestion(documentId)
            completedChallangeViewModel.getSpecificDailyQuestion.collect{
                binding.textviewDailychallangeQuestion.text = it?.dailyQuestion
                binding.textviewDailychallangeName.text = it?.dailyQuestionName!!
            }
        }

        return binding.root
    }

}