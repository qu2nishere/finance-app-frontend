package com.quan.finance_app

import android.app.Application
import com.quan.finance_app.data.ServiceLocator

/**
 * Chạy trước mọi Activity. Nơi khởi tạo những thứ sống suốt vòng đời app.
 * Phải khai báo trong AndroidManifest: android:name=".FinanceApp"
 */
class FinanceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
