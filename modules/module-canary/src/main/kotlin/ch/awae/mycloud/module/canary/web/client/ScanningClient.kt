package ch.awae.mycloud.module.canary.web.client

interface ScanningClient {

    fun performScan(url: String, tests: Set<String>): ScanResult

}

sealed interface ScanResult {
    object Success : ScanResult
    data class Error(val throwable: Throwable) : ScanResult
    data class Failure(val failedTests: Set<String>) : ScanResult
}