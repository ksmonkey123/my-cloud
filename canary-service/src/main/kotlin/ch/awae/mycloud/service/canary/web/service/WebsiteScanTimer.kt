package ch.awae.mycloud.service.canary.web.service

import ch.awae.mycloud.auth.*
import ch.awae.mycloud.service.canary.web.model.*
import net.javacrumbs.shedlock.spring.annotation.*
import org.springframework.scheduling.annotation.*
import org.springframework.stereotype.*

@Component
class WebsiteScanTimer(
    private val monitoredSiteRepository: MonitoredSiteRepository,
    private val scanningService: ScanningService,
) {

    @SchedulerLock(name = "scan-timer")
    @Scheduled(cron = "\${canary.timer.web}")
    fun performScan() {
        AuthInfo.impersonate("scan-timer") {
            for (id in monitoredSiteRepository.findEnabledIds()) {
                scanningService.scan(id)
            }
        }
    }

}