package com.######.view.calls

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.########.MainCoroutineRule
import com.########.database.ISessionManager
import com.########.getOrAwaitValue
import com.########.model.domain.*
import com.########.model.domain.Call
import com.########.repository.CallsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CallsListViewModelTest {

    private lateinit var callsListViewModel: CallsListViewModel

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // fake dependencies for the view model
    private val callsRepositoryMock = mockk<CallsRepository>(relaxed = true)

    private val mockSuccessCallList = listOf(
        Call(
            "000000000000",
            "12/01/2022",
            "Problemas para adicionar unidades adbjab bdjabdjab dadada",
            "aberto",
            "",
            source = CallSource.EQ
        ),
        Call(
            "11111111111",
            "17/01/2022",
            "Recebimento de produto",
            "aberto",
            "",
            source = CallSource.EQ
        ),
        Call(
            "222222222222",
            "22/01/2022",
            "Elogio para o aplicativo",
            "aberto",
            "",
            source = CallSource.EQ
        ),
        Call(
            "333333333333",
            "27/01/2022",
            "Problemas para adicionar unidades",
            "aberto",
            "",
            source = CallSource.EQ
        )
    )

    private val callsPermission = Permission(
        name = "HOMEChamados",
        code = "HOMEChamados",
        isSubMenu = false,
        option = null,
        permissionType = PermissionType.HOME_CARD
    )

    private val eqPermission = Permission(
        name = "HOMEChamados.EQ",
        code = "HOMEChamados.EQ",
        isSubMenu = true,
        option = null
    )
    private val sfPermission = Permission(
        name = "HOMEChamados.SF",
        code = "HOMEChamados.SF",
        isSubMenu = true,
        option = null
    )

    private val mockCallCardList = mockSuccessCallList.map {
        CallCard(
            call = it
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        callsListViewModel =
            CallsListViewModel(callsRepositoryMock, StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun gatAllCalls_successfullyGetCalls_hasCallsList() = runTest {
        //Given
        val callCategory = CallCategory.IN_PROGRESS
        coEvery {
            callsRepositoryMock.getAllCalls(any())
        } coAnswers {
            Result.Success(
                mockSuccessCallList
            )
        }
        assertThat(callsListViewModel.callsList.getOrAwaitValue()).isNull()
        callsListViewModel.updateCallCategory(callCategory)

        //When
        callsListViewModel.getAllCalls()
        runCurrent()
        //Then
        assertThat(callsListViewModel.apiStatus.getOrAwaitValue()).isEqualTo(ApiStatus.DONE)
        assertThat(callsListViewModel.callsList.getOrAwaitValue()?.map {
            it.call
        }).containsExactlyElementsIn(mockSuccessCallList)
    }

    @Test
    fun gatAllCalls_errorGettingCalls_apiStatusError() = runTest {
        //Given
        val callCategory = CallCategory.IN_PROGRESS
        coEvery {
            callsRepositoryMock.getAllCalls(any())
        } coAnswers {
            Result.Error(
                Exception("api error")
            )
        }
        assertThat(callsListViewModel.callsList.getOrAwaitValue()).isNull()
        callsListViewModel.updateCallCategory(callCategory)

        //When
        callsListViewModel.getAllCalls()
        runCurrent()

        //Then
        assertThat(callsListViewModel.apiStatus.getOrAwaitValue()).isEqualTo(ApiStatus.ERROR)
        assertThat(callsListViewModel.callsList.getOrAwaitValue()).isNull()
    }

    @Test
    fun getCallDetail_successfullyGetsDetail_apiStatusDone() = runTest {
        // Given
        coEvery {
            callsRepositoryMock.getCallDetail(any(), any())
        } coAnswers {
            Result.Success(
                mockSuccessCallList[0]
            )
        }
        callsListViewModel._callsList.postValue(mockCallCardList)
        assertThat(callsListViewModel.callsList.getOrAwaitValue()?.get(0)?.apiStatus).isNull()

        // When
        callsListViewModel.getCallDetail(0)
        runCurrent()

        //Then
        assertThat(callsListViewModel.callsList.getOrAwaitValue()?.get(0)?.apiStatus).isEqualTo(
            ApiStatus.DONE
        )
    }

    @Test
    fun getCallDetail_alreadyHasDetail_noRequest() {
        //Given
        callsListViewModel._callsList.postValue(mockCallCardList.apply {
            this[0].apiStatus = ApiStatus.DONE
        })

        //When
        callsListViewModel.getCallDetail(0)

        //Then
        coVerify { callsRepositoryMock.getCallDetail(any(), any()) wasNot Called }
    }

    @Test
    fun getCallDetail_errorGettingDetail_apiStatusError() = runTest {
        // Given
        coEvery {
            callsRepositoryMock.getCallDetail(any(), any())
        } coAnswers {
            Result.Error(
                Exception("api error")
            )
        }
        callsListViewModel._callsList.postValue(mockCallCardList)
        assertThat(callsListViewModel.callsList.getOrAwaitValue()?.get(0)?.apiStatus).isNull()

        // When
        callsListViewModel.getCallDetail(0)
        runCurrent()

        //Then
        assertThat(callsListViewModel.callsList.getOrAwaitValue()?.get(0)?.apiStatus).isEqualTo(
            ApiStatus.ERROR
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getgetCallOpenInfo_errorGettingDetail_apiStatusError() = runTest {
        // Given
        coEvery {
            callsRepositoryMock.getCallOpening(any())
        } coAnswers {
            Result.Error(
                Exception("api error")
            )
        }

        // When
        callsListViewModel.getCallOpenInfo()
        runCurrent()

        //Then
        assertThat(callsListViewModel.callsOpenStatus.value).isEqualTo(
            ApiStatus.ERROR
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getgetCallOpenInfo_successGet_apiStatusDone() = runTest {
        // Given
        coEvery {
            callsRepositoryMock.getCallOpening(any())
        } coAnswers {
            Result.Success(
                CallOpeningInfo(
                    "123",
                    "29/08/2022",
                    "teste",
                    "teste@teste.com",
                    "123456",
                    "123456",
                    "teste",
                    "123456",
                    true
                )
            )
        }

        callsListViewModel._ticket.postValue(
            CallCard(
                call =
                Call("", "", "", "", "123")
            )
        )
        // When
        callsListViewModel.getCallOpenInfo()

        runCurrent()

        //Then
        assertThat(callsListViewModel.callsOpenStatus.value).isEqualTo(
            ApiStatus.DONE
        )
    }

    @Test
    fun `putCallComments should return API status DONE`() = runTest{
//        Arrange - Configuração inicial e envio do dados ao view model
        var callResult = CallComentsReturn("7777777", "05/10/2022", true, false)

        coEvery {
               callsRepositoryMock.putCallComents(any(), any(), any())
        }answers {
            com.raizen.csonline.model.domain.Result.Success(callResult)
        }

        callsListViewModel = CallsListViewModel(callsRepositoryMock, StandardTestDispatcher())
        callsListViewModel._ticket.value = CallCard(call =
            Call("", "", "", "", "123")
        )
//        Act - Chamada da função (assíncrona) que será testada
        callsListViewModel.putInfoPendentCall("Operador", "Comentário inicial")

        advanceUntilIdle()

//        Asset - Verificação conforme o resultado esperado, conforme configuração do Arrange e Act
        assert(
            callsListViewModel.callsOpenStatus.value == ApiStatus.DONE
        )
    }

    @Test
    fun `putCallComments should return API status ERROR`() = runTest {
//        Arrange - Configuração inicial e envio do dados ao view model
        var callResult = CallComentsReturn("7777777", "05/10/2022", true, false)

        coEvery {
            callsRepositoryMock.putCallComents(any(), any(), any())
        } answers {
            com.raizen.csonline.model.domain.Result.Error(Exception())
        }

        callsListViewModel = CallsListViewModel(callsRepositoryMock, StandardTestDispatcher())
        callsListViewModel._ticket.value = CallCard(
            call =
            Call("", "", "", "", "123")
        )
//        Act - Chamada da função (assíncrona) que será testada
        callsListViewModel.putInfoPendentCall("Operador", "Comentário inicial")

        advanceUntilIdle()

//        Asset - Verificação conforme o resultado esperado, conforme configuração do Arrange e Act
        assert(
            callsListViewModel.callsOpenStatus.value == ApiStatus.ERROR
        )
    }

}
