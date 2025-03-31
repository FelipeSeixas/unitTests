package com.#####pository

import com.########.database.SessionEntity
import com.########.di.CrashReporterProvider
import com.########.model.domain.*
import com.########.network.CSOApi
import com.########.network.dto.*
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class CallsRepositoryTest {
    private val mockSessionRepository = mockk<SessionRepository>(relaxed = true)
    private val mockCrashReporter = mockk<CrashReporterProvider>(relaxed = true)

    private val mockPermissionSFEQ = mutableListOf(
        Permission("EQ", "EQ", false, mutableListOf(), null),
        Permission("SF", "SF", false, mutableListOf(), null)
    )

    private val mockPermission = Permission(
        HomeCardOption.CALLS.name,
        HomeCardOption.CALLS.code,
        false,
        mockPermissionSFEQ,
        null
    )

    private val mockListPermission = listOf(mockPermission)

    //"yyyy-MM-dd'T'HH:mm:ss"
    private val mockCallDTOOpen = CallsDTO(
        "123",
        "123",
        "123",
        false,
        "2022-09-15T19:30:51Z",
        "2022-09-15T19:30:51Z",
        "123",
        false,
        "123",
        "123",
        1,
        "123",
        "123",
        "Cliente",
        "123",
        "SalesForce",
    )

    private val mockCallDTOListOpen = listOf(mockCallDTOOpen)

    private val mockCallDTOClosed = CallsDTO(
        "123",
        "123",
        "123",
        false,
        "2022-09-16T19:30:51Z",
        "2022-09-16T19:30:51Z",
        "123",
        false,
        "123",
        "123",
        1,
        "123",
        "123",
        "Cliente",
        "123",
        "SalesForce",
    )
    private val mockCallDTOListClosed = listOf(mockCallDTOOpen)

    private val mockCallSFDTO = CallsSFDTO(
        mockCallDTOListOpen,
        mockCallDTOListClosed
    )

    private val mockCallPage = CallsPage(
        true,
        CallsPageBody(
            mockCallDTOListClosed,
            true,
            1,
            15,
            10,
            150,
            null
        )
    )

    private val mockCallSFDTOList = listOf(mockCallSFDTO)

    private val mockCallListOpen = listOf(
        mockCallDTOOpen.asDomainModel().apply {
            source = CallSource.EQ
            status = "em atendimento"
        },
        mockCallDTOOpen.asDomainModel().apply {
            source = CallSource.SF
        },
    )

    private val mockCallListClosed = listOf(
        mockCallDTOOpen.asDomainModel().apply {
            source = CallSource.EQ
            status = "concluído"
        },
        mockCallDTOOpen.asDomainModel().apply {
            source = CallSource.SF
        },
    )

    private val mockSession = SessionEntity(IBM = "123", permission = mockListPermission)

    private val mockCsoApi = mockk<CSOApi>(relaxed = true)

    private val callRepository = CallsRepository(mockSessionRepository, mockCrashReporter)

    @Before
    fun setup() {
        coEvery {
            mockCsoApi.calls.getCallDetailEQ(any())
        } answers {
            Response.success(200, mockCallDTOOpen)
        }

        coEvery {
            mockCsoApi.calls.getCallDetailSF(any())
        } answers {
            Response.success(200, mockCallDTOOpen)
        }

        coEvery {
            mockCsoApi.calls.getCallsEQ(any(), any(), any())
        } answers {
            Response.success(200, mockCallDTOListOpen)
        }

        coEvery {
            mockCsoApi.calls.getCallsSF(any())
        } answers {
            Response.success(200, mockCallSFDTO)
        }
        coEvery {
            mockCsoApi.calls.getCallsSFPage(any())
        } answers {
            Response.success(200, mockCallPage)
        }

        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }

        coEvery {
            mockCrashReporter.recordException(any())
        } answers { }
        callRepository.csoApi = mockCsoApi
    }

    @After
    fun tearDown() {
        coEvery {
            mockCrashReporter.recordException(any())
        } answers { }
        clearAllMocks()
    }

    @Test
    fun `getAllCalls should list of Calls for open calls`() = runTest {
        val call = callRepository.getAllCalls(CallCategory.IN_PROGRESS)

        assert(
            when (call) {
                is Result.Success -> {
                    mockCallListOpen == call.data
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `getAllCalls should list of Calls for closed calls`() = runTest {
        val call = callRepository.getAllCalls(CallCategory.CLOSED)

        assert(
            when (call) {
                is Result.Success -> {
                    mockCallListClosed.filter { it.source == CallSource.SF } == call.data
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `putCallComents should return Result CallComentsReturn`() = runTest {
//Arrange - Preparando a estrutura do teste -> 189
        val callReturn = CallComentsReturn("123456", "04/10/2022", true, false)

        coEvery {
            mockCsoApi.calls.putInfoPend(any(), any())
        } answers {
            Response.success(callReturn)
        }
        callRepository.csoApi = mockCsoApi

//Act - Chamada do método com os dados simulados
        val repositoryReturn =
            callRepository.putCallComents("555555555", "nome", "Comentário padrão")

//Assert - Verificação dos dados
        assert(
            when (repositoryReturn) {
                is Result.Success -> {
                    callReturn == repositoryReturn.data
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `putCallComents should return Error`() = runTest {
//Arrange - Preparando a estrutura do teste -> 189
        val callReturn = CallComentsReturn("123456", "04/10/2022", true, false)

        coEvery {
            mockCsoApi.calls.putInfoPend(any(), any())
        } answers {
            Response.error<CallComentsReturn>(401, null)
        }
        callRepository.csoApi = mockCsoApi

//Act - Chamada do método com os dados simulados
        val repositoryReturn =
            callRepository.putCallComents("555555555", "nome", "Comentário padrão")

//Assert - Verificação dos dados
        assert(
            when (repositoryReturn) {
                is Result.Error -> {
                    true
                }
                else -> {
                    false
                }
            }
        )
    }
}
