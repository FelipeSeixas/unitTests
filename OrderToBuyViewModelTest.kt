
import com..MainCoroutineRule
import com..database.SessionEntity
import com..model.domain.*
import com..repository.OrderToBuyRepository
import com..repository.SessionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class OrderToBuyViewModelTest{

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val mockSessionRepository = mockk<SessionRepository>(relaxed = true)
    private val mockOrderToBuyRepository = mockk<OrderToBuyRepository>(relaxed = true)
    private val mockSession = SessionEntity(IBM = "123")
    private val mockOrderToBuyViewModel = OrderToBuyViewModel(mockOrderToBuyRepository, mockSessionRepository)
    private val mockBuyOrderList = BuyOrderList(
        "123",
        "griesf",
        "rgeig",
        1,
        1,
        "ekrgbi")

    private val mockOrderItens = mutableListOf<OrderItems>()

    private val mockTracking = Tracking(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,

        )

    private val mockOrderToBuyDetail = BuyOrderDetail(
        orderNumber = "String?",
        dtCreated = "String?",
        creditStatusDesc = "String?",
        showTracking = true,
        orderAmount = null,
        orderAmountWithTax = null,
        vehiclePlate = "String?",
        base = "String?",
        reason = "String?",
        editUrl = "String?",
        driverName = "String?",
        lblEta = "String?",
        strDtEta = "String?",
        statusClass = "String?",
        creditStatusType = "String?",
        sapUnavailable = true,
        deliveryRangeStart = "String?",
        deliveryRangeEnd = "String?",
        deliverySuggestionStart = "String?",
        deliverySuggestionEnd = "String?",
        timeDeliveryMessage  = "String?",
        tracking= mockTracking,
        orderItems= mockOrderItens,
        creditBlockTypeId = null
    )

    @Test
    fun `getAllOrdersToBuy should get list of BuyOrderList`() = runTest {

        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }
        coEvery {
            mockOrderToBuyRepository.getAllOrdersToBuy()
        } answers {
            Result.Success(mutableListOf(mockBuyOrderList))
        }

        mockOrderToBuyViewModel.getAllOrdersToBuy()
        advanceUntilIdle()

        assert(mockOrderToBuyViewModel.apiStatus.value == ApiStatus.DONE)
        assert(mockOrderToBuyViewModel.ordersList.value == mutableListOf(mockBuyOrderList))
    }

    @Test
    fun `getAllOrdersToBuy should get Error`() = runTest {

        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }
        coEvery {
            mockOrderToBuyRepository.getAllOrdersToBuy()
        } answers {
            Result.Error(Exception())
        }

        mockOrderToBuyViewModel.getAllOrdersToBuy()
        advanceUntilIdle()

        assert(mockOrderToBuyViewModel.apiStatus.value == ApiStatus.ERROR)
    }

    @Test
    fun `getDetailOrders should get OrderDetail` () = runTest{

        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }
        coEvery {
            mockOrderToBuyRepository.getAllOrdersToBuyDetail(any(), any())

        } answers {
            Result.Success(mockOrderToBuyDetail)
        }

        mockOrderToBuyViewModel.getDetail("123456789", "3")
        advanceUntilIdle()

        assert(mockOrderToBuyViewModel.orderDetail.value  == mockOrderToBuyDetail)
    }

    @Test
    fun `getDetailOrders should get Error` () = runTest{

        coEvery {
            mockSessionRepository.currentSession
        } answers {
            flowOf(mockSession)
        }
        coEvery {
            mockOrderToBuyRepository.getAllOrdersToBuyDetail(any(), any())

        } answers {
            Result.Error(Exception())
        }

        mockOrderToBuyViewModel.getDetail("123456789", "3")
        advanceUntilIdle()

        assert(mockOrderToBuyViewModel.apiStatusDetail.value  == ApiStatus.ERROR)
    }
}
