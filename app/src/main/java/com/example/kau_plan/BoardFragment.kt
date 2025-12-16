package com.example.kau_plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

class BoardFragment : Fragment() {
    // 주의: Fragment(...) 괄호 안에 R.layout.xxx를 넣지 마세요!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // XML 파일(fragment_board.xml)을 찾지 않고,
        // 여기서 코틀린 코드로 바로 화면(ComposeView)을 생성해서 반환합니다.
        return ComposeView(requireContext()).apply {
            // 뷰가 사라질 때 컴포즈도 같이 정리되도록 설정 (메모리 관리)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                // ReservationScreen.kt에 있는 함수를 여기서 실행!
                ReservationScreen()
            }
        }
    }
}