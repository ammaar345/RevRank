package com.revrank.domain.usecase

import com.revrank.data.repository.AuthRepository
import com.revrank.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
