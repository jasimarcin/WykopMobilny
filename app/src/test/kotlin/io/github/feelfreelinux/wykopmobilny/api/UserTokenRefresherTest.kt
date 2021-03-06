package io.github.feelfreelinux.wykopmobilny.api

import com.nhaarman.mockito_kotlin.*
import io.github.feelfreelinux.wykopmobilny.api.mywykop.MyWykopApi
import io.github.feelfreelinux.wykopmobilny.api.user.UserApi
import io.github.feelfreelinux.wykopmobilny.models.pojo.NotificationCountResponse
import io.github.feelfreelinux.wykopmobilny.utils.usermanager.UserManagerApi
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserTokenRefresherTest {
    lateinit var systemUnderTest : UserTokenRefresher
    val mockOfUserApi = mock<UserApi>()
    val mockOfMyWykopApi = mock<MyWykopApi>()
    val mockOfUserManager = mock<UserManagerApi>()

    @Before
    fun setup() {
        systemUnderTest = UserTokenRefresher(mockOfUserApi, mockOfUserManager)
    }

    @Test
    fun shouldNotInterceptSuccess() {
        val response = NotificationCountResponse(15)
        whenever(mockOfMyWykopApi.getNotificationCount()).thenReturn(Single.just(response))
        val testObserver = TestObserver<NotificationCountResponse>()
        mockOfMyWykopApi
                .getNotificationCount()
                .retryWhen(systemUnderTest)
                .subscribe(testObserver)

        testObserver.assertNoErrors()
        testObserver.assertComplete()
        testObserver.assertResult(response)
    }

    @Test
    fun shouldNotInterceptEveryException() {
        val exception = IOException()
        whenever(mockOfMyWykopApi.getNotificationCount()).thenReturn(Single.error(exception))
        val testObserver = TestObserver<NotificationCountResponse>()
        mockOfMyWykopApi
                .getNotificationCount()
                .retryWhen(systemUnderTest)
                .subscribe(testObserver)

        testObserver.assertError(exception)
        verifyZeroInteractions(mockOfUserApi)
        verifyZeroInteractions(mockOfUserManager)
    }

    @Test
    fun shouldNotInterceptEveryApiException() {
        val exception = WykopRequestBodyConverterFactory.ApiException("Test", 16)
        whenever(mockOfMyWykopApi.getNotificationCount()).thenReturn(Single.error(exception))
        val testObserver = TestObserver<NotificationCountResponse>()
        mockOfMyWykopApi
                .getNotificationCount()
                .retryWhen(systemUnderTest)
                .subscribe(testObserver)

        testObserver.assertError(exception)
        verifyZeroInteractions(mockOfUserApi)
        verifyZeroInteractions(mockOfUserManager)
    }
}