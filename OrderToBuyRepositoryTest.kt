package ****************

import com.########
import com.########
import com.########
import com.########
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class OrderToBuyRepositoryTest{

    private val mockSessionRepository = mockk<SessionRepository>(relaxed = true)
    private val mockCrashReporter = mockk<CrashReporterProvider>(relaxed = true)
    private val mockSession = SessionEntity(IBM = "123")
    private val mockCsoApi = mockk<###Api>(relaxed = true)

    private val mockBuyOrderList = BuyOrderList(
        "123",
        "griesf",
        "rgeig",
        1,
        1,
        "ekrgbi"
    )

    private val mockTracking = Tracking(
        null,
        null,
        null,
        null,
        "ufigef",
        "uegfue",
        "ueigf",
        "urgbie",
        "uergbiu",
        "123",
        "gre2e34",
        "11/11/11"
    )

    private val mockBuyOrderDetail = BuyOrderDetail(
        "123",
        "11/11/11",
        "uygefewgfwei",
        true,
        12.0,
        12.0,
        "gre2e34",
        "vfewfkew",
        "hbrgiweubg",
        "http://",
        "hfuahef",
        "12 days",
        "3 days",
        "hgrbfiewug",
        "iuegfiuw",
        false,
        "3627",
        "7263",
        "4352",
        "6347",
        "grhyfgeurg",
        mockTracking,
        listOf(),
        null
    )

    private val orderToBuyRepository = OrderToBuyRepository(mockSessionRepository, mockCrashReporter)

    @Before
    fun setup() {
        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllOrdersToBuy should return list of BuyOrderList`() = runTest {

        coEvery {
            mockCsoApi.orderToBuy.getOrdersToBuy(any())
        } answers {
            Response.success(listOf(mockBuyOrderList))
        }

        orderToBuyRepository.csoApi = mockCsoApi

        val orderList = orderToBuyRepository.getAllOrdersToBuy()

        coVerify {
            orderToBuyRepository.csoApi.orderToBuy.getOrdersToBuy(any())
        }

        assert(
            when (orderList) {
                is Result.Success -> {
                    listOf(mockBuyOrderList) == orderList.data
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `getAllOrdersToBuy should return Error`() = runTest {
        tearDown()

        coEvery {
            mockCsoApi.orderToBuy.getOrdersToBuy(any())
        } answers {
            Response.error(400, null)
        }

        orderToBuyRepository.csoApi = mockCsoApi

        val orderList = orderToBuyRepository.getAllOrdersToBuy()

        coVerify {
            mockCrashReporter.recordException(any())
        }

        assert(
            when (orderList) {
                is Result.Error -> {
                    true
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `getAllOrdersToBuyDetail should return BuyOrderDetail`() = runTest {

        coEvery {
            mockCsoApi.orderToBuy.getOrdersToBuyDetail(
                "123",
                any(),
                any(),
                any(),
                "321",
                "123"
            )
        } answers {
            Response.success(mockBuyOrderDetail)
        }

        orderToBuyRepository.csoApi = mockCsoApi

        val orderDetail = orderToBuyRepository
            .getAllOrdersToBuyDetail("123","321")

        coVerify {
            orderToBuyRepository.csoApi.orderToBuy.getOrdersToBuyDetail(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }

        assert(
            when (orderDetail) {
                is Result.Success -> {
                    mockBuyOrderDetail == orderDetail.data
                }
                else -> {
                    false
                }
            }
        )
    }

    @Test
    fun `getAllOrdersToBuyDetail should return Error`() = runTest {
        tearDown()

        coEvery {
            mockCsoApi.orderToBuy.getOrdersToBuyDetail(
                "123",
                any(),
                any(),
                any(),
                "321",
                "123"
            )
        } answers {
            Response.error(400, null)
        }

        orderToBuyRepository.csoApi = mockCsoApi

        val orderDetail = orderToBuyRepository
            .getAllOrdersToBuyDetail("123","321")

        coVerify {
            mockCrashReporter.recordException(any())
        }

        assert(
            when (orderDetail) {
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
